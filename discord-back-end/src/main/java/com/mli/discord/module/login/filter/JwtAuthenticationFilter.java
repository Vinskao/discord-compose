package com.mli.discord.module.login.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mli.discord.module.login.service.JwtService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        logger.debug("JwtAuthenticationFilter: Checking for Authorization header");

        // 以下條件為沒有攜帶Token的請求
        // 如果未攜帶JWT令牌或令牌不以"Bearer "開頭，則直接呼叫filterChain.doFilter，繼續處理下一個過濾器或請求處理程序。
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7); // 取"Bearer "後面的Token
        username = jwtService.extractUsername(jwt); // 提取Token中的username

        logger.debug("JwtAuthenticationFilter: Extracted Username: {}", username);

        // 如果用戶名不為null且當前的Security上下文中不存在身份驗證
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // 使用UserDetailsService根據用戶名加載用戶詳細信息。
            if (jwtService.isTokenValid(jwt, userDetails)) {
                logger.debug("JwtAuthenticationFilter: Token is valid");

                // 如果JWT令牌有效，則創建一個UsernamePasswordAuthenticationToken並將其設置到Spring
                // Security的Security上下文中，以確保用戶已成功驗證。
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.debug("JwtAuthenticationFilter: Token is invalid");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}