package com.mytiki.l0_registry.features.latest.usage;

import java.time.ZonedDateTime;
import java.util.List;

public class UsageAO {
    private ZonedDateTime date;
    private List<UsageAOApp> apps;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public List<UsageAOApp> getApps() {
        return apps;
    }

    public void setApps(List<UsageAOApp> apps) {
        this.apps = apps;
    }
}
