package com.mli.discord.module.login.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mli.discord.module.login.dao.UserRepository;
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
     * 處理用戶登入請求。認證成功則建立會話並返回成功響應，認證失敗則返回錯誤訊息。
     *
     * @param loginDTO 包含用戶名和密碼的登入資訊
     * @param request  HttpServletRequest 對象，用於創建會話
     * @return 登入成功或失敗的 ResponseEntity
     */
    @PostMapping("/login")
    @Operation(summary = "用戶登入")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) userService.loadUserByUsername(loginDTO.getUsername());
            HttpSession session = request.getSession(true);
            session.setAttribute("username", user.getUsername());

            return ResponseEntity.ok().body("Login Successful");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed: " + e.getMessage());
        }
    }

    /**
     * 使用者登出
     * 
     * @param request HTTP 請求物件
     * @return ResponseEntity 包含登出成功訊息的回應實體
     */
    @PostMapping("/logout")
    @Operation(summary = "使用者登出")
    @ResponseBody
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new ResponseEntity<>("登出成功", HttpStatus.OK);
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
    @Operation(summary = "驗證session中有沒有使用者")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            String authorities = (String) session.getAttribute("authorities");
            if (username != null && authorities != null) {
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("username", username);
                userInfo.put("authorities", authorities);
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
            // 记录异常信息（例如，使用日志）
            return ResponseEntity.badRequest().body("Error updating user details: " + e.getMessage());
        }
    }

}
