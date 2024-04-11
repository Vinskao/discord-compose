package com.mli.discord.core.config.security;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mli.discord.module.login.service.UserService;

/**
 * 安全配置類，定義了應用的安全性設置。
 * @Author D3031104
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // 避免循環依賴
    @Autowired
    @Lazy
    private UserService userService;
    
    /**
     * 身份驗證提供者的Bean定義。
     * 
     * @return 身份驗證提供者
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    /**
     * 安全過濾器鏈配置。
     * 
     * @param httpSecurity Http安全配置
     * @return 安全過濾器鏈
     * @throws Exception 配置過程中可能發生的異常
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf().disable()
                .authenticationProvider(authenticationProvider())
                .formLogin()
                .loginProcessingUrl("/user/login")
                .successHandler((request, response, authentication) -> {
                    // Handle successful authentication
                    HttpSession session = request.getSession(true);
                    session.setAttribute("username", authentication.getName());

                    // 获取用户权限并转换为逗号分隔的字符串
                    String authorities = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","));
                    session.setAttribute("authorities", authorities);

                    response.setStatus(HttpServletResponse.SC_OK);
                })
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Login failed: " + exception.getMessage());
                })
                .and()
                .cors()
                .and()


                .authorizeRequests()
                .antMatchers("/user/check-session", "/user/logout", "/user/login",
                        "/user/find-by-id", "/user/register", "/user/me","/get-question","/verify-answer","/user/update-password")
                // .antMatchers("/**")
                .permitAll()
                .antMatchers("/export-chat-history").hasAuthority("ADMIN") // 仅ADMIN角色可以访问
                .antMatchers("/user-to-room/**",
                        "/user-to-group/**", "/send", "/get-messages",
                        "/room/find-all-rooms", "/groups/find-all-groups",
                        "/modify-security-question", "/add-security-question", "/user/update-user-details")
                .hasAnyAuthority("ADMIN", "NORMAL") // ADMIN和NORMAL角色都可以访问
                .anyRequest().authenticated() // 所有其他请求都需要认证
                .and()
                .httpBasic(); // 使用HTTP Basic认证
        return httpSecurity.build();
    }
    
    /**
     * 密碼編碼器的Bean定義。
     * 
     * @return 密碼編碼器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    /**
     * CORS配置源的Bean定義。
     * 
     * @return CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8090")); // 允许的前端地址
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // 允许凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    
    /**
     * 身份驗證管理器的Bean定義。
     * 
     * @param http Http安全配置
     * @return 身份驗證管理器
     * @throws Exception 配置過程中可能發生的異常
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
    
    
    /**
     * HTTP防火牆配置的Bean定義。
     * 
     * @return HTTP防火牆
     */
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }
    
    
    /**
     * Web安全自定義配置的Bean定義。
     * 
     * @return Web安全自定義配置
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

}
