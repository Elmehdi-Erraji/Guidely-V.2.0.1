package com.spring.guidely.config;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.repository.AuthRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthRepository authRepository;

    public JwtAuthenticationFilter(JwtService jwtService, AuthRepository authRepository) {
        this.jwtService = jwtService;
        this.authRepository = authRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromHeader(request);

        if (token != null && jwtService.validateToken(token)) {
            String email = jwtService.getEmailFromToken(token);

            // Since AppUser implements UserDetails, we can directly use it
            Optional<AppUser> optionalAppUser = authRepository.findByEmail(email);
            if (optionalAppUser.isPresent()) {
                AppUser appUser = optionalAppUser.get();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        appUser,
                        null,
                        appUser.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
