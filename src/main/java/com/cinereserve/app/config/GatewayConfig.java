package com.cinereserve.app.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private static final String BASE_URL = "/api/cine-reserve/";

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r ->
                    r
                            .path(BASE_URL + "movie/**")
                            .uri("lb://CINE-RESERVE-MOVIE-SERVICE")
                )
                .route(r ->
                    r
                            .path(BASE_URL + "user/**")
                            .uri("lb://CINE-RESERVE-USER-SERVICE")
                )
                .build();
    }

}
