package com.mytiki.l0_registry;

import com.mytiki.l0_registry.features.latest.config.ConfigService;
import com.mytiki.l0_registry.features.latest.usage.UsageDO;
import com.mytiki.l0_registry.features.latest.usage.UsageRepository;
import com.mytiki.l0_registry.features.latest.usage.UsageService;
import com.mytiki.l0_registry.main.App;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Autowired
    private ConfigService configService;

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

    @Test
    public void Test_IncrementMultipleDays_Success(){
        ZonedDateTime now = ZonedDateTime.now();

        String appId = UUID.randomUUID().toString();
        UsageDO usage = new UsageDO();

        usage.setConfig(configService.getCreate(appId));
        usage.setTotal(1L);
        usage.setCreated(now.minusDays(3));
        usage.setModified(now.minusDays(3));
        repository.save(usage);

        service.increment(appId);

        ZonedDateTime start = now.truncatedTo(ChronoUnit.DAYS);
        List<UsageDO> today =
                repository.getAllByConfigAppIdAndCreatedBetween(appId, start, start.plusDays(1));

        assertEquals(1, today.size());
        assertEquals(appId, today.get(0).getConfig().getAppId());
        assertEquals(2, today.get(0).getTotal());
        assertNotNull(today.get(0).getUsageId());
        assertNotNull(today.get(0).getModified());
        assertNotNull(today.get(0).getCreated());
    }

    @Test
    public void Test_DecrementMultipleDays_Success(){
        ZonedDateTime now = ZonedDateTime.now();

        String appId = UUID.randomUUID().toString();
        UsageDO usage = new UsageDO();

        usage.setConfig(configService.getCreate(appId));
        usage.setTotal(1L);
        usage.setCreated(now.minusDays(3));
        usage.setModified(now.minusDays(3));
        repository.save(usage);

        service.decrement(appId);

        ZonedDateTime start = now.truncatedTo(ChronoUnit.DAYS);
        List<UsageDO> today =
                repository.getAllByConfigAppIdAndCreatedBetween(appId, start, start.plusDays(1));

        assertEquals(1, today.size());
        assertEquals(appId, today.get(0).getConfig().getAppId());
        assertEquals(0, today.get(0).getTotal());
        assertNotNull(today.get(0).getUsageId());
        assertNotNull(today.get(0).getModified());
        assertNotNull(today.get(0).getCreated());
    }

    @Test
    public void Test_DecrementExists_Success(){
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime start = now.truncatedTo(ChronoUnit.DAYS);

        String appId = UUID.randomUUID().toString();
        int iterations = 10;
        for(int i=0; i<iterations; i++) {
            service.increment(appId);
        }
        service.decrement(appId);

        List<UsageDO> usage =
                repository.getAllByConfigAppIdAndCreatedBetween(appId, start, start.plusDays(1));

        assertEquals(1, usage.size());
        assertEquals(appId, usage.get(0).getConfig().getAppId());
        assertEquals(iterations-1, usage.get(0).getTotal());
        assertNotNull(usage.get(0).getUsageId());
        assertNotNull(usage.get(0).getModified());
        assertNotNull(usage.get(0).getCreated());
    }
}
