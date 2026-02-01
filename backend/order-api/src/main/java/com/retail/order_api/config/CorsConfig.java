package com.retail.order_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Angular dev server (por defecto)
                .allowedOrigins("http://localhost:4200", "https://pabloavg.github.io")
                .allowedMethods("GET", "POST", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
