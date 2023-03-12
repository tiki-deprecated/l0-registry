/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry;

import com.mytiki.l0_registry.features.latest.id.IdAOReq;
import com.mytiki.l0_registry.features.latest.id.IdAORsp;
import com.mytiki.l0_registry.features.latest.id.IdService;
import com.mytiki.l0_registry.main.App;
import com.mytiki.l0_registry.utilities.AddressSignature;
import com.mytiki.l0_registry.utilities.B64Url;
import com.mytiki.l0_registry.utilities.SHA3Facade;
import com.mytiki.spring_rest_api.ApiException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"ci", "dev", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IdTest {

    @Autowired
    private IdService service;

    @Test
    public void Test_GetNone_Success() throws JOSEException, NoSuchAlgorithmException, CryptoException {
        RSAKey keypair = keypair();
        String address = address(keypair.toRSAPublicKey());
        String signHeader = buildSignature(keypair);

        IdAORsp rsp = service.get(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new AddressSignature(signHeader));
        assertNull(rsp);
    }

    @Test
    public void Test_RegisterNew_Success() throws JOSEException, CryptoException, NoSuchAlgorithmException {
        RSAKey keypair = keypair();
        String address = address(keypair.toRSAPublicKey());
        String signHeader = buildSignature(keypair);

        IdAOReq req = new IdAOReq(UUID.randomUUID().toString(), address);
        IdAORsp rsp = service.register(UUID.randomUUID().toString(), req, new AddressSignature(signHeader), null);

        assertNotNull(rsp.getSignKey());
        assertTrue(rsp.getAddresses().contains(address));
    }

    @Test
    public void Test_RegisterExisting_Success() throws JOSEException, CryptoException, NoSuchAlgorithmException {
        RSAKey keypair = keypair();
        String address = address(keypair.toRSAPublicKey());
        String signHeader = buildSignature(keypair);

        IdAOReq req = new IdAOReq(UUID.randomUUID().toString(), address);
        service.register(UUID.randomUUID().toString(), req, new AddressSignature(signHeader), null);
        IdAORsp rsp2 = service.register(UUID.randomUUID().toString(), req, new AddressSignature(signHeader), null);

        assertTrue(rsp2.getAddresses().contains(address));
        assertEquals(rsp2.getAddresses().size(), 1);
    }

    @Test
    public void Test_RegisterAdd_Success() throws JOSEException, CryptoException, NoSuchAlgorithmException {
        String appId = UUID.randomUUID().toString();

        RSAKey keypair = keypair();
        String address1 = address(keypair.toRSAPublicKey());
        String signHeader = buildSignature(keypair);
        IdAOReq req = new IdAOReq(UUID.randomUUID().toString(), address1);
        IdAORsp rsp1 = service.register(appId, req, new AddressSignature(signHeader), null);

        keypair = keypair();
        String address2 = address(keypair.toRSAPublicKey());
        signHeader = buildSignature(keypair);
        req.setAddress(address2);
        IdAORsp rsp2 = service.register(appId, req, new AddressSignature(signHeader), null);

        assertEquals(rsp1.getSignKey(), rsp2.getSignKey());
        assertEquals(rsp2.getAddresses().size(), 2);
        assertTrue(rsp2.getAddresses().contains(address1));
        assertTrue(rsp2.getAddresses().contains(address2));
    }

    @Test
    public void Test_Get_Success() throws JOSEException, NoSuchAlgorithmException, CryptoException {
        String appId = UUID.randomUUID().toString();
        String cid = UUID.randomUUID().toString();

        RSAKey keypair = keypair();
        String address = address(keypair.toRSAPublicKey());
        String signHeader = buildSignature(keypair);

        IdAOReq req = new IdAOReq(cid, address);
        IdAORsp registerRsp = service.register(appId, req, new AddressSignature(signHeader), null);
        IdAORsp getRsp = service.get(appId, cid, new AddressSignature(signHeader));

        assertEquals(registerRsp.getSignKey(), getRsp.getSignKey());
        assertEquals(registerRsp.getAddresses().size(), getRsp.getAddresses().size());
        assertEquals(getRsp.getAddresses().size(),1);
        assertTrue(getRsp.getAddresses().contains(address));
    }

    @Test
    public void Test_GetBadSig_Failure(){
        ApiException ex = assertThrows(ApiException.class,
                () ->  service.get(UUID.randomUUID().toString(), UUID.randomUUID().toString(), new AddressSignature(
                                UUID.randomUUID() + "." + UUID.randomUUID() + "." + UUID.randomUUID())));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getHttpStatus());
    }

    @Test
    public void Test_GetBadAddress_Failure() throws JOSEException, NoSuchAlgorithmException, CryptoException {
        String appId = UUID.randomUUID().toString();
        String cid = UUID.randomUUID().toString();

        RSAKey keypair = keypair();
        String address = address(keypair.toRSAPublicKey());
        String signHeader = buildSignature(keypair);
        IdAOReq req = new IdAOReq(cid, address);
        service.register(appId, req, new AddressSignature(signHeader), null);

        ApiException ex = assertThrows(ApiException.class,
                () -> {
                    RSAKey kp = keypair();
                    String header = buildSignature(kp);
                    service.get(appId, cid, new AddressSignature(header));
                });
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getHttpStatus());
    }

    @Test
    public void Test_Delete_Success() throws JOSEException, CryptoException, NoSuchAlgorithmException {
        String id = UUID.randomUUID().toString();
        String appId = UUID.randomUUID().toString();
        RSAKey keypair = keypair();
        String address = address(keypair.toRSAPublicKey());
        String signHeader = buildSignature(keypair);

        IdAOReq req = new IdAOReq(id, address);
        service.register(appId, req, new AddressSignature(signHeader), null);
        service.delete(appId, id);

        IdAORsp rsp = service.get(appId, id, new AddressSignature(signHeader));
        assertNull(rsp);
    }

    private RSAKey keypair() throws JOSEException {
        return new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate();
    }

    private String address(RSAPublicKey pubKey) throws NoSuchAlgorithmException {
        return B64Url.encode(SHA3Facade.sha256(pubKey.getEncoded()));
    }

    private String buildSignature(RSAKey keypair) throws JOSEException, CryptoException {
        String stringToSign = UUID.randomUUID().toString();
        byte[] bytesToSign = stringToSign.getBytes();
        RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
        signer.init(true, new RSAKeyParameters(true,
                keypair.toRSAPrivateKey().getModulus(), keypair.toRSAPrivateKey().getPrivateExponent()));
        signer.update(bytesToSign, 0, bytesToSign.length);
        String signature = Base64.getEncoder().encodeToString(signer.generateSignature());
        String pubKey = Base64.getEncoder().encodeToString(keypair.toPublicKey().getEncoded());
        return stringToSign + "." + pubKey + "." + signature;
    }
}
