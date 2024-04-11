package com.mli.discord.module.grouping.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mli.discord.module.grouping.dao.GroupDAO;
import com.mli.discord.module.grouping.model.Group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 群組服務。
 * 
 * @Author D3031104
 * @version 1.0
 */
@Service
@Tag(name = "Group Service", description = "群組相關操作")
public class GroupService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GroupDAO groupDAO;

    /**
     * 獲取所有群組。
     * 
     * @return 所有群組列表
     */
    @Operation(summary = "獲取所有群組")
    public List<Group> getAllGroups() {
        try {
            return groupDAO.selectAllGroups();
        } catch (Exception e) {
            logger.error("Error occurred while fetching all groups: {}", e.getMessage());

            return null;
        }
    }
}
