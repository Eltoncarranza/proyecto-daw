package com.gineco.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Mapea el link limpio al archivo físico
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/dashboard").setViewName("forward:/index.html");

        // Redirección automática: si entran a la raíz, mandarlos al login
        registry.addRedirectViewController("/", "/login");
    }
}