/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.id;

import com.mytiki.l0_registry.features.latest.address.AddressService;
import com.mytiki.l0_registry.features.latest.config.ConfigDO;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.features.latest.jwks.JwksService;
import com.mytiki.l0_registry.features.latest.sign.SignService;
import com.mytiki.l0_registry.utilities.AddressSignature;
import com.mytiki.l0_registry.utilities.B64Url;
import com.mytiki.l0_registry.utilities.RSAFacade;
import com.mytiki.l0_registry.utilities.SHA3Facade;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import jakarta.transaction.Transactional;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IdService {
    private final IdRepository repository;
    private final ConfigService configService;
    private final SignService signService;
    private final AddressService addressService;
    private final JwksService jwksService;

    public IdService(
            IdRepository repository,
            ConfigService configService,
            SignService signService,
            AddressService addressService,
            JwksService jwksService) {
        this.repository = repository;
        this.configService = configService;
        this.signService = signService;
        this.addressService = addressService;
        this.jwksService = jwksService;
    }

    @Transactional
    public IdAORsp get(String appId, String id, AddressSignature addressSignature){
        try {
            guardForSignature(addressSignature);
            String address = B64Url.encode(SHA3Facade.sha256(
                    Base64.getDecoder().decode(addressSignature.getPubKey())));
            Optional<IdDO> found = repository.getByCustomerIdAndConfigAppId(id, appId);
            if (found.isPresent()) {
                Set<String> addressList = found.get()
                        .getAddresses()
                        .stream()
                        .map(a -> B64Url.encode(a.getAddress()))
                        .collect(Collectors.toSet());
                if (!addressList.contains(address))
                    throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                            .message("Address validation failed")
                            .detail("Address is not a member")
                            .help("Try adding the address to the id first")
                            .build();
                IdAORsp rsp = new IdAORsp();
                rsp.setAddresses(addressList);
                rsp.setSignKey(signService.get(found.get()));
                return rsp;
            }
            return null;
        }catch (NoSuchAlgorithmException e){
            throw new ApiExceptionBuilder(HttpStatus.UNPROCESSABLE_ENTITY)
                    .message("Address validation failed")
                    .detail(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public IdAORsp register(String appId, IdAOReq req, AddressSignature addressSignature, String customerToken){
        IdAORsp rsp = new IdAORsp();
        guardForSignature(addressSignature);
        guardForAddress(req.getAddress(), addressSignature.getPubKey());
        Optional<IdDO> found = repository.getByCustomerIdAndConfigAppId(req.getId(), appId);
        if(found.isEmpty()){
            ConfigDO config = configService.getCreate(appId);
            jwksService.guard(req.getId(), customerToken, config);
            IdDO save = new IdDO();
            save.setConfig(config);
            save.setCustomerId(req.getId());
            save.setCreated(ZonedDateTime.now());
            save = repository.save(save);
            rsp.setSignKey(signService.cycle(save));
            addressService.save(save, req.getAddress());
            rsp.setAddresses(Set.of(req.getAddress()));
        }else{
            jwksService.guard(req.getId(), customerToken, found.get().getConfig());
            addressService.save(found.get(), req.getAddress());
            rsp.setSignKey(signService.get(found.get()));
            Set<String> addresses = found.get()
                    .getAddresses()
                    .stream()
                    .map(a -> B64Url.encode(a.getAddress()))
                    .collect(Collectors.toSet());
            addresses.add(req.getAddress());
            rsp.setAddresses(addresses);
        }
        return rsp;
    }

    @Transactional
    public void delete(String appId, String id){
        Optional<IdDO> found = repository.getByCustomerIdAndConfigAppId(id, appId);
        if(found.isPresent()){
            addressService.deleteById(found.get());
            signService.deleteAllById(found.get());
            repository.deleteByCid(found.get().getCid());
        }
    }

    public IdAORspKey pubKey(String appId, String id){
        Optional<IdDO> found = repository.getByCustomerIdAndConfigAppId(id, appId);
        if(found.isPresent()){
            IdAORspKey rsp = new IdAORspKey();
            rsp.setKey(signService.getPublicKey(found.get()));
            return rsp;
        }else
            return null;
    }

    private void guardForSignature(AddressSignature signature){
        try{
            RSAPublicKey publicKey = RSAFacade.decodePublicKey(Base64.getDecoder().decode(signature.getPubKey()));
            boolean isValid = RSAFacade.verify(publicKey, signature.getStringToSign(), signature.getSignature());
            if(!isValid)
                throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                        .message("Failed to validate key/signature paid")
                        .detail("Signature does not match plaintext")
                        .properties(
                                "stringToSign", signature.getStringToSign(),
                                "signature", signature.getSignature())
                        .build();
        } catch (IOException | IllegalArgumentException e) {
            throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                    .message("Failed to validate key/signature paid")
                    .detail("Encoding is incorrect")
                    .cause(e.getCause())
                    .build();
        }
    }

    private void guardForAddress(String address, String pubKey) {
        try {
            byte[] addressBytes = B64Url.decode(address);
            byte[] hashedKey = SHA3Facade.sha256(Base64.getDecoder().decode(pubKey));
            if(!Arrays.equals(addressBytes, hashedKey)){
                throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                        .message("Address validation failed")
                        .detail("Public key does not match the address provided")
                        .build();
            }
        }catch (NoSuchAlgorithmException e){
            throw new ApiExceptionBuilder(HttpStatus.UNPROCESSABLE_ENTITY)
                    .message("Address validation failed")
                    .detail(e.getMessage())
                    .build();
        }
    }
}
