/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_registry.features.latest.sign;

import com.mytiki.l0_registry.features.latest.id.IdDO;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "sign_key")
public class SignDO implements Serializable {
    private Long keyId;
    private IdDO id;
    private byte[] privateKey;
    private ZonedDateTime created;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    public Long getKeyId() {
        return keyId;
    }

    public void setKeyId(Long keyId) {
        this.keyId = keyId;
    }

    @ManyToOne
    @JoinColumn(name="cid_id", nullable=false)
    public IdDO getId() {
        return id;
    }

    public void setId(IdDO id) {
        this.id = id;
    }

    @Column(name = "private_key")
    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
