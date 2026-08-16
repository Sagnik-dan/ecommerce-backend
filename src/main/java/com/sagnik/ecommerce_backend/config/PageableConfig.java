package com.sagnik.ecommerce_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PageableConfig {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {

        return resolver -> {
            resolver.setMaxPageSize(50);
            resolver.setFallbackPageable(
                    org.springframework.data.domain.PageRequest.of(0, 10)
            );
        };
    }
}