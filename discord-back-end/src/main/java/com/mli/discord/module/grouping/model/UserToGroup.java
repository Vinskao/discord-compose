package com.mli.discord.module.grouping.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用戶與群組關聯實體。
 * 
 * @Author D3031104
 * @version 1.0
 */
@Data
@Schema(description = "用戶與群組關聯實體")
public class UserToGroup {
    private String username;
    private int groupId;

    public UserToGroup() {
    }

    public UserToGroup(String username, int groupId) {
        this.username = username;
        this.groupId = groupId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public UserToGroup username(String username) {
        setUsername(username);
        return this;
    }

    public UserToGroup groupId(int groupId) {
        setGroupId(groupId);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserToGroup)) {
            return false;
        }
        UserToGroup userToGroup = (UserToGroup) o;
        return Objects.equals(username, userToGroup.username) && groupId == userToGroup.groupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, groupId);
    }

    @Override
    public String toString() {
        return "{" +
                " username='" + getUsername() + "'" +
                ", groupId='" + getGroupId() + "'" +
                "}";
    }

}
