package com.mli.discord.module.grouping.dao;

import java.util.List;

import com.mli.discord.module.grouping.model.UserToRoom;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 這是一個介面，定義了與用戶與房間關係相關的數據訪問操作。
 * @Author("D3031104")
 * @Version("1.0")
 */
public interface UserToRoomDAO {
    /**
     * 將用戶添加到房間中。
     * 
     * @param userToRoom 用戶與房間關係對象
     * @return 返回受影響的行數
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "將用戶添加到房間中")
    Integer insertUserToRoom(UserToRoom userToRoom);

    /**
     * 從房間中刪除用戶。
     * 
     * @param userToRoom 用戶與房間關係對象
     * @return 返回受影響的行數
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "從房間中刪除用戶")
    Integer deleteUserFromRoom(UserToRoom userToRoom);

    /**
     * 根據房間ID選擇所有用戶。
     * 
     * @param roomId 房間ID
     * @return 返回包含所有用戶的列表
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "根據房間ID選擇所有用戶")
    List<UserToRoom> selectUsersByRoomId(Integer roomId);

    /**
     * 檢查是否存在指定用戶名和房間ID的記錄。
     * 
     * @param username 用戶名
     * @param roomId   房間ID
     * @return 如果存在記錄則返回 1，否則返回 0
     * @throws Exception 如果發生錯誤時拋出異常
     */
    Integer

            existsByUsernameAndRoomId(String username, Integer roomId);

    /**
     * 根據用戶名選擇所有房間。
     * 
     * @param username 用戶名
     * @return 返回包含所有房間的列表
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "根據用戶名選擇所有房間")
    List<UserToRoom> selectRoomsByUsername(String username);

    /**
     * 根據用戶名刪除所有與該用戶有關的房間記錄。
     * 
     * @param username 用戶名
     * @return 返回受影響的行數
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "根據用戶名刪除所有與該用戶有關的房間記錄")
    Integer deleteAllUserEntriesByUsername(String username);

}