package com.hpe.itsma.itsmaInstaller.bean;

import org.springframework.stereotype.Component;

/**
 * Created by tianlib on 9/5/2017.
 */
@Component
public class UpdateProcessStatus {
    private String phase;
    private String detail;

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String toJson() {
        return "{\"phase\": \"" + phase + "\", \"detail\": " + detail + "}";
    }
}
