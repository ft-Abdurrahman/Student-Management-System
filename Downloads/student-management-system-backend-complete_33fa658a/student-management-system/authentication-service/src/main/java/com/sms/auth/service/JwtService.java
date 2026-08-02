package com.sms.auth.service;

import com.sms.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final String audience;
    private final long accessTtlSec;

    public JwtService(
            @Value("${sms.jwt.secret}") String secret,
            @Value("${sms.jwt.issuer}") String issuer,
            @Value("${sms.jwt.audience}") String audience,
            @Value("${sms.jwt.access-ttl-minutes:15}") long accessTtlMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.accessTtlSec = accessTtlMinutes * 60;
    }

    public String issueAccess(User u, String deviceId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(u.getId()))
                .issuer(issuer)
                .audience().add(audience).and()
                .claim("email", u.getEmail())
                .claim("roles", u.getRoles())
                .claim("deviceId", deviceId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSec)))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> verify(String token) {
        return Jwts.parser().verifyWith(key).requireIssuer(issuer).requireAudience(audience)
                .build().parseSignedClaims(token);
    }

    public long getAccessTtlSec() { return accessTtlSec; }
    @SuppressWarnings("unchecked")
    public Set<String> roles(Claims c) { return Set.copyOf((java.util.Collection<String>) c.get("roles", java.util.Collection.class)); }
}
