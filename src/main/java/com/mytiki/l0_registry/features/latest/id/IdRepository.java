/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.id;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdRepository extends JpaRepository<IdDO, Long> {
    Optional<IdDO> getByCustomerIdAndConfigAppId(String customerId, String appId);
    void deleteByCid(Long cid);
    long countByConfigAppId(String appId);
}
