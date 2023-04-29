package com.mytiki.l0_registry.features.latest.usage;

import com.mytiki.l0_registry.features.latest.config.ConfigDO;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "usage")
public class UsageDO implements Serializable {
    private Long usageId;
    private ConfigDO config;
    private Long total;
    private ZonedDateTime created;
    private ZonedDateTime modified;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    public Long getUsageId() {
        return usageId;
    }

    public void setUsageId(Long usageId) {
        this.usageId = usageId;
    }

    @ManyToOne
    @JoinColumn(name="config_id", nullable=false)
    public ConfigDO getConfig() {
        return config;
    }

    public void setConfig(ConfigDO configDO) {
        this.config = configDO;
    }

    @Column(name = "total")
    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    @Column(name = "modified_utc")
    public ZonedDateTime getModified() {
        return modified;
    }

    public void setModified(ZonedDateTime modified) {
        this.modified = modified;
    }
}
