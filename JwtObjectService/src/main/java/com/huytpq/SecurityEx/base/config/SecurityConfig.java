package com.huytpq.SecurityEx.base.config;

import com.huytpq.SecurityEx.base.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //CORS
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // ap dung cho all Endpoint

        // Thêm CorsFilter vào HttpSecurity
        http.cors(cors -> cors.configurationSource(source));

        return http
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        // API public
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/login").permitAll()
                        // API logout
                        .requestMatchers("/logout").authenticated()
                        // API (POST) - only ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/recipes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/recipe-categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/post-tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/ingredients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/recipe-ingredients/**").hasRole("ADMIN")
                        // API (PUT) - only ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/recipes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/recipe-categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/post-tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/ingredients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/recipe-ingredients/**").hasRole("ADMIN")
                        // API (DELETE) - only ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/recipes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/recipe-categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/post-tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/ingredients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/recipe-ingredients/**").hasRole("ADMIN")
                        // API (GET) - PUBLIC
                        .requestMatchers(HttpMethod.GET, "/api/recipes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/recipes/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/recipe-categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/roles/**").permitAll()
                        // API (GET) - ADMIN N USER
                        .requestMatchers(HttpMethod.GET, "/api/tags/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/post-tags/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/ingredients/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/recipe-ingredients/**").hasAnyRole("ADMIN", "USER")
                        // API FavoriteRecipe: USER N ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/favorite-recipes/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/favorite-recipes/**").hasAnyRole("ADMIN", "USER")
                        // API USER: USER N ADMIN
                        .requestMatchers(HttpMethod.GET, "/get-list/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/details/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/update/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//login va reload sinh ra session moi
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(10));
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//
//        UserDetails user1 = User
//                .withDefaultPasswordEncoder()
//                .username("huyad")
//                .password("atm@123")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user2 = User
//                .withDefaultPasswordEncoder()
//                .username("huytpq")
//                .password("atm@1234")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2);
//    }
}
