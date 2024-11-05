package ru.moonlightapp.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import ru.moonlightapp.backend.service.JwtTokenService;
import ru.moonlightapp.backend.service.jwt.JwtTokenAuthorizationFilter;
import ru.moonlightapp.backend.storage.repository.UserRepository;
import ru.moonlightapp.backend.web.MoonlightAuthenticationDetailsSource;
import ru.moonlightapp.backend.web.auth.MoonlightAuthenticationHandler;
import ru.moonlightapp.backend.web.auth.service.UserStorageService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final MoonlightAuthenticationHandler authenticationHandler;
    private final ObjectMapper objectMapper;

    @Value("${server.servlet.session.cookie.name}")
    private String sessionCookieName;

    @Bean
    @Order(1)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/error", "/static/**", "/favicon.ico").permitAll()
                        .requestMatchers("/swagger-ui/**", "/docs/openapi").permitAll()
                        .requestMatchers("/auth/token/validate").authenticated()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/catalog/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
                .formLogin(formLogin -> formLogin
                        .loginPage("/auth/sign-in")
                        .loginProcessingUrl("/auth/sign-in")
                        .successHandler(authenticationHandler)
                        .failureHandler(authenticationHandler)
                        .usernameParameter("email")
                        .passwordParameter("password"))
                .addFilterBefore(jwtTokenAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .deleteCookies(sessionCookieName)
                        .logoutUrl("/auth/logout"))
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain addJwtAuthenticationMethod(HttpSecurity http) throws Exception {
        return http
                .build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain fixNginxReverseProxyRealIPsIssue(HttpSecurity http) throws Exception {
        return enforceCustomAuthenticationDetailsSource(http.build());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserStorageService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public JwtTokenAuthorizationFilter jwtTokenAuthorizationFilter() {
        return new JwtTokenAuthorizationFilter(jwtTokenService, userRepository, objectMapper);
    }

    private SecurityFilterChain enforceCustomAuthenticationDetailsSource(SecurityFilterChain filterChain) {
        // change authentication details source in all filters
        for (Filter filter : filterChain.getFilters()) {
            if (filter instanceof AbstractAuthenticationProcessingFilter cast) {
                cast.setAuthenticationDetailsSource(MoonlightAuthenticationDetailsSource.SINGLETON);
            } else if (filter instanceof AnonymousAuthenticationFilter cast) {
                cast.setAuthenticationDetailsSource(MoonlightAuthenticationDetailsSource.SINGLETON);
            } else if (filter instanceof AbstractPreAuthenticatedProcessingFilter cast) {
                cast.setAuthenticationDetailsSource(MoonlightAuthenticationDetailsSource.SINGLETON);
            } else if (filter instanceof SwitchUserFilter cast) {
                cast.setAuthenticationDetailsSource(MoonlightAuthenticationDetailsSource.SINGLETON);
            } else if (filter instanceof BasicAuthenticationFilter cast) {
                cast.setAuthenticationDetailsSource(MoonlightAuthenticationDetailsSource.SINGLETON);
            } else if (filter instanceof DigestAuthenticationFilter cast) {
                cast.setAuthenticationDetailsSource(MoonlightAuthenticationDetailsSource.SINGLETON);
            }
        }

        return filterChain;
    }

}