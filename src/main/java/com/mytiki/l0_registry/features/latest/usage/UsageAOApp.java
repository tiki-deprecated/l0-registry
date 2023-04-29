package com.mytiki.l0_registry.features.latest.usage;

import java.time.ZonedDateTime;

public class UsageAOApp {
    private String appId;
    private long total;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
