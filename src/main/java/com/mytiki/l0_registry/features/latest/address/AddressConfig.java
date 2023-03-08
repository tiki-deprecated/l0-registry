/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.address;


import com.mytiki.l0_registry.utilities.Constants;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(AddressConfig.PACKAGE_PATH)
@EntityScan(AddressConfig.PACKAGE_PATH)
public class AddressConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".address";

    @Bean
    public AddressService addressService(AddressRepository repository){
        return new AddressService(repository);
    }
}
