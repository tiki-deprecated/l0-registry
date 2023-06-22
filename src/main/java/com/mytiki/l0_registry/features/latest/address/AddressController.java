/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.address;

import com.mytiki.l0_registry.features.latest.id.IdAOReq;
import com.mytiki.l0_registry.features.latest.id.IdAORsp;
import com.mytiki.l0_registry.features.latest.id.IdService;
import com.mytiki.l0_registry.utilities.AddressSignature;
import com.mytiki.l0_registry.utilities.Constants;
import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = AddressController.PATH_CONTROLLER)
public class AddressController {

    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "address";

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @Secured({"ROLE_L0_INDEX", "SCOPE_internal:read"})
    @Operation(hidden = true)
    @RequestMapping(method = RequestMethod.GET, path = "/{address}")
    public AddressRsp getId(
            @PathVariable String address,
            @RequestParam(name = "app-id") String appId) {
        return service.getId(address, appId);
    }
}
