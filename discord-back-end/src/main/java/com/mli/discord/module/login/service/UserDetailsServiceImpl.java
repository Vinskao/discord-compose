package com.mli.discord.module.login.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mli.discord.module.login.dao.UserDAO;
import com.mli.discord.module.login.model.User;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 用戶詳細信息服務實現類，實現了Spring Security的UserDetailsService介面，用於根據使用者名稱加載使用者詳細資訊。
 * 
 * @version 1.0
 * @Author D3031104
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    /**
     * 根據使用者名稱加載使用者詳細資訊。
     * 
     * @param username 使用者名稱
     * @return UserDetails 使用者詳細資訊
     * @throws UsernameNotFoundException 如果找不到使用者，則拋出此異常
     */
    @Override
    @Operation(summary = "根據使用者名稱加載使用者詳細資訊")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Can't find user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getAuthority())));
    }
}
