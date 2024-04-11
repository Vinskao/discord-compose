package com.mli.discord.module.login.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @Author D3031104
 * @version 1.0
 */
public enum Authority implements GrantedAuthority {
    ADMIN,
    NORMAL;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
