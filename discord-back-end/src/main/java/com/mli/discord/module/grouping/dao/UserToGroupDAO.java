package com.mli.discord.module.grouping.dao;

import com.mli.discord.module.grouping.model.UserToGroup;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 這是一個介面，定義了與用戶與群組關係相關的數據訪問操作。
 * 
 * @Author D3031104
 * @Version 1.0
 */
public interface UserToGroupDAO {
    /**
     * 將用戶添加到群組中。
     * 
     * @param userToGroup 用戶與群組關係對象
     * @return 返回受影響的行數
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "將用戶添加到群組中")
    Integer insertUserToGroup(UserToGroup userToGroup);

    /**
     * 從群組中刪除用戶。
     * 
     * @param userToGroup 用戶與群組關係對象
     * @return 返回受影響的行數
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "從群組中刪除用戶")
    Integer deleteUserFromGroup(UserToGroup userToGroup);

    /**
     * 根據用戶名刪除所有與該用戶有關的群組記錄。
     * 
     * @param username 用戶名
     * @return 返回受影響的行數
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "根據用戶名刪除所有與該用戶有關的群組記錄")
    Integer deleteAllUserEntriesByUsername(String username);

}
