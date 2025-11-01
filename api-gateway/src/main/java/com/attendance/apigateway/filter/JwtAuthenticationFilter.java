package com.attendance.apigateway.filter;

import com.attendance.apigateway.config.RouterValidator;
import com.attendance.apigateway.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.attendance.common.dto.ErrorResponse;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RouterValidator routerValidator;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (this.isAuthMissing(request)) {
                return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
            }

            final String token = this.getAuthHeader(request);

            if (!jwtUtil.validateToken(token)) {
                return this.onError(exchange, "Authorization header is invalid", HttpStatus.FORBIDDEN);
            }

            final Claims claims = jwtUtil.extractAllClaims(token);
            final String role = claims.get("role", String.class);

            if (!hasRequiredRole(request, role)) {
                return this.onError(exchange, "You do not have permission to access this resource", HttpStatus.FORBIDDEN);
            }

            exchange.getRequest().mutate()
                    .header("role", role)
                    .header("employeeId", claims.get("employeeId").toString())
                    .build();
        }
        return chain.filter(exchange);
    }

    private boolean hasRequiredRole(ServerHttpRequest request, String role) {
        String path = request.getURI().getPath();
        if (path.startsWith("/employee/") || path.startsWith("/report/")) {
            return "ADMIN".equals(role);
        }
        return true;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        ErrorResponse errorResponse = new ErrorResponse(false, err, null, exchange.getRequest().getPath().toString());
        try {
            byte[] bytes = objectMapper.writeValueAsString(errorResponse).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }
}
