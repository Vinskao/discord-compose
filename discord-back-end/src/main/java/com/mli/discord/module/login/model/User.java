package com.mli.discord.module.login.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 用户
 * 
 * @version 1.0
 * @author D3031104
 */
@Builder
@Schema(description = "用戶")
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;
	@Schema(hidden = true)
    private Integer id;
    @Schema(description = "用戶密碼")
    private String password;
    @Schema(description = "用戶名稱")
    private String username;
    @Schema(description = "用戶權限")
    private String authority;
    @Schema(description = "用户生日")
    private LocalDateTime birthday;
    private String interests;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.authority));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User() {
    }

    public User(Integer id, String password, String username, String authority, LocalDateTime birthday,
            String interests) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.authority = authority;
        this.birthday = birthday;
        this.interests = interests;
    }

    public User(String password, String username, String authority, LocalDateTime birthday,
            String interests) {
        this.password = password;
        this.username = username;
        this.authority = authority;
        this.birthday = birthday;
        this.interests = interests;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public LocalDateTime getBirthday() {
        return this.birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public String getInterests() {
        return this.interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public User id(Integer id) {
        setId(id);
        return this;
    }

    public User password(String password) {
        setPassword(password);
        return this;
    }

    public User username(String username) {
        setUsername(username);
        return this;
    }

    public User authority(String authority) {
        setAuthority(authority);
        return this;
    }

    public User birthday(LocalDateTime birthday) {
        setBirthday(birthday);
        return this;
    }

    public User interests(String interests) {
        setInterests(interests);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(password, user.password)
                && Objects.equals(username, user.username) && Objects.equals(authority, user.authority)
                && Objects.equals(birthday, user.birthday) && Objects.equals(interests, user.interests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, username, authority, birthday, interests);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", password='" + getPassword() + "'" +
                ", username='" + getUsername() + "'" +
                ", authority='" + getAuthority() + "'" +
                ", birthday='" + getBirthday() + "'" +
                ", interests='" + getInterests() + "'" +
                "}";
    }

}
