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
    private List<String> claims;

    @JsonCreator
    public ConfigAOReq(@JsonProperty(required = true) String appId,
                       @JsonProperty URI jwksEndpoint,
                       @JsonProperty List<String> claims) {
        this.appId = appId;
        this.jwksEndpoint = jwksEndpoint;
        this.claims = claims;
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

    public List<String> getClaims() {
        return claims;
    }

    public void setClaims(List<String> claims) {
        this.claims = claims;
    }
}
