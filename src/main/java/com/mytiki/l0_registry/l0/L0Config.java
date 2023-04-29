package com.mytiki.l0_registry.l0;

import com.mytiki.l0_registry.l0.auth.L0AuthConfig;
import org.springframework.context.annotation.Import;

@Import({
        L0AuthConfig.class
})
public class L0Config {}
