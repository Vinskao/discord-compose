package com.mli.discord.module.login.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mli.discord.module.login.dto.UsernameDTO;
import com.mli.discord.module.login.model.SecurityQuestion;
import com.mli.discord.module.login.service.SecurityQuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 處理安全問題相關HTTP請求的控制器。
 * 
 * @version 1.0
 * @author D3031104
 */
@RestController
@Tag(name = "SecurityQuestionController", description = "安全問題控制器")
public class SecurityQuestionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityQuestionService securityQuestionService;

    /**
     * 添加安全问题
     * 
     * @param securityQuestion 安全问题实体对象
     * @return ResponseEntity 包含操作结果的响应实体
     */
    @Operation(summary = "添加安全问题")
    @PostMapping("/add-security-question")
    public ResponseEntity<String> addSecurityQuestion(@RequestBody SecurityQuestion securityQuestion) {
        logger.info("securityQuestion: {}", securityQuestion);

        try {
            Integer result = securityQuestionService.addSecurityQuestion(securityQuestion);
            if (result > 0) {
                logger.info("安全問題新增成功");
                return new ResponseEntity<>("安全問題新增成功", HttpStatus.OK);
            } else {
                logger.info("安全問題新增失敗");
                return new ResponseEntity<>("安全問題新增失敗", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("新增安全問題時出現異常：{}", e.getMessage());
            return new ResponseEntity<>("新增安全問題失敗", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 修改安全問題
     * 
     * @param securityQuestion 安全問題實體對象，包含要修改的詳細信息
     * @return ResponseEntity 包含操作結果的響應實體
     */
    @Operation(summary = "修改安全問題")
    @PostMapping("/modify-security-question")
    public ResponseEntity<String> modifySecurityQuestion(@RequestBody SecurityQuestion securityQuestion) {
        logger.info("securityQuestion: {}", securityQuestion);
        try {
            Integer result = securityQuestionService.modifySecurityQuestion(securityQuestion);
            if (result > 0) {
                logger.info("安全問題修改成功");
                return new ResponseEntity<>("安全問題修改成功", HttpStatus.OK);
            } else {
                logger.info("安全問題修改失敗");
                return new ResponseEntity<>("安全問題修改失敗", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("修改安全問題時出現異常：{}", e.getMessage());
            return new ResponseEntity<>("修改安全問題失敗", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 通過用戶名獲取安全問題
     * 
     * @param usernameDTO 用戶名DTO
     * @return ResponseEntity 包含安全問題的響應實體
     */
    @Operation(summary = "獲取安全問題")
    @PostMapping("/get-question")
    public ResponseEntity<String> getQuestionByUsername(@RequestBody UsernameDTO usernameDTO) {
        logger.info("usernameDTO: {}", usernameDTO);
        try {
            String question = securityQuestionService.getQuestionByUsername(usernameDTO.getUsername());
            if (question != null) {
                return ResponseEntity.ok(question);
            } else {
                // 當用戶名找不到對應的安全問題時，返回一個具體的錯誤訊息
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("無此使用者，請確認用戶名結尾@mli.com");
            }
        } catch (Exception e) {
            // 處理其他可能的異常情況
            logger.error("處理請求時出錯：", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("伺服器錯誤，請稍後再試");
        }
    }

    /**
     * 驗證安全問題答案
     * 
     * @param securityQuestion 安全問題實體對象，包含要驗證的答案
     * @return ResponseEntity 包含驗證結果的響應實體
     */
    @Operation(summary = "驗證安全問題答案")
    @PostMapping("/verify-answer")
    public ResponseEntity<Boolean> verifyAnswer(@RequestBody SecurityQuestion securityQuestion) {
        logger.info("securityQuestion: {}", securityQuestion);

        boolean isCorrect = securityQuestionService.verifyAnswer(securityQuestion);
        return ResponseEntity.ok(isCorrect);
    }
}
