package com.huytpq.SecurityEx.base.security;

import com.huytpq.SecurityEx.recipe.entity.UserPrinciple;
import com.huytpq.SecurityEx.recipe.service.impl.JWTService;
import com.huytpq.SecurityEx.recipe.service.impl.MyUserDetailService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext applicationContext;

    private static final List<Pair<String, String>> BYPASS_TOKENS = Arrays.asList(
            new Pair<>(String.format("/%s", "register"), "POST"), // skip token api POST /register
            new Pair<>(String.format("/%s", "login"), "POST"),    // skip token api /login
            new Pair<>(String.format("/%s", "roles"), "GET"),
            new Pair<>(String.format("/%s", "recipes"), "GET"),
            new Pair<>(String.format("/%s", "recipe-categories"), "GET")
//            new Pair<>(String.format("/%s", "recipe/images"), "GET")
    );

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String method = request.getMethod();

        for (Pair<String, String> bypassToken : BYPASS_TOKENS) {
            if (servletPath.contains(bypassToken.getFirst()) && method.equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isBypassToken(request)) {
            filterChain.doFilter(request, response); // skip JWT
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                username = jwtService.extractUserName(jwtToken);
            } catch (Exception e) {
                System.out.println("JWT Token extraction failed: " + e.getMessage());
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserPrinciple userDetails = (UserPrinciple) applicationContext.getBean(MyUserDetailService.class).loadUserByUsername(username);
            if (jwtService.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }
        filterChain.doFilter(request, response);
    }

    private static class Pair<K, V> {
        private final K first;
        private final V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }

        public K getFirst() {
            return first;
        }

        public V getSecond() {
            return second;
        }
    }

}
