/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.utilities;

import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import org.springframework.http.HttpStatus;

public class AddressSignature {
    public static final String HEADER = "X-Address-Signature";

    private final String stringToSign;
    private final String pubKey;
    private final String signature;

    public AddressSignature(String header) {
        String[] split = header.split("\\.");
        if(split.length != 3){
            throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                    .message("Invalid " + HEADER)
                    .help("Check format (stringToSign.pubKey.signature)")
                    .build();
        }
        stringToSign = split[0];
        pubKey = split[1];
        signature = split[2];
    }

    public String getStringToSign() {
        return stringToSign;
    }

    public String getPubKey() {
        return pubKey;
    }

    public String getSignature() {
        return signature;
    }
}
