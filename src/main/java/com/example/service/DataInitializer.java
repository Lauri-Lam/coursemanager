package com.example.service;

import com.example.domain.AppUser;
import com.example.domain.Role;
import com.example.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository appUserRepository,
                           PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createUserIfNotExists("admin", "admin123", Role.ADMIN);
        createUserIfNotExists("super", "super123", Role.SUPER);
        createUserIfNotExists("user", "user123", Role.USER);
    }

    private void createUserIfNotExists(String username, String password, Role role) {
        if (!appUserRepository.existsByUsername(username)) {
            AppUser user = new AppUser(
                    username,
                    passwordEncoder.encode(password),
                    role
            );

            appUserRepository.save(user);
        }
    }
}