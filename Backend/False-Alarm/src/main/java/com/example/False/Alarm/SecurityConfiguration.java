package com.example.False.Alarm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder getEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests(auth->{
                    auth.requestMatchers("/users/user").permitAll();
                    auth.requestMatchers("/users/admin").hasAuthority("ADMIN");
                    auth.requestMatchers("/users/chat/**").hasAuthority("USER");
                    auth.requestMatchers("/users/search").hasAuthority("USER");
                    auth.anyRequest().authenticated();

                })
                .formLogin(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf-> csrf.disable())
                .build();
    }

}