package com.mli.discord.module.grouping.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mli.discord.module.grouping.dao.UserToRoomDAO;
import com.mli.discord.module.grouping.model.UserToRoom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 使用者與房間服務。
 * 
 * @Author D3031104
 * @version 1.0
 */
@Tag(name = "使用者與房間服務", description = "使用者與房間相關操作")
@Service
public class UserToRoomService {
    @Autowired
    private UserToRoomDAO userToRoomDAO;

    /**
     * 添加使用者到房間。
     * 
     * @param newUserToRoom 新使用者與房間關聯實體
     * @return 添加的使用者與房間關聯實體數量
     */
    @Operation(summary = "添加使用者到房間")
    public Integer addUserToRoom(UserToRoom newUserToRoom) {
        // 首先，检查用户是否已在其他房间中
        List<UserToRoom> existingRooms = getRoomsByUsername(newUserToRoom.getUsername());

        // 如果用户已经在一个或多个房间中，从这些房间中移除他们
        if (!existingRooms.isEmpty()) {
            for (UserToRoom existingRoom : existingRooms) {
                userToRoomDAO.deleteUserFromRoom(existingRoom);
            }
        }

        // 然后，将用户加入新房间
        return userToRoomDAO.insertUserToRoom(newUserToRoom);
    }

    /**
     * 從房間中移除使用者。
     * 
     * @param userToRoom 使用者與房間關聯實體
     * @return 移除的使用者與房間關聯實體數量
     */
    @Operation(summary = "從房間中移除使用者")
    public Integer removeUserFromRoom(UserToRoom userToRoom) {
        return userToRoomDAO.deleteUserFromRoom(userToRoom);
    }

    /**
     * 根據房間ID獲取所有使用者與房間的關聯。
     * 
     * @param roomId 房間ID
     * @return 符合條件的使用者與房間關聯列表
     */
    @Operation(summary = "根據房間ID獲取所有使用者與房間的關聯")
    public List<UserToRoom> getAllUserToRooms(Integer roomId) {
        return userToRoomDAO.selectUsersByRoomId(roomId);
    }

    /**
     * 根據使用者名稱獲取所有與之相關的房間。
     * 
     * @param username 使用者名稱
     * @return 符合條件的使用者與房間關聯列表
     */
    @Operation(summary = "根據使用者名稱獲取所有與之相關的房間")
    public List<UserToRoom> getRoomsByUsername(String username) {
        return userToRoomDAO.selectRoomsByUsername(username);
    }

    /**
     * 根据使用者名稱刪除所有使用者與房間的關聯。
     * 
     * @param username 使用者名稱
     * @return 刪除的使用者與房間關聯數量
     */
    @Operation(summary = "根據使用者名稱刪除所有使用者與房間的關聯")
    public Integer deleteAllUserEntriesByUsername(String username) {
        return userToRoomDAO.deleteAllUserEntriesByUsername(username);
    }

    // /**
    // * 检查给定的用户是否已在指定的房间中。
    // *
    // * @param username 用户名
    // * @param roomId 房间ID
    // * @return 如果用户已在房间中，则返回1；否则返回0。
    // */
    // private Integer checkIfUserInRoom(String username, Integer roomId) {
    // // 调用UserToRoomDAO接口中的方法来检查数据库中是否存在相应的记录
    // return userToRoomDAO.existsByUsernameAndRoomId(username, roomId);
    // }

}
