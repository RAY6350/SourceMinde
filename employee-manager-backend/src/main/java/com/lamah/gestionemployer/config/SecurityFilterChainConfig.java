package com.lamah.gestionemployer.config;

import com.lamah.gestionemployer.jwt.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final static String[] VERIFICATION_URLS = {
            "/api/v1/employees/confirm",
            "/api/v1/employees/resendVerifyToken",
            "/api/v1/employees/savePassword"
    };

    private final static String[] RESET_PASSWORD_URLS = {
            "/api/v1/employees/resetPassword",
            "/api/v1/employees/savePassword",
            "/api/v1/employees/changePassword"
    };

    public SecurityFilterChainConfig(
            JWTAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST,"/api/v1/employees", "/api/v1/auth/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/hello")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/sendMail")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, VERIFICATION_URLS)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, RESET_PASSWORD_URLS)
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(a -> a.authenticationEntryPoint(authenticationEntryPoint));
        return http.build();
    }
}








