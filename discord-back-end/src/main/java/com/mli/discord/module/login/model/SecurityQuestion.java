package com.mli.discord.module.login.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Security Questions
 * 
 * @version 1.0
 * @author D3031104
 */
@Schema(description = "安全問題")
public class SecurityQuestion {
    @Schema(hidden = true)
    private int id;
    private String username;
    private String question;
    private String answer;

    public SecurityQuestion() {
    }

    public SecurityQuestion(int id, String username, String question, String answer) {
        this.id = id;
        this.username = username;
        this.question = question;
        this.answer = answer;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public SecurityQuestion id(int id) {
        setId(id);
        return this;
    }

    public SecurityQuestion username(String username) {
        setUsername(username);
        return this;
    }

    public SecurityQuestion question(String question) {
        setQuestion(question);
        return this;
    }

    public SecurityQuestion answer(String answer) {
        setAnswer(answer);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SecurityQuestion)) {
            return false;
        }
        SecurityQuestion securityQuestion = (SecurityQuestion) o;
        return id == securityQuestion.id && Objects.equals(username, securityQuestion.username)
                && Objects.equals(question, securityQuestion.question)
                && Objects.equals(answer, securityQuestion.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, question, answer);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", username='" + getUsername() + "'" +
                ", question='" + getQuestion() + "'" +
                ", answer='" + getAnswer() + "'" +
                "}";
    }

}
