package com.mli.discord.module.login.dao;

import com.mli.discord.module.login.model.SecurityQuestion;

/**
 * 安全問題資料訪問對象介面。
 * 這個介面定義了一系列操作來管理安全問題。
 *
 * @author D3031104
 * @version 1.0
 */
public interface SecurityQuestionDAO {
    /**
     * 插入安全問題。
     *
     * @param securityQuestion 要插入的安全問題
     * @return 插入成功的行數
     */
    Integer insertSecurityQuestion(SecurityQuestion securityQuestion);

    /**
     * 修改安全問題。
     *
     * @param securityQuestion 要修改的安全問題
     * @return 修改成功的行數
     */
    Integer modifySecurityQuestion(SecurityQuestion securityQuestion);

    /**
     * 根據使用者名稱獲取安全問題。
     *
     * @param username 使用者名稱
     * @return 對應使用者的安全問題
     */
    String getQuestionByUsername(String username);

    /**
     * 驗證安全問題的答案。
     *
     * @param securityQuestion 要驗證的安全問題
     * @return 驗證結果，1表示驗證通過，0表示驗證失敗
     */
    Integer verifyAnswer(SecurityQuestion securityQuestion);
}
