/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

public class ConfigAOReq {
    private String appId;
    private URI jwksEndpoint;
    private Boolean verifySubject;

    @JsonCreator
    public ConfigAOReq(@JsonProperty(required = true) String appId,
                       @JsonProperty URI jwksEndpoint,
                       @JsonProperty Boolean verifySubject) {
        this.appId = appId;
        this.jwksEndpoint = jwksEndpoint;
        this.verifySubject = verifySubject;
    }

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
}
