/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.config;

import com.mytiki.l0_registry.security.SecurityConfig;
import com.mytiki.l0_registry.utilities.Constants;
import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Tag(name = "")
@Secured("SCOPE_admin")
@RestController
@RequestMapping(value = ConfigController.PATH_CONTROLLER)
public class ConfigController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "config";

    private final ConfigService service;

    public ConfigController(ConfigService service) {
        this.service = service;
    }

    @Operation(operationId = Constants.PROJECT_DASH_PATH +  "-config-get",
            summary = "Get Config", description = "Get a configuration",
            security = @SecurityRequirement(name = "oauth", scopes = "registry:admin"))
    @RequestMapping(method = RequestMethod.GET, path = "/{app-id}")
    public ConfigAORsp getConfig(
            @PathVariable(name = "app-id") String appId) {
        return service.get(appId);
    }

    @Operation(operationId = Constants.PROJECT_DASH_PATH +  "-config-post",
            summary = "Modify Config", description = "Modify a configuration",
            security = @SecurityRequirement(name = "oauth", scopes = { "registry:admin", "registry" }))
    @RequestMapping(method = RequestMethod.POST)
    public ConfigAORsp postConfig(@RequestBody ConfigAOReq body) {
        return service.modify(body);
    }
}
