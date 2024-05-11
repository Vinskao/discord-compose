package com.mli.discord.module.message.dto;

import java.util.Objects;

public class UserFileDTO {
    private String fileName;
    private String username;

    public UserFileDTO() {
    }

    public UserFileDTO(String fileName, String username) {
        this.fileName = fileName;
        this.username = username;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserFileDTO fileName(String fileName) {
        setFileName(fileName);
        return this;
    }

    public UserFileDTO username(String username) {
        setUsername(username);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserFileDTO)) {
            return false;
        }
        UserFileDTO userFileDTO = (UserFileDTO) o;
        return Objects.equals(fileName, userFileDTO.fileName) && Objects.equals(username, userFileDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, username);
    }

    @Override
    public String toString() {
        return "{" +
                " fileName='" + getFileName() + "'" +
                ", username='" + getUsername() + "'" +
                "}";
    }

}
