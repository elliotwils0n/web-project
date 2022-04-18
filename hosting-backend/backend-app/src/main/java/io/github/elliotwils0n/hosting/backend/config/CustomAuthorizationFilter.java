package io.github.elliotwils0n.hosting.backend.config;

import io.github.elliotwils0n.hosting.backend.model.Role;
import io.github.elliotwils0n.hosting.backend.service.implementation.AuthorizationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final AuthorizationService authorizationService;

    public CustomAuthorizationFilter(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> authHeader = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (authHeader.isPresent() && authHeader.get().startsWith("Bearer ")) {
            String token = authHeader.get().replace("Bearer ", "");

            boolean validToken = false;
            Optional<UUID> accountId = Optional.empty();

            try {
                validToken = authorizationService.isAccessTokenValid(token);
                accountId = authorizationService.getAccountIdFromAccessToken(token);
            } catch (Exception e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }

            if (validToken && accountId.isPresent()) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(accountId.get(), null, Collections.singleton(new SimpleGrantedAuthority(Role.AUTHORIZED.getName())));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}