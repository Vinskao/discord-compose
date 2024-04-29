package com.mli.discord.module.login.dao;

import com.mli.discord.module.login.model.SecurityQuestion;

public interface SecurityQuestionDAO {
    Integer insertSecurityQuestion(SecurityQuestion securityQuestion);

    Integer modifySecurityQuestion(SecurityQuestion securityQuestion);

    String getQuestionByUsername(String username);

    Integer verifyAnswer(SecurityQuestion securityQuestion);
}
