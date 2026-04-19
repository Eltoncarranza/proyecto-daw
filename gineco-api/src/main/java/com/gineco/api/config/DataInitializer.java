package com.gineco.api.config;

import com.gineco.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;

    @Override
    public void run(String... args) {
        authService.crearUsuarioInicial();
        log.info("=== GinecoObstetrics API lista ===");
        log.info("Usuario inicial: doctor / gineco2025");
        log.info("Swagger UI: http://localhost:8080/swagger-ui.html");
    }
}
