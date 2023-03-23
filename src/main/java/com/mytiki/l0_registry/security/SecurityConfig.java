/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytiki.l0_registry.features.latest.address.AddressController;
import com.mytiki.l0_registry.features.latest.config.ConfigController;
import com.mytiki.l0_registry.features.latest.id.IdController;
import com.mytiki.l0_registry.utilities.Constants;
import com.mytiki.spring_rest_api.ApiConstants;
import com.mytiki.spring_rest_api.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

@EnableWebSecurity
public class SecurityConfig {
    private static final String ADMIN_SCOPE = "SCOPE_admin";
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtDecoder jwtDecoder;
    private final String l0IndexId;
    private final String l0IndexSecret;
    private final String l0IndexRole;

    public SecurityConfig(
            @Autowired ObjectMapper objectMapper,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") URL jwtJwkUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.audiences}") Set<String> jwtAudiences,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String jwtIssuer,
            @Value("${com.mytiki.l0_registry.l0_index.id}") String l0IndexId,
            @Value("${com.mytiki.l0_registry.l0_index.secret}") String l0IndexSecret,
            @Value("${com.mytiki.l0_registry.l0_index.role}") String l0IndexRole) {
        this.accessDeniedHandler = new AccessDeniedHandler(objectMapper);
        this.authenticationEntryPoint = new AuthenticationEntryPoint(objectMapper);
        this.jwtDecoder = jwtDecoder(jwtJwkUri, jwtAudiences, jwtIssuer);
        this.l0IndexSecret = l0IndexSecret;
        this.l0IndexId = l0IndexId;
        this.l0IndexRole = l0IndexRole;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilter(new WebAsyncManagerIntegrationFilter())
                .servletApi().and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .securityContext().and()
                .headers()
                .cacheControl().and()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity().and()
                .frameOptions().and()
                .xssProtection().and()
                .referrerPolicy().and()
                .permissionsPolicy().policy(SecurityConstants.FEATURE_POLICY).and()
                .contentSecurityPolicy(SecurityConstants.CONTENT_SECURITY_POLICY).and().and()
                .anonymous().and()
                .cors()
                .configurationSource(SecurityConstants.corsConfigurationSource()).and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher(ConfigController.PATH_CONTROLLER, HttpMethod.POST.name())
                ).and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, ApiConstants.HEALTH_ROUTE, Constants.API_DOCS_PATH ).permitAll()
                .requestMatchers(HttpMethod.GET, AddressController.PATH_CONTROLLER).hasRole(l0IndexRole)
                .requestMatchers(ConfigController.PATH_CONTROLLER + "/**").hasAuthority(ADMIN_SCOPE)
                .requestMatchers(HttpMethod.DELETE, IdController.PATH_CONTROLLER + "/**").hasAuthority(ADMIN_SCOPE)
                .anyRequest().authenticated().and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint).and()
                .oauth2ResourceServer()
                .jwt().decoder(jwtDecoder).and()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username(l0IndexId)
                .password(l0IndexSecret)
                .roles(l0IndexRole)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    private JwtDecoder jwtDecoder(
            URL jwtJwkUri,
            Set<String> jwtAudiences,
            String jwtIssuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(jwtJwkUri.toString())
                .jwsAlgorithm(SignatureAlgorithm.ES256)
                .build();
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(new JwtIssuerValidator(jwtIssuer));
        validators.add(new JwtClaimValidator<>(JwtClaimNames.SUB, Objects::nonNull));
        validators.add(new JwtClaimValidator<>(JwtClaimNames.IAT, Objects::nonNull));
        Predicate<List<String>> audienceTest = (audience) -> (audience != null)
                && new HashSet<>(audience).containsAll(jwtAudiences);
        validators.add(new JwtClaimValidator<>(JwtClaimNames.AUD, audienceTest));
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));
        return decoder;
    }
}
