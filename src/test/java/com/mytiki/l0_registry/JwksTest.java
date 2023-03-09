/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry;

import com.mytiki.l0_registry.features.latest.config.ConfigDO;
import com.mytiki.l0_registry.features.latest.jwks.JwksDO;
import com.mytiki.l0_registry.features.latest.jwks.JwksRepository;
import com.mytiki.l0_registry.features.latest.jwks.JwksService;
import com.mytiki.l0_registry.main.App;
import com.mytiki.l0_registry.utilities.Constants;
import com.mytiki.spring_rest_api.ApiException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.CurveBasedJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jwt.JWTClaimsSet;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"ci", "dev", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwksTest {
    @Autowired
    private JwksRepository repository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private MockRestServiceServer mockServer;
    private final String dummyEndpoint = "http://localhost:8080/.well-known/jwks.json";
    private final String jwksRS256 = """
            {
                "keys": [
                    {
                        "p": "30DlK7TX5mHgHQhDHqIT8GB3fXwzMz1kHmLldJLixg3MzRliM-BU8HAs-sZ01TkgQD8vMbv6FZzJEuO4CLoCaGlLKfRPjACvjMF2NrlvCIIFUguRgr3BvyK98mRA4g7ChA_K7_HPuL5v24bjyF_zEtUZJ3sy0rEjyRAx8hAXlE0",
                        "kty": "RSA",
                        "q": "mpS56bvvvis0zFBOUz3OHQFY_sOcsUHqMgsWYW9kxGWCZenmmGqiC7_7igrh303ev3pVXxiZuOjtMSS6ug_Nk5gfLVh7yvtcLEYqdeATBVSLh8nLLagSO7dJBcJTOXG-HATUG78KdeUXv7Mqim0NvEmD_HRVf9kJ8cZZzO8ysKk",
                        "d": "OXDcNUGLH6I5ljfFUCDx7LkN8GvZ4TCo75kM0pnHxbhiSTbVClK0bWf5zMox5JdZ4EuB7G9PWFt41QKNbTYrwxhLJDX9q3do6vGePWH_v3kMC77uZq7vg-r47Y35Uy2wmZQk_jT7KLRNt5fsAbgHv9splT70lMILStLCpqOYPVgfWWdBT-woG3SqiXFPaz_iJkEWoB6gKJ3hfKwYo_av07P3Bnrc_kZ9dgYSiyT8MR5hMwzS8g-96mHwNR1XW9C3fy0IiOdWqinngWBS0cQTDDbo-JQTS39RFxD_pgEIi_ejotpQEFrXlk4WHjwp8nwKgsiMKZToPP6LuFrTvXBXYQ",
                        "e": "AQAB",
                        "use": "sig",
                        "kid": "76e77cea-5ee2-478f-a16e-93eb12c1dd45",
                        "qi": "OJFOfEV5TrcMxlbOD1aH2IXPCiDCaTnt6zdGgVv1Sr1HsZDlQof9U1G5H4oBFpvtFgOMe4mVDpqZatJAzSLcPTRHiCIhCz_pOPqFYJiJ7gzx2ctgCwCtGuIWtbcrgGjATXJ24kjDmvo9gdJx-SbLg269wWQKo3Uh79xj5Az23uQ",
                        "dp": "uaRc7FsUrJ32ni2gonhj3B5bPh1o9dK2zg2uf6EksUwIYQQahMil2MlunZkozaUTDFl-BP0ql44oJWz2O0txdSEZP2nIO8LWN1Un15mampiDlBXKic0Ars9U45o52cAsP2Rie-O3twekPAeOobAnkCFjKVFokYp7F1ZAMejvsoE",
                        "alg": "RS256",
                        "dq": "Iepdu_2jBTtfkzBPbw4RaeXAy-zJNU77_kzWdTxGhJys9oVSNcC3mxJdMxVeJ2tjYumJT5sLJznbyLuBSI9tEGQA-yb9yjRKLeCbMk-efL3m-zz4GiVVEssM93mCXwkop-cbTpckyWchRcsem06AA_6xObOgirNo7iYRz9fvbDk",
                        "n": "hs69goOhErRoVEeAY4FmBWZADpxmGQzhoXcryh6V_hzqAPIA8hc4ATa7OVLjitoUJY_56Y0X-EB55PNTUgkVrJhraPfUq3oox6cfpVrU-PUh2BzEyL3uqb-Pk1_adfm-7Emn_v3I61dEuiZxyFqpLKT9bX3I0_AfXEnwhRhBgCkMf-JXquV5S7KXR8Dy90yi4rErGeqSgZRtcL1DF63f7lMxmumP5jEc_mu4nZfvNXm8M5Ku-x-cDDSr5B9zqBrEDkqSwl197FQygn80rnkRevJBoaF665rDJRKN6Nn32ZMhdKGQ4ZEpyApfzCHQpdcBcBp-vXXzA7DSO35Hr4_W1Q"
                    }
                ]
            }""";
    private final String jwksES256 = """
            {
                "keys": [
                    {
                        "kty": "EC",
                        "d": "RNA2EmYNa8lRkWovqQ65wyrHE-3vmgGZiX8D7LPBfmg",
                        "use": "sig",
                        "crv": "P-256",
                        "kid": "6373263a-8761-4e07-bed0-ffa0d7783741",
                        "x": "XN9VmEwJnQKDjnnBP8E6nBoMP-rPIJ28A_YAC-ZK33M",
                        "y": "v59-3V86_XTFfWBjEgHcKDC7vFBpJXUPOn6GkUiq4Tg",
                        "alg": "ES256"
                    }
                ]
            }""";

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(testRestTemplate.getRestTemplate());
    }

    @Test
    public void Test_NoEnpoint_Success() {
        JwksService service = new JwksService(repository, testRestTemplate.getRestTemplate(),0);
        ConfigDO configDO = new ConfigDO();
        service.guard(UUID.randomUUID().toString(), UUID.randomUUID().toString(), configDO);
    }

    @Test
    public void Test_ES256_Success() throws URISyntaxException, ParseException, JOSEException {
        mockServer.expect(requestTo(new URI(dummyEndpoint)))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jwksES256));
        JwksService service = new JwksService(repository, testRestTemplate.getRestTemplate(), 0);
        ConfigDO configDO = new ConfigDO();
        configDO.setJwksEndpoint(dummyEndpoint);
        JWSObject jwt = buildJwt(null,
                signer(jwksES256, "6373263a-8761-4e07-bed0-ffa0d7783741"), JWSAlgorithm.ES256);
        service.guard(UUID.randomUUID().toString(), jwt.serialize(), configDO);
    }

    @Test
    public void Test_RS256_Success() throws URISyntaxException, ParseException, JOSEException {
        mockServer.expect(requestTo(new URI(dummyEndpoint)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jwksRS256));
        JwksService service = new JwksService(repository, testRestTemplate.getRestTemplate(), 0);
        ConfigDO configDO = new ConfigDO();
        configDO.setJwksEndpoint(dummyEndpoint);
        JWSObject jwt = buildJwt(null,
                signer(jwksRS256, "76e77cea-5ee2-478f-a16e-93eb12c1dd45"), JWSAlgorithm.RS256);
        service.guard(UUID.randomUUID().toString(), jwt.serialize(), configDO);
    }

    @Test
    public void Test_BadUri_Success() throws URISyntaxException, ParseException, JOSEException {
        mockServer.expect(requestTo(new URI(dummyEndpoint)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(""));
        JwksService service = new JwksService(repository, testRestTemplate.getRestTemplate(), 0);
        ConfigDO configDO = new ConfigDO();
        configDO.setJwksEndpoint(dummyEndpoint);
        JWSObject jwt = buildJwt(null,
                signer(jwksRS256, "76e77cea-5ee2-478f-a16e-93eb12c1dd45"), JWSAlgorithm.RS256);
        service.guard(UUID.randomUUID().toString(), jwt.serialize(), configDO);
    }

    @Test
    public void Test_Subject_Success() throws URISyntaxException, ParseException, JOSEException {
        mockServer.expect(requestTo(new URI(dummyEndpoint)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jwksRS256));
        JwksService service = new JwksService(repository, testRestTemplate.getRestTemplate(), 0);
        ConfigDO configDO = new ConfigDO();
        configDO.setJwksEndpoint(dummyEndpoint);
        configDO.setVerifySubject(true);
        String subject = UUID.randomUUID().toString();
        JWSObject jwt = buildJwt(subject,
                signer(jwksRS256, "76e77cea-5ee2-478f-a16e-93eb12c1dd45"), JWSAlgorithm.RS256);
        service.guard(subject, jwt.serialize(), configDO);
    }

    @Test
    public void Test_InvalidSubject_Success() throws URISyntaxException, ParseException, JOSEException {
        mockServer.expect(requestTo(new URI(dummyEndpoint)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jwksRS256));
        JwksService service = new JwksService(repository, testRestTemplate.getRestTemplate(), 0);
        ConfigDO configDO = new ConfigDO();
        configDO.setJwksEndpoint(dummyEndpoint);
        configDO.setVerifySubject(true);
        JWSObject jwt = buildJwt(UUID.randomUUID().toString(),
                signer(jwksRS256, "76e77cea-5ee2-478f-a16e-93eb12c1dd45"), JWSAlgorithm.RS256);

        ApiException ex = assertThrows(ApiException.class,
                () ->  service.guard(UUID.randomUUID().toString(), jwt.serialize(), configDO));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getHttpStatus());
    }

    @Test
    public void Test_Cache_Success() throws URISyntaxException, ParseException, JOSEException {
        String unique = UUID.randomUUID().toString();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(dummyEndpoint + unique)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jwksES256));
        JwksService service = new JwksService(repository, testRestTemplate.getRestTemplate(), 5);
        ConfigDO configDO = new ConfigDO();
        configDO.setJwksEndpoint(dummyEndpoint + unique);
        JWSObject jwt = buildJwt(null,
                signer(jwksES256, "6373263a-8761-4e07-bed0-ffa0d7783741"), JWSAlgorithm.ES256);
        service.guard(UUID.randomUUID().toString(), jwt.serialize(), configDO);

        Optional<JwksDO> cached = repository.getByEndpoint(dummyEndpoint + unique);
        assertTrue(cached.isPresent());
        assertEquals(cached.get().getEndpoint(), dummyEndpoint + unique);
        assertNotNull(cached.get().getCreated());
        assertNotNull(cached.get().getModified());

        service.guard(UUID.randomUUID().toString(), jwt.serialize(), configDO);
        Optional<JwksDO> cached2 = repository.getByEndpoint(dummyEndpoint + unique);
        assertTrue(cached2.isPresent());
        assertEquals(cached.get().getModified(), cached2.get().getModified());
    }

    private JWSObject buildJwt(String sub, JWSSigner signer, JWSAlgorithm algorithm) throws JOSEException {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        JWSObject jws = new JWSObject(
                new JWSHeader
                        .Builder(algorithm)
                        .type(JOSEObjectType.JWT)
                        .build(),
                new Payload(
                        new JWTClaimsSet.Builder()
                                .issuer(Constants.MODULE_DOT_PATH)
                                .issueTime(java.sql.Date.from(now.toInstant()))
                                .expirationTime(Date.from(now.plusSeconds(60).toInstant()))
                                .subject(sub)
                                .build()
                                .toJSONObject()
                ));
        jws.sign(signer);
        return jws;
    }

    private JWSSigner signer(String jwks, String kid) throws ParseException, JOSEException {
        JWKSet jwkSet = JWKSet.parse(jwks);
        JWK jwk = jwkSet.getKeyByKeyId(kid);

        if(jwk.getKeyType().equals(KeyType.EC))
            return new ECDSASigner(jwk.toECKey().toECPrivateKey(), ((CurveBasedJWK) jwk).getCurve());
        else
            return new RSASSASigner(jwk.toRSAKey().toRSAPrivateKey());
    }
}
