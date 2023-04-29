/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.config;


import com.mytiki.l0_registry.l0.auth.L0AuthService;
import com.mytiki.l0_registry.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(ConfigConfig.PACKAGE_PATH)
@EntityScan(ConfigConfig.PACKAGE_PATH)
public class ConfigConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".config";

    @Bean
    public ConfigController configController(@Autowired ConfigService service){
        return new ConfigController(service);
    }

    @Bean
    public ConfigService configService(
            @Autowired ConfigRepository repository,
            @Autowired L0AuthService l0AuthService){
        return new ConfigService(repository, l0AuthService);
    }
}
