/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.jwks;

import com.mytiki.l0_registry.features.latest.config.ConfigDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwksRepository extends JpaRepository<JwksDO, Long> {
    Optional<JwksDO> getByEndpoint(String endpoint);
}
