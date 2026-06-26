package com.dexcode.taskmasterai.filters;

import com.dexcode.taskmasterai.components.JWTUtil;
import com.dexcode.taskmasterai.entities.User;
import com.dexcode.taskmasterai.exceptions.jwt.JWTFilterException;
import com.dexcode.taskmasterai.services.AuthService;
import com.dexcode.taskmasterai.services.JWTService;
import com.dexcode.taskmasterai.services.SessionManagerService;
import com.dexcode.taskmasterai.services.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SessionManagerService sessionManagerService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtil.isAccessTokenValid(token)) {
                Claims claims = jwtUtil.getClaimsFromToken(token, false);
                setAuthContext(request, claims);
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtUtil.isTokenExpired(token)) {

                Claims expiredClaims = jwtUtil.getClaimsFromToken(token, true);
                String refreshToken = expiredClaims.get("refresh_token", String.class);
                Long user_id = Long.valueOf(expiredClaims.get("userId", String.class));

                if (sessionManagerService.checkUserSessionValidity(refreshToken)) {

                    String newAccessToken = jwtService.generateNewAuthToken(user_id, refreshToken);

                    response.setHeader("X-New-Access-Token", newAccessToken);

                    setAuthContext(request, expiredClaims);
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            handleInvalidToken(response);

        } catch (Exception e) {
            handleInvalidToken(response);
        }
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid or expired token");
    }

    private void setAuthContext(HttpServletRequest request, Claims claims) {

        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role)
        );

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null, authorities);

        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
