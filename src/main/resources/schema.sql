CREATE TABLE user_account (
    id                  UUID DEFAULT random_uuid() PRIMARY KEY,
    username            VARCHAR(50) NOT NULL,
    lowercased_username VARCHAR(50) NOT NULL,
    name                VARCHAR(300) NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    --creation_datetime DATETIME DEFAULT NOW() NOT NULL,
    --modification_datetime DATETIME DEFAULT NOW() NOT NULL,
    --UNIQUE KEY user_account_username_uk (username),
    UNIQUE KEY user_account_lowercased_username_uk (lowercased_username)
);
