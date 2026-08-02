package com.sms.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lightweight in-memory token-bucket: maxRequests / windowSeconds per client IP.
 * Production deployments should switch to a Redis-backed limiter.
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Value("${sms.gateway.rate-limit.max-requests:200}") private int maxRequests;
    @Value("${sms.gateway.rate-limit.window-seconds:60}") private int windowSeconds;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = exchange.getRequest().getRemoteAddress() == null
                ? "unknown" : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        long now = Instant.now().getEpochSecond();
        Bucket b = buckets.computeIfAbsent(ip, k -> new Bucket(now));
        synchronized (b) {
            if (now - b.windowStart >= windowSeconds) { b.windowStart = now; b.count.set(0); }
            int c = b.count.incrementAndGet();
            if (c > maxRequests) {
                ServerHttpResponse res = exchange.getResponse();
                res.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                res.getHeaders().add("Retry-After", String.valueOf(windowSeconds - (now - b.windowStart)));
                return res.setComplete();
            }
        }
        return chain.filter(exchange);
    }
    @Override public int getOrder() { return -2; }

    static class Bucket { long windowStart; AtomicInteger count = new AtomicInteger(); Bucket(long s){windowStart=s;} }
}
