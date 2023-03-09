/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.jwks;

import com.mytiki.l0_registry.features.latest.config.ConfigConfig;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(JwksConfig.PACKAGE_PATH)
@EntityScan(JwksConfig.PACKAGE_PATH)
public class JwksConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".jwks";

    @Bean
    public JwksService jwksService(
            @Autowired JwksRepository repository,
            @Autowired RestTemplateBuilder restTemplateBuilder){
        return new JwksService(repository, restTemplateBuilder.build(), 3600);
    }
}
