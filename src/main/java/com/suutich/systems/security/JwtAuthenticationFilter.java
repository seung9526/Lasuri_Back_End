package com.suutich.systems.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // 요청에서 토큰 추출 로직을 여기에 구현
        // 만약 토큰이 "Bearer {token}" 형식으로 전달된다면, "Bearer " 부분을 제거하고 순수한 토큰만 반환하세요.
        // 헤더, 쿼리 매개변수 또는 요청 본문에서 토큰을 추출하는 방법을 선택할 수 있습니다.
        // 추출된 토큰을 반환합니다.
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 예외 처리
        }

        filterChain.doFilter(request, response);

    }
}