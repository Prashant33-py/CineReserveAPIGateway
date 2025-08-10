package com.cinereserve.app.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class JwtGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtGatewayFilterFactory.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String path = exchange.getRequest().getURI().getPath();
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            try {

                if (path.startsWith("/api/cine-reserve/user/login") ||
                        path.startsWith("/api/cine-reserve/user/register")) {
                    return chain.filter(exchange);
                }

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return this.onError(exchange, "Missing or invalid Authorization header");
                }

                String token = authHeader.substring(7);

                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                exchange = exchange.mutate()
                        .request(r -> r.headers(headers -> {
                            headers.add("X-User-Id", claims.getSubject());
                            headers.add("X-User-Roles", String.join(",", claims.get("role", String.class)));
                        }))
                        .build();

            } catch (ExpiredJwtException e) {
                return this.onError(exchange, "Invalid JWT token");
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}
