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

import com.mli.discord.module.grouping.dto.GroupIdDTO;
import com.mli.discord.module.grouping.model.Room;
import com.mli.discord.module.grouping.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 群組房間的控制器，負責處理與房間相關的請求。
 *
 * @author D3031104
 * @version 1.0
 */
@RestController
@RequestMapping("/room")
@Tag(name = "RoomController", description = "群組房間控制器")
public class RoomController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoomService roomService;

    /**
     * 根據群組ID獲取所有房間。
     * 
     * @param groupIdDTO 包含群組ID的傳輸物件
     * @return 包含所有房間資訊的響應實體
     */
    @PostMapping("/find-all-rooms")
    @Operation(summary = "根據群組ID獲取所有房間")
    public ResponseEntity<?> getRoomsByGroupId(@RequestBody GroupIdDTO groupIdDTO) {

        try {
            List<Room> rooms = roomService.findAllRoomsByGroupId(groupIdDTO.getGroupId());
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            logger.error("無法根據提供的群組ID檢索房間：{}", groupIdDTO.getGroupId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("無法根據提供的群組ID檢索房間");
        }
    }
}
