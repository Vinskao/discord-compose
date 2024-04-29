package com.mli.discord.module.login.dao;

import com.mli.discord.module.login.model.JwtToken;
import java.util.List;

public interface JwtTokenDAO {
    void insertJwtToken(JwtToken jwtToken);

    List<JwtToken> findAll();

}