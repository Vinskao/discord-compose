package com.mli.discord.module.grouping.model;

import lombok.Data;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 群組實體。
 * 
 * @Author D3031104
 * @version 1.0
 */
@Schema(description = "群組實體")
@Data
public class Group {
    private Integer id;
    private String name;

    public Group() {
    }

    public Group(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group id(Integer id) {
        setId(id);
        return this;
    }

    public Group name(String name) {
        setName(name);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Group)) {
            return false;
        }
        Group group = (Group) o;
        return Objects.equals(id, group.id) && Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", name='" + getName() + "'" +
                "}";
    }

}
