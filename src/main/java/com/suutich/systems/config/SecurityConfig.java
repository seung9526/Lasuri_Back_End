package com.suutich.systems.config;

import com.suutich.systems.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    //private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    //private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http.csrf(AbstractHttpConfigurer::disable)
                        .anonymous(auth -> auth.authorities("/**"))
                .headers(headers -> headers.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                        /* h2 false 시 사용가능
                        .authorizeHttpRequests((authorizeRequests) -> {
                            authorizeRequests.requestMatchers("/api/user/**").authenticated();

                            authorizeRequests.requestMatchers("/**").permitAll();

                            authorizeRequests.anyRequest().permitAll();
                        })*/
                        .apply(new JwtSecurityConfig(jwtTokenProvider))
                ;


                return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder () {
        return new BCryptPasswordEncoder();
    }

}