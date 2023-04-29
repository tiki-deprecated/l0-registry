package com.mytiki.l0_registry.features.latest.usage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface UsageRepository extends JpaRepository<UsageDO, Long> {
    List<UsageDO> getAllByConfigAppIdAndCreatedBetween(String appId, ZonedDateTime start, ZonedDateTime end);
}
