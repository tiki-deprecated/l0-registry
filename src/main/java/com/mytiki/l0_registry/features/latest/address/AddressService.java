/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.address;

import com.mytiki.l0_registry.features.latest.id.IdDO;
import com.mytiki.l0_registry.utilities.B64Url;

import java.time.ZonedDateTime;
import java.util.Optional;

public class AddressService {
    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }

    public void save(IdDO id, String address){
        byte[] addressBytes = B64Url.decode(address);
        Optional<AddressDO> found = repository.findByIdAndAddress(id, addressBytes);
        if(found.isPresent()) return;

        AddressDO save = new AddressDO();
        save.setId(id);
        save.setAddress(addressBytes);
        save.setCreated(ZonedDateTime.now());
        repository.save(save);
    }
}
