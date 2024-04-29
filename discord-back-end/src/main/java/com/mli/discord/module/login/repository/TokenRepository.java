package com.mli.discord.module.login.repository;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

/**
 * TokenRepository 管理用戶的認證令牌。
 * 提供方法來存儲、檢查和移除令牌。
 * 
 * @author D3031104
 * @version 1.0
 */
@Repository
public class TokenRepository {
    private final ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();

    /**
     * 將令牌和相關聯的用戶名存儲在存儲庫中。
     * 
     * @param token    令牌字符串
     * @param username 與令牌關聯的用戶名
     */
    public void storeToken(String token, String username) {
        tokenStore.put(token, username);
    }

    /**
     * 檢查指定的令牌是否存在於存儲庫中。
     * 
     * @param token 要檢查的令牌字符串
     * @return boolean 令牌是否存在
     */
    public boolean isTokenPresent(String token) {
        return tokenStore.containsKey(token);
    }

    /**
     * 從存儲庫中移除指定的令牌。
     * 
     * @param token 要移除的令牌字符串
     */
    public void removeToken(String token) {
        tokenStore.remove(token);
    }
}