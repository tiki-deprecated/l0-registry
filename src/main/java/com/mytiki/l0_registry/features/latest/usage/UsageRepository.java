package com.mytiki.l0_registry.features.latest.usage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface UsageRepository extends JpaRepository<UsageDO, Long> {
    List<UsageDO> getAllByConfigAppIdAndCreatedBetween(String appId, ZonedDateTime start, ZonedDateTime end);

    @Query("SELECT SUM(u.total) " +
            "FROM UsageDO u " +
            "WHERE u.config.appId = :appId " +
            "AND u.created >= :start AND u.created < :end")
    Long getTotalByConfigAppIdAndCreatedBetween(String appId, ZonedDateTime start, ZonedDateTime end);
}
