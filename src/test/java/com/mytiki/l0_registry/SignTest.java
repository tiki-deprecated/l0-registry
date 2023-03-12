/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry;

import com.mytiki.l0_registry.features.latest.config.*;
import com.mytiki.l0_registry.features.latest.id.IdDO;
import com.mytiki.l0_registry.features.latest.id.IdRepository;
import com.mytiki.l0_registry.features.latest.id.IdService;
import com.mytiki.l0_registry.features.latest.sign.SignRepository;
import com.mytiki.l0_registry.features.latest.sign.SignService;
import com.mytiki.l0_registry.main.App;
import com.mytiki.l0_registry.utilities.B64Url;
import com.mytiki.l0_registry.utilities.RSAFacade;
import com.mytiki.l0_registry.utilities.SHA3Facade;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"ci", "dev", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignTest {

    @Autowired
    private ConfigService configService;

    @Autowired
    private IdRepository idRepository;

    @Autowired
    private SignService service;

    @Autowired
    private SignRepository repository;

    @Test
    public void Test_Cycle_Success(){
        ConfigDO config = configService.getCreate(UUID.randomUUID().toString());
        IdDO idDO = new IdDO();
        idDO.setConfig(config);
        idDO.setCustomerId(UUID.randomUUID().toString());
        idDO.setCreated(ZonedDateTime.now());
        idDO = idRepository.save(idDO);
        String key = service.cycle(idDO);
        assertNotNull(key);
    }

    @Test
    public void Test_Get_Success(){
        ConfigDO config = configService.getCreate(UUID.randomUUID().toString());
        IdDO idDO = new IdDO();
        idDO.setConfig(config);
        idDO.setCustomerId(UUID.randomUUID().toString());
        idDO.setCreated(ZonedDateTime.now());
        idDO = idRepository.save(idDO);
        String key = service.cycle(idDO);
        String latest = service.get(idDO);
        assertEquals(key, latest);
    }

    @Test
    public void Test_Latest_Success(){
        ConfigDO config = configService.getCreate(UUID.randomUUID().toString());
        IdDO idDO = new IdDO();
        idDO.setConfig(config);
        idDO.setCustomerId(UUID.randomUUID().toString());
        idDO.setCreated(ZonedDateTime.now());
        idDO = idRepository.save(idDO);
        String orig = service.cycle(idDO);
        String cycled = service.cycle(idDO);
        String latest = service.get(idDO);
        assertNotEquals(orig, latest);
        assertEquals(cycled, latest);
    }
}
