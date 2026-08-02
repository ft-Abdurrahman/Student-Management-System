package com.sms.student.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Reads gateway-injected identity headers (X-User-Id, X-User-Email, X-Roles) and
 * populates the SecurityContext so @PreAuthorize works. The gateway is
 * authoritative — this service must only be reachable through it.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                    .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                    .anyRequest().authenticated())
            .addFilterBefore(headerAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    OncePerRequestFilter headerAuthFilter() {
        return new OncePerRequestFilter() {
            @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
                    throws ServletException, IOException {
                String uid = req.getHeader("X-User-Id");
                String email = req.getHeader("X-User-Email");
                String roles = req.getHeader("X-Roles");
                if (uid != null && !uid.isBlank()) {
                    List<SimpleGrantedAuthority> auths = roles == null ? List.of() :
                            Arrays.stream(roles.split(",")).filter(s -> !s.isBlank())
                                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim())).toList();
                    var token = new AbstractAuthenticationToken(auths) {
                        @Override public Object getCredentials() { return ""; }
                        @Override public Object getPrincipal() { return email == null ? uid : email; }
                    };
                    token.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
                fc.doFilter(req, res);
            }
        };
    }
}
