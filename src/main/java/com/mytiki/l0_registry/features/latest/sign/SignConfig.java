/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.sign;


import com.mytiki.l0_registry.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(SignConfig.PACKAGE_PATH)
@EntityScan(SignConfig.PACKAGE_PATH)
public class SignConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".sign";

    @Bean
    public SignService signService(@Autowired SignRepository repository){
        return new SignService(repository);
    }
}
