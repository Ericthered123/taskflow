package com.eric.taskflow.config;

import com.eric.taskflow.model.Role;
import com.eric.taskflow.model.User;
import com.eric.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.role}")
    private String adminRole;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            try {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setEmail(adminEmail);
                admin.setRole(Role.valueOf(adminRole.toUpperCase()));
                userRepository.save(admin);
                log.info("Admin creado: {}", adminUsername);
            } catch (IllegalArgumentException ex) {
                log.error("Role admin inválido en config: {}", adminRole, ex);
            } catch (Exception ex) {
                log.error("Error creando admin por defecto", ex);
            }
        } else {
            log.info("Admin ya existe: {}", adminUsername);
        }
    }
}