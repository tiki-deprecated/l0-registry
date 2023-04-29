package com.mytiki.l0_registry.l0.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

public class L0AuthConfig {

    @Bean
    public L0AuthService l0AuthService(
            @Value("${com.mytiki.l0_registry.l0_auth.uri}") String uri,
            @Value("${com.mytiki.l0_registry.l0_auth.key.id}") String id,
            @Value("${com.mytiki.l0_registry.l0_auth.key.secret}") String secret,
            @Autowired RestTemplateBuilder builder){
        return new L0AuthService(builder.rootUri(uri).build(),id,secret);
    }
}
