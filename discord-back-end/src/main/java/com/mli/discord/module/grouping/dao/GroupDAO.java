package com.mli.discord.module.grouping.dao;

import java.util.List;

import com.mli.discord.module.grouping.model.Group;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 這是一個介面，定義了與群組相關的數據訪問操作。
 * @Author("D3031104")
 * @Version("1.0")
 */
public interface GroupDAO {
    /**
     * 這是一個示例方法，用於選擇所有群組。
     * 
     * @return 返回包含所有群組的列表
     * @throws Exception 如果發生錯誤時拋出異常
     */
    @Operation(summary = "選擇所有群組")
    List<Group> selectAllGroups();
}
