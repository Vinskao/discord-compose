package com.mli.discord.module.login.model;

import java.util.Date;
import java.util.Objects;

public class JwtToken {
    private Integer id;
    private Date exp;
    private Date iat;
    private String jti;
    private String iss;
    private String sub;
    private String typ;

    public JwtToken() {
    }

    public JwtToken(Integer id, Date exp, Date iat, String jti, String iss, String sub, String typ) {
        this.id = id;
        this.exp = exp;
        this.iat = iat;
        this.jti = jti;
        this.iss = iss;
        this.sub = sub;
        this.typ = typ;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getExp() {
        return this.exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    public Date getIat() {
        return this.iat;
    }

    public void setIat(Date iat) {
        this.iat = iat;
    }

    public String getJti() {
        return this.jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getIss() {
        return this.iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSub() {
        return this.sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getTyp() {
        return this.typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public JwtToken id(Integer id) {
        setId(id);
        return this;
    }

    public JwtToken exp(Date exp) {
        setExp(exp);
        return this;
    }

    public JwtToken iat(Date iat) {
        setIat(iat);
        return this;
    }

    public JwtToken jti(String jti) {
        setJti(jti);
        return this;
    }

    public JwtToken iss(String iss) {
        setIss(iss);
        return this;
    }

    public JwtToken sub(String sub) {
        setSub(sub);
        return this;
    }

    public JwtToken typ(String typ) {
        setTyp(typ);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof JwtToken)) {
            return false;
        }
        JwtToken jwtToken = (JwtToken) o;
        return Objects.equals(id, jwtToken.id) && Objects.equals(exp, jwtToken.exp) && Objects.equals(iat, jwtToken.iat)
                && Objects.equals(jti, jwtToken.jti) && Objects.equals(iss, jwtToken.iss)
                && Objects.equals(sub, jwtToken.sub) && Objects.equals(typ, jwtToken.typ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exp, iat, jti, iss, sub, typ);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", exp='" + getExp() + "'" +
                ", iat='" + getIat() + "'" +
                ", jti='" + getJti() + "'" +
                ", iss='" + getIss() + "'" +
                ", sub='" + getSub() + "'" +
                ", typ='" + getTyp() + "'" +
                "}";
    }

}
