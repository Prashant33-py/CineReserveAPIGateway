package com.cinereserve.app.config;

import com.cinereserve.app.filter.JwtGatewayFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtGatewayFilterFactory jwtFilterFactory;

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("cine-reserve-user-service", r -> r
                        .path("/api/cine-reserve/user/**")
                        .filters(f -> f.filter(jwtFilterFactory.apply(new JwtGatewayFilterFactory.Config())))
                        .uri("lb://CINE-RESERVE-USER-SERVICE"))
                .route("cine-reserve-user-service", r -> r
                        .path("/api/cine-reserve/movie/**")
                        .filters(f -> f.filter(jwtFilterFactory.apply(new JwtGatewayFilterFactory.Config())))
                        .uri("lb://CINE-RESERVE-MOVIE-SERVICE"))
                .build();
    }


}
