package com.example.goldenhour.security.jwt;

import com.example.goldenhour.dto.UserDTO;
import com.example.goldenhour.exception.http.Http400ResponseException;
import com.example.goldenhour.security.dto.CustomUserDetails;
import com.example.goldenhour.security.oauth2.OAuth2UserInfoProxy;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final JWTService jwtService;
    private final Map<String, OAuth2UserInfoProxy> oAuth2UserInfoProxyMap;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (requestUri.matches("^\\/login(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authorization = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            System.out.println(cookie.getName());
            if (cookie.getName().equals("Authorization")) {

                authorization = cookie.getValue();
            }
        }

        if (authorization == null) {

            System.out.println("Token null");
            filterChain.doFilter(request, response);

            return;
        }

        String jwtToken = authorization;

        try {
            jwtUtil.isExpired(jwtToken);
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", e);
            System.out.println("Token expired");
            filterChain.doFilter(request, response);

            return;
        }

        String username = jwtUtil.getUsername(jwtToken);
        String role = jwtUtil.getRole(jwtToken);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);

        if (!jwtService.checkJWTUser(userDTO)) {

            System.out.println("invalid user");
            filterChain.doFilter(request, response);

            return;
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(userDTO);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
