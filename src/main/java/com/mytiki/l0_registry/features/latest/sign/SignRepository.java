/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.sign;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SignRepository extends JpaRepository<SignDO, Long> {}
