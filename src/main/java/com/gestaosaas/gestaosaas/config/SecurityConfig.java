package com.gestaosaas.gestaosaas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//Classe responsável pela configuração de segurança da aplicação
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Bean responsável por criptografar e validar senhas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração principal das regras de segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/403",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/h2-console/**"
                        ).permitAll()

                        .requestMatchers("/empresa/**").hasRole("ADMIN")
                        .requestMatchers("/financeiro/**").hasAnyRole("ADMIN", "GERENTE")
                        .requestMatchers("/", "/clientes/**", "/produtos/**", "/vendas/**")
                        .hasAnyRole("ADMIN", "GERENTE", "OPERADOR")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/403")
                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )

                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }
}