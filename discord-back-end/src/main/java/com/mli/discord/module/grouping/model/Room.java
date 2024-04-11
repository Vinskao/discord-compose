package com.mli.discord.module.grouping.model;

import lombok.Data;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 房間實體。
 * 
 * @Author D3031104
 * @version 1.0
 */
@Schema(description = "房間實體")
@Data
public class Room {
	private int id;
	private String name;
	private int groupId;
	private int visibility;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public Room() {
	}

	public Room(int id, String name, int groupId, int visibility) {
		this.id = id;
		this.name = name;
		this.groupId = groupId;
		this.visibility = visibility;
	}

	public Room id(int id) {
		setId(id);
		return this;
	}

	public Room name(String name) {
		setName(name);
		return this;
	}

	public Room groupId(int groupId) {
		setGroupId(groupId);
		return this;
	}

	public Room visibility(int visibility) {
		setVisibility(visibility);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Room)) {
			return false;
		}
		Room room = (Room) o;
		return id == room.id && Objects.equals(name, room.name) && groupId == room.groupId
				&& visibility == room.visibility;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, groupId, visibility);
	}

	@Override
	public String toString() {
		return "{" +
				" id='" + getId() + "'" +
				", name='" + getName() + "'" +
				", groupId='" + getGroupId() + "'" +
				", visibility='" + getVisibility() + "'" +
				"}";
	}

}
