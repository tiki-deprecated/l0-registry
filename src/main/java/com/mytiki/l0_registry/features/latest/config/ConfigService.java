/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.config;

import com.mytiki.l0_registry.l0.auth.L0AuthAOApp;
import com.mytiki.l0_registry.l0.auth.L0AuthAOOrg;
import com.mytiki.l0_registry.l0.auth.L0AuthAOToken;
import com.mytiki.l0_registry.l0.auth.L0AuthService;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class ConfigService {
    private final ConfigRepository repository;
    private final L0AuthService l0AuthService;

    public ConfigService(ConfigRepository repository, L0AuthService l0AuthService) {
        this.repository = repository;
        this.l0AuthService = l0AuthService;
    }

    public ConfigAORsp get(String appId){
        Optional<ConfigDO> found = repository.getByAppId(appId);
        return found.map(this::toRsp).orElse(null);
    }

    public ConfigDO getBilling(String appId){
        Optional<ConfigDO> found = repository.getByAppId(appId);
        if(found.isEmpty())
            throw new ApiExceptionBuilder(HttpStatus.BAD_REQUEST)
                    .detail("Invalid appId")
                    .properties("appId", appId)
                    .build();
        if(found.get().getBillingId() == null){
            L0AuthAOToken token = l0AuthService.getToken(List.of("auth:internal:read"));
            L0AuthAOApp app = l0AuthService.getApp(appId, token.getAccessToken());
            L0AuthAOOrg org = l0AuthService.getOrg(app.getOrgId(), token.getAccessToken());
            ConfigDO update = found.get();
            update.setBillingId(org.getBillingId());
            return repository.save(update);
        }else return found.get();
    }

    public ConfigAORsp modify(ConfigAOReq req){
        Optional<ConfigDO> found = repository.getByAppId(req.getAppId());
        ZonedDateTime now = ZonedDateTime.now();
        ConfigDO save;
        if (found.isPresent()) save = found.get();
        else {
            save = new ConfigDO();
            save.setAppId(req.getAppId());
            save.setCreated(now);
        }
        if(req.getJwksEndpoint() != null)
            save.setJwksEndpoint(req.getJwksEndpoint().toString());
        if(req.getVerifySubject() != null)
            save.setVerifySubject(req.getVerifySubject());
        save.setModified(now);
        return toRsp(repository.save(save));
    }

    public ConfigDO getCreate(String appId){
        Optional<ConfigDO> found = repository.getByAppId(appId);
        if(found.isEmpty()){
            ConfigDO save = new ConfigDO();
            ZonedDateTime now = ZonedDateTime.now();
            save.setAppId(appId);
            save.setCreated(now);
            save.setModified(now);
            return repository.save(save);
        }else return found.get();
    }

    private ConfigAORsp toRsp(ConfigDO cfg) {
        ConfigAORsp rsp = new ConfigAORsp();
        rsp.setAppId(cfg.getAppId());
        rsp.setCreated(cfg.getCreated());
        rsp.setModified(cfg.getModified());
        if(cfg.getJwksEndpoint() != null){
            try {
                rsp.setJwksEndpoint(new URI(cfg.getJwksEndpoint()));
            }
            catch (URISyntaxException e) {
                throw new ApiExceptionBuilder(HttpStatus.UNPROCESSABLE_ENTITY)
                        .message("Invalid Endpoint")
                        .help("JWKS endpoint requires a valid URI")
                        .properties("jwksEndpoint", cfg.getJwksEndpoint())
                        .build();
            }
        }
        rsp.setVerifySubject(cfg.getVerifySubject());
        return rsp;
    }
}
