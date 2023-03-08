/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.sign;

import com.mytiki.l0_registry.features.latest.id.IdDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignRepository extends JpaRepository<SignDO, Long> {
    Optional<SignDO> getFirstByIdOrderByCreatedDesc(IdDO id);
}
