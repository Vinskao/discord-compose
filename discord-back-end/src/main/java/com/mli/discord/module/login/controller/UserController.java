package com.mli.discord.module.login.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mli.discord.module.login.dao.UserRepository;
import com.mli.discord.module.login.dto.AuthenticationResponse;
import com.mli.discord.module.login.dto.LoginDTO;
import com.mli.discord.module.login.dto.RegisterDTO;
import com.mli.discord.module.login.dto.UpdateUserDetailsDTO;
import com.mli.discord.module.login.dto.UserIdDTO;
import com.mli.discord.module.login.dto.UsernameDTO;
import com.mli.discord.module.login.model.User;
import com.mli.discord.module.login.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 處理用戶相關 HTTP 請求的控制器，包括登入、登出、註冊等功能。
 * 
 * @version 1.0
 * @author D3031104
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用戶功能控制器")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Processes login requests. Delegates authentication to the UserService and
     * returns the token or error response.
     *
     * @param loginDTO Contains the username and password for login.
     * @return A ResponseEntity containing either an authentication token or an
     *         error message.
     */
    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        logger.info("Trying to login via UserController");
        try {
            // Authenticate the login request
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Assuming userService.processSuccessfulAuthentication handles JWT creation
            ResponseEntity<AuthenticationResponse> response = userService
                    .processSuccessfulAuthentication(loginDTO.getUsername());

            // Set session attributes if authentication is successful
            HttpSession session = request.getSession(true);
            session.setAttribute("username", authentication.getName());
            session.setAttribute("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(",")));

            return response;
        } catch (AuthenticationException e) {
            logger.error("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationResponse("Login Failed: " + e.getMessage(), null));
        }
    }

    /**
     * 檢查使用者登入狀態
     * 
     * @param request HTTP 請求物件
     * @return 字串表示使用者是否登入的狀態碼，"1" 表示已登入，"0" 表示未登入
     */
    @Operation(summary = "檢查登入狀況")
    @PostMapping("/check-session")
    public String checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("authenticatedUser") != null) {
            // 使用者已登入
            return "1";
        } else {
            // 使用者未登入
            return "0";
        }
    }

    /**
     * 根據使用者 ID 查詢使用者資訊
     * 
     * @param userIdDTO 使用者 ID 的資料傳輸物件
     * @return 匹配給定 ID 的使用者列表，如果找不到則返回空列表
     */
    @Operation(summary = "用使用者 ID 查詢")
    @PostMapping("/find-by-id")
    public List<User> findById(@RequestBody UserIdDTO userIdDTO) {
        logger.info("控制器，使用者 ID 資料傳輸物件: {}", userIdDTO);
        int userId = userIdDTO.getId();
        return userService.findById(userId);
    }

    /**
     * 註冊使用者
     * 
     * @param loginDTO 註冊資訊的資料傳輸物件
     * @return ResponseEntity 包含註冊成功或失敗訊息的回應實體
     */
    @PostMapping("/register")
    @Operation(summary = "註冊使用者")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerDTO) {
        int result = userRepository.createUser(registerDTO.getUsername(), registerDTO.getPassword(),
                registerDTO.getBirthday(), registerDTO.getInterests());

        if (result == 1) {
            return ResponseEntity.ok("使用者成功註冊");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("註冊失敗");
        }
    }

    /**
     * 更新使用者密碼
     * 
     * @param loginDTO 包含使用者名稱和新密碼的資料傳輸物件
     * @return ResponseEntity 包含密碼更新成功或失敗訊息的回應實體
     */
    @PostMapping("/update-password")
    @Operation(summary = "更新使用者密碼")
    public ResponseEntity<?> updatePassword(@RequestBody LoginDTO loginDTO) {
        boolean updated = userService.updatePassword(loginDTO.getUsername(), loginDTO.getPassword());
        if (updated) {
            return ResponseEntity.ok().body("密碼更新成功");
        } else {
            return ResponseEntity.badRequest().body("密碼更新失敗");
        }
    }

    /**
     * 驗證session中有沒有使用者
     * 
     * @param request
     * @return ResponseEntity userInfo
     */
    @PostMapping("/me")
    @Operation(summary = "Get current user's details from JWT")
    public ResponseEntity<?> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Assuming the UserDetails or similar principal object has appropriate methods
            // to fetch needed info
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", userDetails.getUsername());
                userInfo.put("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));

                return ResponseEntity.ok(userInfo);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 查找到的用户信息，如果未找到则返回404状态码
     */
    @PostMapping("/find-by-username")
    @Operation(summary = "根据用户名查找用户")
    public ResponseEntity<?> findByUsername(@RequestBody UsernameDTO usernameDTO) {
        User user = userService.findByUsername(usernameDTO.getUsername());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新用戶資料
     *
     * @param userDetails
     * @return 更新成功的訊息或not found
     */
    @Operation(summary = "Update user details")
    @PostMapping("/update-user-details")
    public ResponseEntity<?> updateUserDetails(@RequestBody UpdateUserDetailsDTO userDetails) {
        try {
            boolean success = userService.updateUserDetails(userDetails.getUsername(), userDetails.getBirthday(),
                    userDetails.getInterests());

            if (success) {
                return ResponseEntity.ok("User details updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or update failed.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user details: " + e.getMessage());
        }
    }

}
