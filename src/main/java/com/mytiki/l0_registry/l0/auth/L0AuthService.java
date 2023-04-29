package com.mytiki.l0_registry.l0.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mytiki.spring_rest_api.ApiError;
import com.mytiki.spring_rest_api.ApiException;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L0AuthService {
    private final RestTemplate client;
    private final String clientId;
    private final String clientSecret;

    public L0AuthService(RestTemplate client, String clientId, String clientSecret) {
        this.client = client;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public L0AuthAOToken getToken(List<String> scopes){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body= new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", String.join(" ", scopes));

        try{
            return client.postForObject(
                    "/api/latest/oauth/token",
                    new HttpEntity<MultiValueMap<String, String>>(body, headers),
                    L0AuthAOToken.class);
        }catch (HttpClientErrorException ex){
            throw resolveOauthError(ex);
        }
    }

    public L0AuthAOOrg getOrg(String orgId, String token){
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(token);
            ResponseEntity<L0AuthAOOrg> response = client.exchange(
                    "/api/latest/org/{orgId}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    L0AuthAOOrg.class,
                    orgId);
            return response.getBody();
        }catch (HttpStatusCodeException ex){
            throw resolveHttpError(ex);
        }
    }

    public L0AuthAOUser getUser(String userId, String token){
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(token);
            ResponseEntity<L0AuthAOUser> response = client.exchange(
                    "/api/latest/user/{userId}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    L0AuthAOUser.class,
                    userId);
            return response.getBody();
        }catch (HttpStatusCodeException ex){
            throw resolveHttpError(ex);
        }
    }

    public L0AuthAOApp getApp(String appId, String token){
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(token);
            ResponseEntity<L0AuthAOApp> response = client.exchange(
                    "/api/latest/app/{appId}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    L0AuthAOApp.class,
                    appId);
            return response.getBody();
        }catch (HttpStatusCodeException ex){
            throw resolveHttpError(ex);
        }
    }

    private ApiException resolveOauthError(HttpClientErrorException ex){
        ParameterizedTypeReference<Map<String, String>> type = new ParameterizedTypeReference<Map<String, String>>() {};
        Map<String, String> error = ex.getResponseBodyAs(type);
        if(error == null) error = new HashMap<>();
        return new ApiExceptionBuilder(HttpStatus.resolve(ex.getStatusCode().value()))
                .id(error.get("error"))
                .message(error.get("error_description"))
                .help(error.get("error_uri"))
                .cause(ex.getCause())
                .build();
    }

    private ApiException resolveHttpError(HttpStatusCodeException ex){
        ApiError error = ex.getResponseBodyAs(ApiError.class);
        if(error == null) error = new ApiError();
        return new ApiExceptionBuilder(HttpStatus.resolve(ex.getStatusCode().value()))
                .id(error.getId())
                .message(error.getMessage())
                .detail(error.getDetail())
                .help(error.getHelp())
                .properties(error.getProperties())
                .cause(ex.getCause())
                .build();
    }
}
