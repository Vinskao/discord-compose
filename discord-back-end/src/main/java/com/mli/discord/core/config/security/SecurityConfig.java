package com.mli.discord.core.config.security;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mli.discord.module.login.dao.UserDAO;
import com.mli.discord.module.login.filter.JwtAuthenticationFilter;
import com.mli.discord.module.login.service.JwtService;
import com.mli.discord.module.login.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * 安全配置類，定義了應用的安全性設置。
 * 
 * @Author D3031104
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	// 避免循環依賴
	@Lazy
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	// 避免循環依賴
	@Autowired
	@Lazy
	private UserService userService;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserDAO userDAO;

	/**
	 * 身份驗證提供者的Bean定義。
	 * 
	 * @return 身份驗證提供者
	 */
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	/**
	 * 提供UserDetailsService的Bean配置。
	 * 此服務用於根據用戶名從資料庫檢索用戶詳情。
	 * 
	 * @return UserDetailsService實例
	 */
	@Bean
	UserDetailsService userDetailsService() {
		return username -> userDAO.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	}

	/**
	 * 配置和初始化HttpSecurity來定義安全控制規則。
	 * 設定CORS、CSRF、路由權限、過濾器、會話管理和登出流程。
	 * 
	 * @param http HttpSecurity實例
	 * @return 配置完成的SecurityFilterChain
	 * @throws Exception 如果配置過程中出現錯誤
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				// 授權請求配置
				.authorizeRequests(auth -> {
					// 允許無權限訪問的URL
					auth.antMatchers("/user/check-session", "/user/logout", "/user/login", "/user/find-by-id",
							"/user/register", "/user/me", "/get-question", "/verify-answer", "/user/update-password",
							"/error", "/ws-message/**", "/send", "/get-messages",
							"/add-security-question")
							.permitAll()
							// 要求ADMIN或NORMAL權限的URL
							.antMatchers("/export-chat-history").hasAuthority("ADMIN")
							.antMatchers("/user-to-room/**", "/user-to-group/**", "/send", "/get-messages",
									"/room/find-all-rooms", "/groups/find-all-groups", "/modify-security-question",
									"/user/update-user-details")
							.hasAnyAuthority("ADMIN", "NORMAL").anyRequest().authenticated();
				})
				// 在UsernamePasswordAuthenticationFilter之前添加自定義JWT過濾器
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				// 會話管理配置
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				// 登出配置
				.logout(logout -> {
					logout.logoutUrl("/user/logout")
							.addLogoutHandler(new LogoutHandler() {
								@Override
								public void logout(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) {
									if (authentication != null
											&& authentication.getPrincipal() instanceof UserDetails) {
										UserDetails userDetails = (UserDetails) authentication.getPrincipal();
										String logoutToken = jwtService.generateAndPersistLogoutToken(userDetails);
										response.setContentType("application/json");
										response.setStatus(HttpServletResponse.SC_OK);
										try {
											response.getWriter().write("{\"logoutToken\": \"" + logoutToken
													+ "\", \"message\": \"Logout successful.\"}");
										} catch (IOException e) {
											logger.error("Error writing logout token to response", e);
										}
									}
								}
							})
							.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
							.deleteCookies("JSESSIONID").clearAuthentication(true);
				});

		return http.build();
	}

	/**
	 * 密碼編碼器的Bean定義。
	 * 
	 * @return 密碼編碼器
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * CORS配置源的Bean定義。
	 * 
	 * @return CORS配置源
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedMethod("GET");
		configuration.addAllowedMethod("POST");
		configuration.addAllowedMethod("PUT");
		configuration.addAllowedMethod("DELETE");
		configuration.addAllowedMethod("OPTIONS");
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);
		configuration.setAllowedOriginPatterns(
				Arrays.asList("http://*.localhost", "https://*.localhost", "http://localhost:8091",
						"http://localhost:8090"));

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
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * HTTP防火牆配置的Bean定義。
	 * 
	 * @return HTTP防火牆
	 */
	@Bean
	HttpFirewall allowUrlEncodedSlashHttpFirewall() {
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
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
	}

	/**
	 * 提供SessionRegistry的Bean配置。
	 * SessionRegistry用於追蹤和管理所有活動的會話。
	 * 
	 * @return SessionRegistry實例
	 */
	@Bean
	SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
}
