package com.mytiki.l0_registry.features.latest.usage;

import com.mytiki.l0_registry.features.latest.id.IdAORsp;
import com.mytiki.l0_registry.features.latest.id.IdController;
import com.mytiki.l0_registry.utilities.AddressSignature;
import com.mytiki.l0_registry.utilities.Constants;
import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

@Tag(name = "")
@RestController
@RequestMapping(value = UsageController.PATH_CONTROLLER)
public class UsageController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "usage";

    private final UsageService service;

    public UsageController(UsageService service) {
        this.service = service;
    }

    @Operation(operationId = Constants.PROJECT_DASH_PATH +  "-usage-get",
            summary = "Get Usage", description = "Get an account's monthly usage",
            security = @SecurityRequirement(name = "oauth", scopes = "registry:admin"))
    @Secured("SCOPE_admin")
    @RequestMapping(method = RequestMethod.GET)
    public List<UsageAO> getAddresses(
            Principal principal,
            @RequestParam(required = false) ZonedDateTime day) {
        return service.get(principal.getName(), day);
    }
}
