package com.mli.discord.module.grouping.dao;

import java.util.List;

import com.mli.discord.module.grouping.model.Room;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 這是一個介面，定義了與房間相關的數據訪問操作。
 * @Author("D3031104")
 * @Version("1.0")
 */
public interface RoomDAO {
    /**
     * 這是一個示例方法，用於根據群組ID查找所有房間。
     * 
     * @param groupId 群組ID
     * @return 返回包含所有房間的列表
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "根據群組ID查找所有房間")
    List<Room> findAllByGroupId(int groupId);
}
