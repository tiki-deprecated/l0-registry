/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.id;


import com.mytiki.l0_registry.features.latest.address.AddressService;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.features.latest.jwks.JwksService;
import com.mytiki.l0_registry.features.latest.sign.SignService;
import com.mytiki.l0_registry.features.latest.usage.UsageService;
import com.mytiki.l0_registry.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(IdConfig.PACKAGE_PATH)
@EntityScan(IdConfig.PACKAGE_PATH)
public class IdConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".id";

    @Bean
    public IdController idController(@Autowired IdService service){
        return new IdController(service);
    }

    @Bean
    public IdService idService(
            @Autowired IdRepository repository,
            @Autowired ConfigService configService,
            @Autowired SignService signService,
            @Autowired AddressService addressService,
            @Autowired JwksService jwksService,
            @Autowired UsageService usageService){
        return new IdService(repository, configService, signService, addressService, jwksService, usageService);
    }
}
