/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.id;

import com.mytiki.l0_registry.features.latest.address.AddressDO;
import com.mytiki.l0_registry.features.latest.config.ConfigDO;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "cid")
public class IdDO implements Serializable {
    private Long cid;
    private String customerId;
    private ConfigDO config;
    private List<AddressDO> addresses;
    private ZonedDateTime created;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid_id")
    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }
    @Column(name = "customer_id")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @ManyToOne
    @JoinColumn(name="config_id", nullable=false)
    public ConfigDO getConfig() {
        return config;
    }

    public void setConfig(ConfigDO config) {
        this.config = config;
    }

    @OneToMany(mappedBy="id")
    public List<AddressDO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDO> addresses) {
        this.addresses = addresses;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
