package com.mli.discord.module.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mli.discord.module.login.dao.SecurityQuestionDAO;
import com.mli.discord.module.login.model.SecurityQuestion;

/**
 * 安全問題服務類，用於處理安全問題相關的操作。
 * 
 * @version 1.0
 * @author D3031104
 */
@Transactional
@Service
public class SecurityQuestionService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityQuestionDAO securityQuestionDAO;

    /**
     * 新增安全問題。
     * 
     * @param securityQuestion 要新增的安全問題
     * @return 如果成功新增，返回1；失敗返回0
     */
    public Integer addSecurityQuestion(SecurityQuestion securityQuestion) {
        logger.info("addSecurityQuestion: {}", securityQuestion);
        try {
            securityQuestionDAO.insertSecurityQuestion(securityQuestion);
            logger.info("安全問題成功插入");
            return 1; // 成功插入
        } catch (Exception e) {
            logger.error("插入安全問題時出現異常：{}", e.getMessage());
            return 0; // 插入失败
        }
    }

    /**
     * 修改安全問題
     * 
     * @param securityQuestion 包含要修改的安全問題的詳細信息
     * @return 成功返回1，失敗返回0
     */
    public Integer modifySecurityQuestion(SecurityQuestion securityQuestion) {
        logger.info("modifySecurityQuestion: {}", securityQuestion);

        try {
            Integer result = securityQuestionDAO.modifySecurityQuestion(securityQuestion);
            if (result > 0) {
                logger.info("安全問題修改成功，問題ID: {}", securityQuestion.getId());
            } else {
                logger.info("安全問題修改失敗，問題ID: {}", securityQuestion.getId());
            }
            return result;
        } catch (Exception e) {
            logger.error("修改安全問題時出現異常：{}", e.getMessage());
            return 0; // 修改失敗
        }
    }

    /**
     * 根據用戶名獲取安全問題
     * 
     * @param username 用戶名
     * @return 安全問題文字，若無則返回null
     */
    public String getQuestionByUsername(String username) {
        logger.info("getQuestionByUsername: {}", username);

        try {
            return securityQuestionDAO.getQuestionByUsername(username);
        } catch (Exception e) {
            logger.error("獲取安全問題時出現異常，用戶名：{}，異常信息：{}", username, e.getMessage());
            return null;
        }
    }

    /**
     * 驗證安全問題答案
     * 
     * @param securityQuestion 包含用戶名、問題和答案的安全問題對象
     * @return 如果答案正確，返回true，否則返回false
     */
    public boolean verifyAnswer(SecurityQuestion securityQuestion) {
        logger.info("verifyAnswer: {}", securityQuestion);
        try {
            return securityQuestionDAO.verifyAnswer(securityQuestion) > 0;
        } catch (Exception e) {
            logger.error("驗證安全問題答案時出現異常，用戶名：{}，問題：{}，異常信息：{}",
                    securityQuestion.getUsername(), securityQuestion.getQuestion(), e.getMessage());
            return false;
        }
    }
}
