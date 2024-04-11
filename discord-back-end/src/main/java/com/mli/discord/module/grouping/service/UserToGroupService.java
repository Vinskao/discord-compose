package com.mli.discord.module.grouping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mli.discord.module.grouping.dao.UserToGroupDAO;
import com.mli.discord.module.grouping.model.UserToGroup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 使用者與房間關聯服務。
 * 
 * @Author D3031104
 * @version 1.0
 */
@Tag(name = "使用者與房間服務", description = "使用者與房間相關操作")
@Service

public class UserToGroupService {
    @Autowired
    private UserToGroupDAO userToGroupDAO;

    /**
     * 添加使用者到房間。
     * 
     * @param newUserToGroup 新使用者與房間關聯實體
     * @return 添加的使用者與房間關聯實體數量
     */
    @Operation(summary = "添加使用者到房間")
    public Integer addUserToGroup(UserToGroup userToGroup) {
        return userToGroupDAO.insertUserToGroup(userToGroup);
    }

    /**
     * 從房間中移除使用者。
     * 
     * @param userToGroup 使用者與房間關聯實體
     * @return 移除的使用者與房間關聯實體數量
     */
    @Operation(summary = "從房間中移除使用者")
    public Integer removeUserFromGroup(UserToGroup userToGroup) {
        return userToGroupDAO.deleteUserFromGroup(userToGroup);
    }

    /**
     * 根據房間ID獲取所有使用者與房間的關聯。
     * 
     * @param roomId 房間ID
     * @return 符合條件的使用者與房間關聯列表
     */
    @Operation(summary = "根據房間ID獲取所有使用者與房間的關聯")
    public Integer deleteAllUserEntriesByUsername(String username) {
        return userToGroupDAO.deleteAllUserEntriesByUsername(username);
    }
}
