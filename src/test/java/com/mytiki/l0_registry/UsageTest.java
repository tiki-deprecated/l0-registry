package com.mytiki.l0_registry;

import com.mytiki.l0_registry.features.latest.config.ConfigDO;
import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.features.latest.id.IdDO;
import com.mytiki.l0_registry.features.latest.sign.SignRepository;
import com.mytiki.l0_registry.features.latest.usage.UsageAO;
import com.mytiki.l0_registry.features.latest.usage.UsageDO;
import com.mytiki.l0_registry.features.latest.usage.UsageRepository;
import com.mytiki.l0_registry.features.latest.usage.UsageService;
import com.mytiki.l0_registry.l0.auth.L0AuthService;
import com.mytiki.l0_registry.main.App;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.UsageRecord;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.UsageRecordCreateOnSubscriptionItemParams;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"ci", "dev", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsageTest {

    @Autowired
    private UsageService service;

    @Autowired
    private UsageRepository repository;

    @Test
    public void Test_IncrementNone_Success(){
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime start = now.truncatedTo(ChronoUnit.DAYS);

        String appId = UUID.randomUUID().toString();
        service.increment(appId);

        List<UsageDO> usage =
                repository.getAllByConfigAppIdAndCreatedBetween(appId, start, start.plusDays(1));

        assertEquals(1, usage.size());
        assertEquals(appId, usage.get(0).getConfig().getAppId());
        assertEquals(1, usage.get(0).getTotal());
        assertNotNull(usage.get(0).getUsageId());
        assertNotNull(usage.get(0).getModified());
        assertNotNull(usage.get(0).getCreated());
    }

    @Test
    public void Test_IncrementExists_Success(){
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime start = now.truncatedTo(ChronoUnit.DAYS);

        String appId = UUID.randomUUID().toString();
        int iterations = 10;
        for(int i=0; i<iterations; i++) {
            service.increment(appId);
        }

        List<UsageDO> usage =
                repository.getAllByConfigAppIdAndCreatedBetween(appId, start, start.plusDays(1));

        assertEquals(1, usage.size());
        assertEquals(appId, usage.get(0).getConfig().getAppId());
        assertEquals(iterations, usage.get(0).getTotal());
        assertNotNull(usage.get(0).getUsageId());
        assertNotNull(usage.get(0).getModified());
        assertNotNull(usage.get(0).getCreated());
    }
}
