package com.mli.discord.module.grouping.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mli.discord.module.grouping.dao.RoomDAO;
import com.mli.discord.module.grouping.model.Room;

@Service
public class RoomService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoomDAO roomDAO;

    public List<Room> findAllRoomsByGroupId(int groupId) {
        try {
            return roomDAO.findAllByGroupId(groupId);
        } catch (Exception e) {
            logger.error("An error occurred while fetching rooms by group ID {}: {}", groupId, e.getMessage());

            return null;
        }
    }
}
