package com.sms.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

/** Adds a correlation id and logs each request. */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String reqId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (reqId == null || reqId.isBlank()) reqId = UUID.randomUUID().toString();
        final String rid = reqId;
        long start = System.currentTimeMillis();
        var mutated = exchange.mutate()
                .request(exchange.getRequest().mutate().header("X-Request-Id", rid).build())
                .build();
        return chain.filter(mutated).doFinally(s -> {
            long took = System.currentTimeMillis() - start;
            var req = mutated.getRequest();
            var res = mutated.getResponse();
            System.out.printf("[GW] %s %s -> %s (%dms) [rid=%s]%n",
                    req.getMethod(), req.getPath(),
                    res.getStatusCode() == null ? "?" : res.getStatusCode().toString(),
                    took, rid);
        });
    }
    @Override public int getOrder() { return -1; }
}
