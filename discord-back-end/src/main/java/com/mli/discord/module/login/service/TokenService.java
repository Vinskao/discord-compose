// package com.mli.discord.module.login.service;

// import java.security.Key;
// import java.time.Instant;
// import java.util.Date;

// import javax.annotation.PostConstruct;

// import org.springframework.stereotype.Service;

// import com.mli.discord.module.login.model.LoginRequest;
// import com.mli.discord.module.login.model.LoginResponse;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys;

// @Service
// public class TokenService {
// private Key secretKey;

// @PostConstruct
// private void init() {
// String key = "amazingamazingamazingamazingamazingamazingamazing";
// secretKey = Keys.hmacShaKeyFor(key.getBytes());
// }

// public LoginResponse createToken(LoginRequest request) {
// String accessToken = createAccessToken(request.getUsername());
// LoginResponse res = new LoginResponse();
// res.setAccessToken(accessToken);
// return res;
// }

// public String createAccessToken(String username) {
// // 有效時間（毫秒）
// long expirationMillis = Instant.now()
// .plusSeconds(90)
// .getEpochSecond()
// * 1000;
// // 設置標準內容與自定義內容
// Claims claims = Jwts.claims();
// claims.setSubject("Access Token");
// claims.setIssuedAt(new Date());
// claims.setExpiration(new Date(expirationMillis));
// claims.put("username", username);

// return Jwts.builder()
// .setClaims(claims)
// .signWith(secretKey)
// .compact();
// }
// }
