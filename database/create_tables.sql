
-- -----------------------------------------------------------------------
-- CONFIG
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS config(
    config_id BIGSERIAL PRIMARY KEY,
    app_id TEXT NOT NULL UNIQUE,
    jwks_endpoint TEXT,
    verify_subject BOOLEAN DEFAULT FALSE,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    modified_utc TIMESTAMP WITH TIME ZONE NOT NULL
);

-- -----------------------------------------------------------------------
-- CID (CUSTOMER ID)
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cid(
     cid_id BIGSERIAL PRIMARY KEY,
     customer_id TEXT NOT NULL,
     config_id BIGSERIAL NOT NULL,
     created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
     FOREIGN KEY(config_id) REFERENCES config(config_id),
     UNIQUE (customer_id, config_id)
);

-- -----------------------------------------------------------------------
-- SIGN KEY
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sign_key(
     key_id BIGSERIAL PRIMARY KEY,
     cid_id BIGSERIAL NOT NULL,
     private_key BYTEA NOT NULL UNIQUE,
     created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
     FOREIGN KEY(cid_id) REFERENCES cid(cid_id)
);

-- -----------------------------------------------------------------------
-- ADDRESS
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS address(
    address_id BIGSERIAL PRIMARY KEY,
    cid_id BIGSERIAL NOT NULL,
    address bytea NOT NULL,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY(cid_id) REFERENCES cid(cid_id),
    UNIQUE(cid_id, address)
);

-- -----------------------------------------------------------------------
-- JWKS
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jwks(
    jwks_id BIGSERIAL PRIMARY KEY,
    endpoint TEXT NOT NULL UNIQUE,
    key_set TEXT NOT NULL,
    modified_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL
);
