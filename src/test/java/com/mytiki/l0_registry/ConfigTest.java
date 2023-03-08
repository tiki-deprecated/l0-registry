/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry;

import com.mytiki.l0_registry.features.latest.config.ConfigAOReq;
import com.mytiki.l0_registry.features.latest.config.ConfigAORsp;
import com.mytiki.l0_registry.features.latest.config.ConfigRepository;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.main.App;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"ci", "dev", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigTest {

    @Autowired
    private ConfigRepository repository;

    @Autowired
    private ConfigService service;

    @Test
    public void Test_Create_Success(){
        ZonedDateTime start = ZonedDateTime.now();
        ConfigAOReq req = new ConfigAOReq();
        req.setAppId(UUID.randomUUID().toString());
        ConfigAORsp rsp = service.modify(req);
        assertEquals(req.getAppId(), rsp.getAppId());
        assertTrue(rsp.getCreated().isAfter(start));
        assertTrue(rsp.getModified().isAfter(start));
        assertNull(rsp.getClaims());
        assertNull(rsp.getJwksEndpoint());
    }

    @Test
    public void Test_Get_Success(){
        ConfigAOReq req = new ConfigAOReq();
        req.setAppId(UUID.randomUUID().toString());
        service.modify(req);

        ConfigAORsp rsp = service.get(req.getAppId());
        assertEquals(req.getAppId(), rsp.getAppId());
        assertNotNull(rsp.getModified());
        assertNotNull(rsp.getCreated());
        assertNull(rsp.getClaims());
        assertNull(rsp.getJwksEndpoint());
    }

    @Test
    public void Test_Modify_Endpoint_Success() throws URISyntaxException {
        ConfigAOReq req = new ConfigAOReq();
        req.setAppId(UUID.randomUUID().toString());
        ConfigAORsp orig = service.modify(req);
        assertNull(orig.getJwksEndpoint());

        URI endpoint = new URI("mytiki.com");
        req.setJwksEndpoint(endpoint);
        ConfigAORsp update = service.modify(req);
        assertEquals(endpoint, update.getJwksEndpoint());
        assertNotEquals(orig.getModified(), update.getModified());
    }

    @Test
    public void Test_Modify_SingleClaim_Success() {
        ConfigAOReq req = new ConfigAOReq();
        req.setAppId(UUID.randomUUID().toString());
        ConfigAORsp orig = service.modify(req);
        assertNull(orig.getJwksEndpoint());

        List<String> claims = List.of("sub");
        req.setClaims(claims);
        ConfigAORsp update = service.modify(req);
        assertEquals(claims, update.getClaims());
        assertNotEquals(orig.getModified(), update.getModified());
    }

    @Test
    public void Test_Modify_MultipleClaims_Success() {
        ConfigAOReq req = new ConfigAOReq();
        req.setAppId(UUID.randomUUID().toString());
        ConfigAORsp orig = service.modify(req);
        assertNull(orig.getJwksEndpoint());

        List<String> claims = List.of("sub", "aud");
        req.setClaims(claims);
        ConfigAORsp update = service.modify(req);
        assertEquals(claims, update.getClaims());
        assertNotEquals(orig.getModified(), update.getModified());
    }
}
