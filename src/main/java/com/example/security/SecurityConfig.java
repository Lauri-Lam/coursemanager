package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/VAADIN/**",
                                "/frontend/**",
                                "/themes/**",
                                "/images/**",
                                "/icons/**",
                                "/favicon.ico",
                                "/manifest.webmanifest",
                                "/sw.js",
                                "/offline.html",
                                "/HILLA/**",
                                "/connect/**",
                                "/login",
                                "/register",
                                "/"
                        ).permitAll()

                        .requestMatchers("/dashboard").authenticated()

                        .requestMatchers("/authenticated").authenticated()

                        .requestMatchers("/push").authenticated()

                        .requestMatchers("/user-super").hasRole("SUPER")

                        .requestMatchers("/admin").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/dashboard", true)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}