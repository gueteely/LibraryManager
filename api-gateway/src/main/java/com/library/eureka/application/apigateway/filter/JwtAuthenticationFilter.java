package com.library.eureka.application.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private final List<String> openApiEndpoints = List.of(
            "/auth/login",
            "/auth/register",
            "/books"  // Только для чтения (GET method only)
    );

    private final List<String> adminEndpoints = List.of(
            "/logs/all",
            "/logs/period"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // Пропускаем аутентикацию для открытых эндпоинтов
        if (isOpenEndpoint(path, method)) {
            return chain.filter(exchange);
        }

        // Проверка на заголовок в запросе, есть ли авторизация
        if (!request.getHeaders().containsKey("Authorization")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String authHeader = request.getHeaders().getOrEmpty("Authorization").get(0);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            // Проверка токена
            Claims claims = extractClaims(token);
            String username = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            String role = extractRole(claims);

            // Проверка есть ли админ-доступ
            if (isAdminEndpoint(path) && !"ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-ID", userId.toString())
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isOpenEndpoint(String path, String method) {
        // Разрешаем только GET-запросы к публичным книгам
        if (path.contains("/books") && !method.equals("GET")) {
            return false;
        }
        return openApiEndpoints.stream()
                .anyMatch(path::contains);
    }

    private boolean isAdminEndpoint(String path) {
        return adminEndpoints.stream()
                .anyMatch(path::contains);
    }

    private String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public int getOrder() {
        return -1; // Высокий приоритет для выполнения этого фильтра перед другими
    }
}