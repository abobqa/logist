package org.logistservice.logist.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService customUserDetailsService) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .userDetailsService(customUserDetailsService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // ДОЛЖНО ловить ВСЁ, иначе статика не работает
                .securityMatcher("/**")

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- СТАТИКА ДОЛЖНА БЫТЬ ЗДЕСЬ ---
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()

                        .requestMatchers("/login", "/ui/register").permitAll()

                        .requestMatchers("/ui/users", "/ui/users/**", "/ui/admin/users", "/ui/admin/users/**").hasRole("ADMIN")
                        .requestMatchers("/ui/*/*/delete").hasRole("ADMIN")
                        .requestMatchers("/ui/drivers/new", "/ui/drivers/*/edit", "/ui/drivers/*/delete",
                                         "/ui/vehicles/new", "/ui/vehicles/*/edit", "/ui/vehicles/*/delete")
                                .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/ui/orders/new", "/ui/orders/*/edit", "/ui/orders/*/delete",
                                         "/ui/clients/new", "/ui/clients/*/edit", "/ui/clients/*/delete")
                                .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/ui/orders", "/ui/orders/**").hasAnyRole("ADMIN", "MANAGER", "OPERATOR", "USER")
                        .requestMatchers("/ui/clients", "/ui/clients/**").hasAnyRole("ADMIN", "MANAGER", "OPERATOR", "USER")
                        .requestMatchers("/ui/drivers", "/ui/drivers/**").hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                        .requestMatchers("/ui/vehicles", "/ui/vehicles/**").hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                        .requestMatchers("/ui/stats", "/ui/stats/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/ui/**").hasAnyRole("ADMIN", "MANAGER", "OPERATOR", "USER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}

