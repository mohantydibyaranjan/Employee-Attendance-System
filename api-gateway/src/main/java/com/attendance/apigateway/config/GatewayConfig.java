package com.attendance.apigateway.config;

import com.attendance.apigateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))
                .route("employee-service", r -> r.path("/employee/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://employee-service"))
                .route("attendance-service", r -> r.path("/attendance/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://attendance-service"))
                .route("report-service", r -> r.path("/report/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://report-service"))
                .build();
    }
}
