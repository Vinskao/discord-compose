package com.mli.discord.module.grouping.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mli.discord.module.grouping.model.Group;
import com.mli.discord.module.grouping.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 群組控制器，負責管理群組的各種操作。
 * 
 * @author D3031104
 * @version 1.0
 */
@Tag(name = "GroupController", description = "群組管理的端點")
@RestController
@RequestMapping("/groups")
public class GroupController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GroupService groupService;

    /**
     * 獲取所有群組的列表。
     * 
     * @return 包含所有群組的響應實體
     */
    @Operation(summary = "檢索所有群組的列表")
    @PostMapping("/find-all-groups")
    public ResponseEntity<List<Group>> getAllGroups() {
        try {
            List<Group> groups = groupService.getAllGroups();
            if (groups.isEmpty()) {
                logger.info("未找到群組");
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("檢索群組失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
