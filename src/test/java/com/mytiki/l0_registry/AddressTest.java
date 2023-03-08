/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry;

import com.mytiki.l0_registry.features.latest.address.AddressDO;
import com.mytiki.l0_registry.features.latest.address.AddressRepository;
import com.mytiki.l0_registry.features.latest.address.AddressService;
import com.mytiki.l0_registry.features.latest.config.ConfigDO;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.features.latest.id.IdDO;
import com.mytiki.l0_registry.features.latest.id.IdRepository;
import com.mytiki.l0_registry.main.App;
import com.mytiki.l0_registry.utilities.B64Url;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"ci", "dev", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddressTest {
    @Autowired
    private ConfigService configService;

    @Autowired
    private IdRepository idRepository;

    @Autowired
    private AddressService service;

    @Autowired
    private AddressRepository repository;

    @Test
    public void Test_SaveOne_Success(){
        ConfigDO config = configService.getCreate(UUID.randomUUID().toString());
        IdDO idDO = new IdDO();
        idDO.setConfig(config);
        idDO.setCustomerId(UUID.randomUUID().toString());
        idDO.setCreated(ZonedDateTime.now());
        idDO = idRepository.save(idDO);
        String address = UUID.randomUUID().toString();
        service.save(idDO, address);
        Optional<AddressDO> addressDO = repository.findByIdAndAddress(idDO, B64Url.decode(address));
        assertTrue(addressDO.isPresent());
        assertEquals(address, B64Url.encode(addressDO.get().getAddress()));
        assertNotNull(addressDO.get().getCreated());
        assertEquals(idDO.getCid(), addressDO.get().getId().getCid());
    }

    @Test
    public void Test_SaveMultiple_Success(){
        ConfigDO config = configService.getCreate(UUID.randomUUID().toString());
        IdDO idDO = new IdDO();
        idDO.setConfig(config);
        idDO.setCustomerId(UUID.randomUUID().toString());
        idDO.setCreated(ZonedDateTime.now());
        idDO = idRepository.save(idDO);

        int numAddresses = 5;
        List<String> addresses = new ArrayList<>(numAddresses);
        for(int i=0; i<numAddresses; i++){
            String address = UUID.randomUUID().toString();
            service.save(idDO, address);
            addresses.add(address);
        }
        for(int i=0; i<numAddresses; i++){
            Optional<AddressDO> addressDO = repository.findByIdAndAddress(idDO, B64Url.decode(addresses.get(i)));
            assertTrue(addressDO.isPresent());
            assertEquals(addresses.get(i), B64Url.encode(addressDO.get().getAddress()));
            assertNotNull(addressDO.get().getCreated());
            assertEquals(idDO.getCid(), addressDO.get().getId().getCid());
        }
    }

}
