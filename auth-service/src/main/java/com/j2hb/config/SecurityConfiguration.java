package com.j2hb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.j2hb.user.Role.ADMIN;
import static com.j2hb.user.Role.MANAGER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/auth/**","/services/**")
                .permitAll()
                .requestMatchers("/e/**").hasAnyRole(ADMIN.name(), MANAGER.name())
//                .requestMatchers("/infos/**")
//                .hasAnyRole(ADMIN.name(),MANAGER.name())
//                .requestMatchers(GET, "/infos/**").hasAnyAuthority(ADMIN_READ.name(),MANAGER_READ.name())
//                .requestMatchers(GET, "/infos/**").hasAnyAuthority(ADMIN_READ.name())
//                .requestMatchers(PUT, "/infos/**").hasAnyAuthority(ADMIN_UPDATE.name())
//                .requestMatchers(POST, "/infos/**").hasAnyAuthority(ADMIN_CREATE.name())
//                .requestMatchers(DELETE, "/infos/**").hasAnyAuthority(ADMIN_DELETE.name())
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
        return http.build();
    }
}
