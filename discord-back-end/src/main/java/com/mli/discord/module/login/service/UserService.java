package com.mli.discord.module.login.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mli.discord.module.login.dao.UserDAO;
import com.mli.discord.module.login.dto.AuthenticationResponse;
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
    private JwtService jwtService;

    @Autowired
    private UserDAO userDAO;

    /**
     * 處理成功的身份驗證後，生成JWT令牌並返回。
     * 
     * @param username 用戶名
     * @return ResponseEntity 包含令牌和操作狀態
     */
    public ResponseEntity<AuthenticationResponse> processSuccessfulAuthentication(String username) {
        logger.info("Processing successful authentication for: {}", username);
        UserDetails userDetails = loadUserByUsername(username);
        // Generate the JWT token
        String jwtToken = jwtService.generateAndPersistToken(userDetails);
        AuthenticationResponse response = new AuthenticationResponse("Success", jwtToken);
        return ResponseEntity.ok(response);
    }

    /**
     * 根據用戶名加載用戶詳情，實現 UserDetailsService 介面。
     * 
     * @param username 用戶名
     * @return UserDetails 包含用戶的安全信息
     * @throws UsernameNotFoundException 當用戶不存在時拋出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userDAO.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            List<GrantedAuthority> authorities = Arrays.stream(user.getAuthority().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("User not found with username: " + username, e);
        }
    }

    /**
     * 根據用戶名和密碼驗證用戶，並返回用戶信息。
     * 
     * @param username 用戶名
     * @param password 密碼
     * @return User 匹配的用戶，如果密碼錯誤則返回 null
     */
    public User findByUsernameAndPassword(String username, String password) {
        logger.info("service, Authenticating User: {}", username);

        try {
            User user = userDAO.findEncodedPasswordByUsername(username);

            if (user != null) {
                String encodedPassword = user.getPassword();

                boolean passwordMatches = passwordEncoder.matches(password, encodedPassword);

                if (passwordMatches) {
                    return user;
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

    /**
     * 更新用戶的密碼。
     * 
     * @param username    用戶名
     * @param newPassword 新密碼
     * @return boolean 表示是否更新成功
     */
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
     * 根據用戶名查找用戶。
     *
     * @param username 用戶名
     * @return User 查找到的用戶，如果未找到則拋出UsernameNotFoundException
     */
    public User findByUsername(String username) {
        logger.info("Searching for user by username: {}", username);
        return userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * 更新用戶詳情，如生日和興趣。
     *
     * @param username  用戶名
     * @param birthday  生日
     * @param interests 興趣
     * @return boolean 表示更新是否成功
     */
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
