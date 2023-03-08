/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features;

import com.mytiki.l0_registry.features.latest.address.AddressConfig;
import com.mytiki.l0_registry.features.latest.config.ConfigConfig;
import com.mytiki.l0_registry.features.latest.id.IdConfig;
import com.mytiki.l0_registry.features.latest.sign.SignConfig;
import org.springframework.context.annotation.Import;

@Import({
        ConfigConfig.class,
        IdConfig.class,
        AddressConfig.class,
        SignConfig.class
})
public class FeaturesConfig {}
