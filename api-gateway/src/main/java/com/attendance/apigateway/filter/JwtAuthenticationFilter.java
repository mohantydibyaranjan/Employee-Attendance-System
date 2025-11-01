package com.attendance.apigateway.filter;

import com.attendance.apigateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        final List<String> apiEndpoints = List.of("/register", "/login");

        if (apiEndpoints.stream().anyMatch(s -> request.getURI().getPath().contains(s))) {
            return chain.filter(exchange);
        }

        final String authHeader = request.getHeaders().getOrEmpty("Authorization").get(0);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        final String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.extractAllClaims(token);
        exchange.getRequest().mutate().header("role", String.valueOf(claims.get("role"))).build();

        return chain.filter(exchange);
    }
}
