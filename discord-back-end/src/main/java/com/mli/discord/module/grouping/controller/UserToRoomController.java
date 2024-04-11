package com.mli.discord.module.grouping.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mli.discord.module.grouping.dto.RoomIdDTO;
import com.mli.discord.module.grouping.model.UserToRoom;
import com.mli.discord.module.grouping.service.UserToRoomService;
import com.mli.discord.module.login.dto.UsernameDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 使用者與房間控制器，負責處理使用者與房間相關的HTTP請求。
 * 
 * @author D3031104
 * @version 1.0
 */
@RestController
@RequestMapping("/user-to-room")
@Tag(name = "UserToRoomController", description = "使用者與房間控制器")
public class UserToRoomController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserToRoomService userToRoomService;

    /**
     * 將使用者新增到房間。
     *
     * @param userToRoom 包含使用者和房間資訊的實體
     * @return ResponseEntity 表示操作結果的HTTP響應
     */
    @Operation(summary = "將使用者新增到房間")
    @PostMapping("/add")
    public ResponseEntity<?> addUserToRoom(@RequestBody UserToRoom userToRoom) {
        logger.info("addUserToRoom: userToRoom  -", userToRoom.getRoomId());

        try {
            int rowsAffected = userToRoomService.addUserToRoom(userToRoom);
            if (rowsAffected > 0) {
                return ResponseEntity.ok().body("User added to room successfully");
            } else {
                logger.warn("Failed to add user to room: {}", userToRoom);
                return ResponseEntity.badRequest().body("Failed to add user to room");
            }
        } catch (Exception e) {
            logger.error("Exception adding user to room: {}", userToRoom, e);
            return ResponseEntity.badRequest().body("Failed to add user to room");
        }
    }

    /**
     * 從房間中移除使用者。
     *
     * @param userToRoom 包含使用者和房間資訊的實體
     * @return ResponseEntity 表示操作結果的HTTP響應
     */
    @Operation(summary = "從房間中移除使用者")
    @PostMapping("/remove")
    public ResponseEntity<?> removeUserFromRoom(@RequestBody UserToRoom userToRoom) {
        logger.info("removeUserFromRoom: userToRoom  -", userToRoom.getRoomId());

        try {
            int rowsAffected = userToRoomService.removeUserFromRoom(userToRoom);
            if (rowsAffected > 0) {
                return ResponseEntity.ok().body("User removed from room successfully");
            } else {
                logger.warn("Failed to remove user from room: {}", userToRoom);
                return ResponseEntity.badRequest().body("Failed to remove user from room");
            }
        } catch (Exception e) {
            logger.error("Exception removing user from room: {}", userToRoom, e);
            return ResponseEntity.badRequest().body("Failed to remove user from room");
        }
    }

    /**
     * 根據房間ID獲取房間中的所有使用者。
     *
     * @param roomIdDTO 包含房間ID的資料傳輸物件
     * @return 包含房間中所有使用者的響應實體
     */
    @Operation(summary = "根據房間ID獲取房間中的所有使用者")
    @PostMapping("/get-by-room")
    public ResponseEntity<?> getUsersByRoomId(@RequestBody RoomIdDTO roomIdDTO) {
        try {
            List<UserToRoom> users = userToRoomService.getAllUserToRooms(roomIdDTO.getRoomId());
            if (users != null && !users.isEmpty()) {
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            // 日志记录异常信息
            logger.error("Error occurred while fetching users by room ID: ", e);
            // 返回一般性错误信息给客户端
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching users.");
        }
    }

    /**
     * 根據使用者名稱獲取該使用者參與的所有房間。
     *
     * @param usernameDTO 包含使用者名稱的資料傳輸物件
     * @return 包含該使用者參與的所有房間的響應實體
     */
    @Operation(summary = "根據使用者名稱獲取該使用者參與的所有房間")
    @PostMapping("/get-by-username")
    public ResponseEntity<?> getRoomsByUsername(@RequestBody UsernameDTO usernameDTO) {
        try {
            List<UserToRoom> rooms = userToRoomService.getRoomsByUsername(usernameDTO.getUsername());
            if (rooms != null && !rooms.isEmpty()) {
                return ResponseEntity.ok(rooms);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            // 日志记录异常信息
            logger.error("Error occurred while fetching rooms by username: ", e);
            // 返回一般性错误信息给客户端
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching rooms.");
        }
    }

    /**
     * 根據使用者名稱刪除所有該使用者的房間項目。
     *
     * @param usernameDTO 包含使用者名稱的資料傳輸物件
     * @return ResponseEntity 表示操作結果的HTTP響應
     */
    @Operation(summary = "根據使用者名稱刪除所有該使用者的房間項目")
    @PostMapping("/delete-all-by-username")
    public ResponseEntity<?> deleteAllUserEntriesByUsername(@RequestBody UsernameDTO usernameDTO) {
        try {
            int rowsAffected = userToRoomService.deleteAllUserEntriesByUsername(usernameDTO.getUsername());
            if (rowsAffected > 0) {
                return ResponseEntity.ok().body("All entries for the user deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("No entries found for the user");
            }
        } catch (Exception e) {
            logger.error("Exception deleting all entries for username: {}", usernameDTO.getUsername(), e);
            return ResponseEntity.badRequest().body("Failed to delete entries for the user");
        }
    }

}
