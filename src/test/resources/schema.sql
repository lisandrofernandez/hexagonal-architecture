CREATE EXTENSION IF NOT EXISTS "uuid-ossp"; -- This is required to auto generate UUID values

CREATE TABLE IF NOT EXISTS hex_user_account.user_account (
    id                  uuid NOT NULL DEFAULT uuid_generate_v4(),
    username            varchar(50) NOT NULL,
    lowercased_username varchar(50) NOT NULL,
    name                varchar(300) NOT NULL,
    version             integer NOT NULL DEFAULT 0,
    --creation_datetime DATETIME DEFAULT NOW() NOT NULL,
    --modification_datetime DATETIME DEFAULT NOW() NOT NULL,
    CONSTRAINT user_account_id_pkey PRIMARY KEY (id),
    CONSTRAINT user_account_lowercased_username_uk UNIQUE (lowercased_username)
);
