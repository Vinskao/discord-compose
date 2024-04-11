package com.mli.discord.module.grouping.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 這是用於表示群組 ID 的資料傳輸物件。
 *
 * @author D3031104
 * @version 1.0
 */
@Schema(description = "這是表示群組 ID 的資料傳輸物件")
public class GroupIdDTO {
    @Schema(description = "群組 ID")
    private int groupId;

    public GroupIdDTO() {
    }

    public GroupIdDTO(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public GroupIdDTO groupId(int groupId) {
        setGroupId(groupId);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GroupIdDTO)) {
            return false;
        }
        GroupIdDTO groupIdDTO = (GroupIdDTO) o;
        return groupId == groupIdDTO.groupId;
    }

    @Override
    public String toString() {
        return "{" +
                " groupId='" + getGroupId() + "'" +
                "}";
    }

}
