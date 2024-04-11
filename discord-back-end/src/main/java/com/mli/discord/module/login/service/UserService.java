package com.mli.discord.module.login.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mli.discord.module.login.dao.UserDAO;
import com.mli.discord.module.login.model.User;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 使用者服務類，提供使用者相關操作的方法。
 * 
 * @version 1.0
 * @author D3031104
 */
@Transactional
@Service
public class UserService implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userDAO.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            // 解析用户的权限字段，并为每个权限创建GrantedAuthority对象
            List<GrantedAuthority> authorities = Arrays.stream(user.getAuthority().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities // 将权限列表传递给UserDetails对象
            );
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("User not found with username: " + username, e);
        }
    }

    public User findByUsernameAndPassword(String username, String password) {
        logger.info("service, Authenticating User: {}", username);

        try {
            // 从数据库中获取用户存储的加密密码
            User user = userDAO.findEncodedPasswordByUsername(username);

            if (user != null) {
                // 从用户对象中获取存储的加密密码
                String encodedPassword = user.getPassword();

                // 使用PasswordEncoder.matches方法比较用户提交的密码和存储的加密密码
                boolean passwordMatches = passwordEncoder.matches(password, encodedPassword);

                if (passwordMatches) {
                    return user; // 返回用户对象，认证成功
                }
            }

        } catch (EmptyResultDataAccessException e) {
            logger.info("用户不存在", e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * 根據使用者 ID 尋找使用者。
     * 
     * @param userId 使用者 ID
     * @return 匹配給定 ID 的使用者清單，如果找不到則返回空列表
     */
    @Operation(summary = "Find user by user ID")
    public List<User> findById(int userId) {
        logger.info("service, userId {}", userId);

        try {
            return userDAO.findById(userId);
        } catch (EmptyResultDataAccessException e) {
            logger.error("service, User with id {} not found", userId);
            return null;
        }
    }

    @Operation(summary = "更新密碼")
    public boolean updatePassword(String username, String newPassword) {
        logger.info("服務層，正在更新用戶 {} 的密碼", username);

        try {
            boolean updated = userDAO.updatePasswordByUsername(username, passwordEncoder.encode(newPassword)) > 0;
            if (updated) {
                logger.info("密碼更新成功");
            } else {
                logger.info("密碼更新失敗");
            }
            return updated;
        } catch (Exception e) {
            logger.error("更新密碼時出現異常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 查找到的用户，如果未找到则返回 null
     */
    public User findByUsername(String username) {
        logger.info("Searching for user by username: {}", username);
        return userDAO.findByUsername(username);
    }

    public boolean updateUserDetails(String username, LocalDateTime birthday, String interests) {
        logger.info("Service layer, updating user details for: {}", username);
        try {
            int updateCount = userDAO.updateUserDetailsByUsername(username, birthday, interests);
            if (updateCount > 0) {
                logger.info("User details updated successfully");
                return true;
            } else {
                logger.warn("No user found with username: {}", username);
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception occurred while updating user details: {}", e.getMessage());
            return false;
        }
    }

}
