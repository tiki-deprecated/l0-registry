/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.address;

import com.mytiki.l0_registry.features.latest.id.IdDO;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "address")
public class AddressDO implements Serializable {
    private Long addressId;
    private IdDO id;
    private byte[] address;
    private ZonedDateTime created;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    @ManyToOne
    @JoinColumn(name="cid_id", nullable=false)
    public IdDO getId() {
        return id;
    }

    public void setId(IdDO id) {
        this.id = id;
    }

    @Column(name = "address")
    public byte[] getAddress() {
        return address;
    }

    public void setAddress(byte[] address) {
        this.address = address;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
