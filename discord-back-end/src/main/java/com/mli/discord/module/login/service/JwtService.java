package com.mli.discord.module.login.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mli.discord.module.login.dao.JwtTokenDAO;
import com.mli.discord.module.login.model.JwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * JWT服務類，負責JWT令牌的生成和驗證。
 * 
 * @author D3031104
 * @version 1.0
 */
@Service
public class JwtService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// Token有效期限 (設定15分鐘過期)
	private Long EXPIRATION_TIME = 60L * 60 * 1000; // 單位ms

	// Token有效期限 (設定0.00025分鐘過期)
	private Long LOGOUT_EXPIRATION_TIME = 60L * 1; // 單位ms

	@Autowired
	private JwtTokenDAO jwtTokenDAO;
	// BASE64編碼的密鑰
	private String SECRET_KEY = "amazingamazingamazingamazingamazingamazingamazing";

	/**
	 * 從JWT令牌中提取用戶名。
	 * 
	 * @param token JWT令牌
	 * @return 提取的用戶名
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * 提取JWT令牌中的任何聲明（Claims）。
	 * 
	 * @param token          JWT令牌
	 * @param claimsResolver 解析Claims的函數
	 * @return 解析後的Claims數據
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * 生成JWT令牌。
	 * 
	 * @param userDetails 用戶詳情
	 * @return 生成的JWT令牌
	 */
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	/**
	 * 根據提供的Claims和用戶詳情生成JWT令牌。
	 * 
	 * @param extractClaims 預先設定的Claims
	 * @param userDetails   用戶詳情
	 * @return 生成的JWT令牌
	 */
	public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
		return Jwts.builder().setClaims(extractClaims).setSubject(userDetails.getUsername()) // 以Username做為Subject
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	/**
	 * 驗證JWT令牌的有效性。
	 * 
	 * @param token       JWT令牌
	 * @param userDetails 用戶詳情
	 * @return 如果令牌有效則返回True，否則返回False
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		logger.debug("isTokenValid; token: {}, userDetails: {}", token, userDetails);
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	/**
	 * 驗證JWT令牌是否已過期。
	 * 
	 * @param token JWT令牌
	 * @return 如果已過期則返回True，否則返回False
	 */
	private boolean isTokenExpired(String token) {
		final Date expirationDate = extractExpiration(token);
		// return extractExpiration(token).before(new Date());
		return expirationDate != null && expirationDate.before(new Date());
	}

	/**
	 * 從JWT令牌中提取過期時間。
	 * 
	 * @param token JWT令牌
	 * @return 過期時間
	 */
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * 獲取令牌中所有的聲明將其解析
	 * 
	 * @return 令牌中所有的聲明
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	/**
	 * 獲取JWT簽名的密鑰。
	 * 
	 * @return 簽名密鑰
	 */
	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * 生成並持久化JWT令牌。
	 * 使用用戶的詳細信息生成JWT令牌，並將令牌資訊存儲到資料庫。
	 * 
	 * @param userDetails 用戶的詳細信息
	 * @return 生成的JWT令牌
	 */
	public String generateAndPersistToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		String jti = UUID.randomUUID().toString(); // 生成JWT ID
		claims.put("jti", jti);

		String token = Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();

		JwtToken jwtToken = new JwtToken();
		jwtToken.setExp(new Date(System.currentTimeMillis() + EXPIRATION_TIME));
		jwtToken.setIat(new Date());
		jwtToken.setJti(jti);
		jwtToken.setIss("Issuer");
		jwtToken.setSub(userDetails.getUsername());
		jwtToken.setTyp("Bearer");
		try {
			jwtTokenDAO.insertJwtToken(jwtToken);
			logger.info("JWT token inserted successfully with JTI: {}", jti);
		} catch (Exception e) {
			logger.error("Failed to insert JWT token with JTI: {}", jti, e);
		}
		logger.info("Token generation and persistence completed for user: {}", userDetails.getUsername());

		return token;
	}

	/**
	 * 生成並持久化登出令牌。
	 * 使用用戶的詳細信息生成登出用的JWT令牌，並將令牌資訊存儲到資料庫。
	 * 
	 * @param userDetails 用戶的詳細信息
	 * @return 生成的登出JWT令牌
	 */
	public String generateAndPersistLogoutToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		String jti = UUID.randomUUID().toString(); // 生成JWT ID
		claims.put("jti", jti);

		String token = Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + LOGOUT_EXPIRATION_TIME))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();

		JwtToken jwtToken = new JwtToken();
		jwtToken.setExp(new Date(System.currentTimeMillis() + LOGOUT_EXPIRATION_TIME));
		jwtToken.setIat(new Date());
		jwtToken.setJti(jti);
		jwtToken.setIss("Issuer");
		jwtToken.setSub(userDetails.getUsername());
		jwtToken.setTyp("Bearer");
		try {
			jwtTokenDAO.insertJwtToken(jwtToken);
			logger.info("JWT token inserted successfully with JTI: {}", jti);
		} catch (Exception e) {
			logger.error("Failed to insert JWT token with JTI: {}", jti, e);
		}
		logger.info("Token generation and persistence completed for user: {}", userDetails.getUsername());

		return token;
	}

}