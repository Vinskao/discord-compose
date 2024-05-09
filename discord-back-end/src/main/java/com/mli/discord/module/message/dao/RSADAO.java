package com.mli.discord.module.message.dao;

import java.util.List;

import com.mli.discord.module.message.model.RSAEntity;

public interface RSADAO {

    /**
     * 插入签名记录到数据库。
     * 
     * @param record 要插入的签名记录对象。
     */
    void insertRSA(RSAEntity record);

    List<RSAEntity> findByUsername(String username);

    RSAEntity findByUsernameAndFileName(String username, String name);
}
