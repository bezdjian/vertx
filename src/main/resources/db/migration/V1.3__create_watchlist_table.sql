CREATE TABLE watchlist
(
    account_id VARCHAR,
    asset      INTEGER,
    FOREIGN KEY (asset) REFERENCES broker.assets (id),
    PRIMARY KEY (account_id, asset)
);
