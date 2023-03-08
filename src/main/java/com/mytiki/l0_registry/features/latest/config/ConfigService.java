/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.config;

import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class ConfigService {
    private final ConfigRepository repository;

    public ConfigService(ConfigRepository repository) {
        this.repository = repository;
    }

    public ConfigAORsp get(String appId){
        Optional<ConfigDO> found = repository.getByAppId(appId);
        return found.map(this::toRsp).orElse(null);
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
        if(req.getClaims() != null)
            save.setClaims(String.join(",", req.getClaims()));
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
        if(cfg.getClaims() != null)
            rsp.setClaims(List.of(cfg.getClaims().split(",")));
        return rsp;
    }
}
