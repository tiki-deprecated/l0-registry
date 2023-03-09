/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.config;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

public class ConfigAORsp {
    private String appId;
    private URI jwksEndpoint;
    private Boolean verifySubject;
    private ZonedDateTime created;
    private ZonedDateTime modified;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public URI getJwksEndpoint() {
        return jwksEndpoint;
    }

    public void setJwksEndpoint(URI jwksEndpoint) {
        this.jwksEndpoint = jwksEndpoint;
    }

    public Boolean getVerifySubject() {
        return verifySubject;
    }

    public void setVerifySubject(Boolean verifySubject) {
        this.verifySubject = verifySubject;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getModified() {
        return modified;
    }

    public void setModified(ZonedDateTime modified) {
        this.modified = modified;
    }
}
