package com.sms.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.jsonwebtoken.security.Keys;

/**
 * JwtValidation filter — verifies the bearer token using the shared secret,
 * extracts claims, and injects identity headers downstream.
 *
 * Use as a per-route filter: <code>filters: [JwtValidation]</code>.
 */
@Component
public class JwtValidationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtValidationGatewayFilterFactory.Config> {

    private final SecretKey key;
    private final String issuer;
    private final String audience;

    public JwtValidationGatewayFilterFactory(
            @Value("${sms.jwt.secret}") String secret,
            @Value("${sms.jwt.issuer:sms-auth}") String issuer,
            @Value("${sms.jwt.audience:sms-api}") String audience) {
        super(Config.class);
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest req = exchange.getRequest();
            String auth = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (auth == null || !auth.startsWith("Bearer ")) {
                return unauthorized(exchange.getResponse(), "Missing bearer token");
            }
            String token = auth.substring(7);
            try {
                Jws<Claims> jws = Jwts.parser()
                        .verifyWith(key)
                        .requireIssuer(issuer)
                        .requireAudience(audience)
                        .build()
                        .parseSignedClaims(token);
                Claims c = jws.getPayload();

                @SuppressWarnings("unchecked")
                List<String> roles = c.get("roles", List.class);
                String rolesHeader = roles == null ? "" : String.join(",", roles);

                // strip any client-supplied identity headers and re-inject
                ServerHttpRequest mutated = req.mutate()
                        .headers(h -> {
                            h.remove("X-User-Id");
                            h.remove("X-User-Email");
                            h.remove("X-Roles");
                        })
                        .header("X-User-Id", String.valueOf(c.getSubject()))
                        .header("X-User-Email", String.valueOf(c.get("email")))
                        .header("X-Roles", rolesHeader)
                        .build();
                return chain.filter(exchange.mutate().request(mutated).build());
            } catch (Exception ex) {
                return unauthorized(exchange.getResponse(), "Invalid token: " + ex.getMessage());
            }
        };
    }

    private Mono<Void> unauthorized(ServerHttpResponse res, String msg) {
        res.setStatusCode(HttpStatus.UNAUTHORIZED);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"status\":\"ERROR\",\"message\":\"" + msg.replace("\"","'") + "\"}";
        DataBuffer buf = res.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return res.writeWith(Mono.just(buf));
    }

    public static class Config { }
}
