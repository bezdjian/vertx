CREATE TABLE assets (
  "id" serial primary key,
  "name" VARCHAR(45) NULL
);

CREATE TABLE quotes (
    id         SERIAL PRIMARY KEY,
    bid        NUMERIC,
    ask        NUMERIC,
    last_price NUMERIC,
    volume     NUMERIC,
    asset      INT,
    FOREIGN KEY (asset) REFERENCES assets (id),
    CONSTRAINT last_price_is_positive CHECK (last_price > 0),
    CONSTRAINT volume_is_positive_or_zero CHECK (volume >= 0)
);
