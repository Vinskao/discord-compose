package com.mli.discord.module.grouping.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mli.discord.module.grouping.dao.RoomDAO;
import com.mli.discord.module.grouping.model.Room;

/**
 * 房間服務類別。
 * 這個類別提供了處理群組中房間相關業務邏輯的方法。
 *
 * @author D3031104
 * @version 1.0
 */
@Service
public class RoomService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoomDAO roomDAO;

    /**
     * 根據群組ID查找所有房間。
     *
     * @param groupId 群組ID
     * @return 群組中的所有房間列表
     */
    public List<Room> findAllRoomsByGroupId(int groupId) {
        try {
            return roomDAO.findAllByGroupId(groupId);
        } catch (Exception e) {
            logger.error("An error occurred while fetching rooms by group ID {}: {}", groupId, e.getMessage());

            return null;
        }
    }
}
