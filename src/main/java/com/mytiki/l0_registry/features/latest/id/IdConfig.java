/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.id;


import com.mytiki.l0_registry.features.latest.address.AddressService;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.features.latest.sign.SignService;
import com.mytiki.l0_registry.utilities.Constants;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(IdConfig.PACKAGE_PATH)
@EntityScan(IdConfig.PACKAGE_PATH)
public class IdConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".id";

    @Bean
    public IdController idController(IdService service){
        return new IdController(service);
    }

    @Bean
    public IdService idService(
            IdRepository repository,
            ConfigService configService,
            SignService signService,
            AddressService addressService){
        return new IdService(repository, configService, signService, addressService);
    }
}
