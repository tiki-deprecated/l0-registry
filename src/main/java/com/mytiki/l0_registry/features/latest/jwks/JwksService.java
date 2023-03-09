/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.jwks;

import com.mytiki.l0_registry.features.latest.config.ConfigDO;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JwksService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final int cacheSeconds;
    private final JwksRepository repository;
    private final RestTemplate client;

    public JwksService(JwksRepository repository, RestTemplate client, int cacheSeconds) {
        this.repository = repository;
        this.client = client;
        this.cacheSeconds = cacheSeconds;
    }

    public void guard(String id, String token, ConfigDO config){
        if(config.getJwksEndpoint() == null) return;
        try {
            JWKSet jwks = get(config.getJwksEndpoint());
            if (jwks != null) {
                JwtDecoder decoder = buildDecoder(jwks);
                Jwt jwt = decoder.decode(token);
                if(config.getVerifySubject() && !jwt.getSubject().equals(id)){
                    throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                            .message("Invalid sub claim")
                            .build();
                }
            }
        } catch (URISyntaxException | ParseException e) {
            logger.error("Failed to fetch endpoint, skipping.", e);
        } catch (JwtException e){
            throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                    .message("Invalid token")
                    .build();
        }
    }

    private JWKSet get(String endpoint) throws URISyntaxException, ParseException {
        Optional<JwksDO> saved = repository.getByEndpoint(endpoint);
        if(saved.isPresent() && saved.get().getEndpoint() != null && !saved.get().getEndpoint().isEmpty() &&
                (ZonedDateTime.now().toEpochSecond() - saved.get().getModified().toEpochSecond()) < cacheSeconds){
            return JWKSet.parse(saved.get().getKeySet());
        }else {
            JWKSet keySet = fetch(new URI(endpoint));
            if(keySet != null) {
                JwksDO toSave = saved.orElse(null);
                ZonedDateTime now = ZonedDateTime.now();
                if(saved.isEmpty()){
                    toSave = new JwksDO();
                    toSave.setEndpoint(endpoint);
                    toSave.setCreated(now);
                }
                toSave.setKeySet(keySet.toString());
                toSave.setModified(now);
                repository.save(toSave);
            }
            return keySet;
        }
    }

    private JWKSet fetch(URI endpoint){
        try {
            ResponseEntity<String> response = client.getForEntity(endpoint, String.class);
            if(response.getStatusCode().is2xxSuccessful()){
                JWKSet jwkSet = JWKSet.parse(response.getBody());
                return new JWKSet(jwkSet.getKeys()
                        .stream()
                        .filter(jwk -> jwk.getKeyUse().equals(KeyUse.SIGNATURE))
                        .filter(jwk -> jwk.getKeyType().equals(KeyType.EC) || jwk.getKeyType().equals(KeyType.RSA))
                        .toList());
            }
        } catch (Exception e) {
            logger.error("Failed to fetch endpoint, skipping.", e);
        }
        return null;
    }

    private JwtDecoder buildDecoder(JWKSet jwkSet){
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(
                jwkSet.getKeys()
                        .stream()
                        .map(jwt -> JWSAlgorithm.parse(jwt.getAlgorithm().getName()))
                        .collect(Collectors.toSet()),
                new ImmutableJWKSet<>(jwkSet)));
        NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));
        return decoder;
    }
}
