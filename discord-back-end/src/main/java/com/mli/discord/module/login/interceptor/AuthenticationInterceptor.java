// package com.mli.discord.interceptor;

// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.servlet.HandlerInterceptor;

// public class AuthenticationInterceptor implements HandlerInterceptor {
// private final Logger logger = LoggerFactory.getLogger(this.getClass());

// @Override
// public boolean preHandle(HttpServletRequest request, HttpServletResponse
// response, Object handler)
// throws Exception {
// logger.info("AuthenticationInterceptor - PreHandle method is called");

// Authentication authentication =
// SecurityContextHolder.getContext().getAuthentication();
// System.out.printf("authentication", authentication);
// if (authentication != null && authentication.isAuthenticated()) {
// String loggedInUsername = authentication.getName();
// logger.info("User {} is already authenticated.", loggedInUsername);
// // 如果已经有用户认证，可以进行进一步处理，如直接返回成功或跳转到其他页面
// return true;
// } else {
// // 如果用户未经身份验证，可以根据需要进行处理，如跳转到登录页面或返回未经授权的错误
// response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户未经身份验证");
// return false;
// }
// }
// }
