package com.mytiki.l0_registry.features.latest.usage;

import com.mytiki.l0_registry.features.latest.config.ConfigConfig;
import com.mytiki.l0_registry.features.latest.config.ConfigController;
import com.mytiki.l0_registry.features.latest.config.ConfigRepository;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.l0.auth.L0AuthService;
import com.mytiki.l0_registry.utilities.Constants;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(UsageConfig.PACKAGE_PATH)
@EntityScan(UsageConfig.PACKAGE_PATH)
public class UsageConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".usage";

    @Bean
    public UsageController usageController(@Autowired UsageService service){
        return new UsageController(service);
    }

    @Bean
    public UsageService usageService(
            @Autowired UsageRepository repository,
            @Autowired ConfigService configService,
            @Autowired L0AuthService l0AuthService,
            @Value("${com.mytiki.l0_registry.usage.min_users}") int minUsers,
            @Value("${com.mytiki.l0_registry.usage.stripe.key}") String stripekey){
        Stripe.apiKey = stripekey;
        return new UsageService(repository, configService, l0AuthService, minUsers);
    }
}
