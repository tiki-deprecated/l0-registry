/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.address;

import com.mytiki.l0_registry.features.latest.id.IdDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<AddressDO, Long> {
    Optional<AddressDO> findByIdAndAddress(IdDO id, byte[] address);
    void deleteAllById(IdDO id);
}
