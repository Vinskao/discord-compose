package com.mli.discord.module.login.dto;

import java.util.Objects;

public class StatusResponse {
    private String status;

    public StatusResponse() {
    }

    public StatusResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StatusResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof StatusResponse)) {
            return false;
        }
        StatusResponse statusResponse = (StatusResponse) o;
        return Objects.equals(status, statusResponse.status);
    }

    @Override
    public String toString() {
        return "{" +
                " status='" + getStatus() + "'" +
                "}";
    }

}
