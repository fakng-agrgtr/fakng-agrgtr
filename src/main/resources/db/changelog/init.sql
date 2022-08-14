CREATE SEQUENCE company_seq;
CREATE SEQUENCE location_seq;
CREATE SEQUENCE vacancy_seq;
CREATE SEQUENCE subscription_seq;
CREATE SEQUENCE company_sub_seq;

CREATE TABLE location
(
    id    INT         NOT NULL DEFAULT nextval('location_seq') PRIMARY KEY,
    title VARCHAR(32) NOT NULL
);

CREATE TABLE company
(
    id       INT         NOT NULL DEFAULT nextval('company_seq') PRIMARY KEY,
    title    VARCHAR(32) NOT NULL,
    logo_url VARCHAR(256)
);

CREATE TABLE vacancy
(
    id          BIGINT                   NOT NULL DEFAULT nextval('vacancy_seq') PRIMARY KEY,
    title       VARCHAR(128)             NOT NULL,
    description VARCHAR                  NOT NULL,
    url         VARCHAR                  NOT NULL,
    company_id  INT                      NOT NULL,
    location_id BIGINT                   NOT NULL,
    add_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),

    CONSTRAINT vacancy_company_fk FOREIGN KEY (company_id) REFERENCES company (id),
    CONSTRAINT vacancy_location_fk FOREIGN KEY (location_id) REFERENCES location (id)
);

CREATE INDEX vacancy_company_idx ON vacancy (company_id);
CREATE INDEX vacancy_location_idx ON vacancy (location_id);

CREATE TABLE subscription
(
    id        BIGINT      NOT NULL DEFAULT nextval('subscription_seq') PRIMARY KEY,
    email     VARCHAR(64) NOT NULL UNIQUE,
    activated SMALLINT    NOT NULL
);

CREATE TABLE company_sub
(
    id              BIGINT NOT NULL DEFAULT nextval('company_sub_seq') PRIMARY KEY,
    company_id      INT    NOT NULL,
    location_id     INT    NOT NULL,
    subscription_id BIGINT NOT NULL,

    CONSTRAINT company_sub_company_fk FOREIGN KEY (company_id) REFERENCES company (id),
    CONSTRAINT company_sub_location_fk FOREIGN KEY (location_id) REFERENCES location (id),
    CONSTRAINT company_sub_subscription_fk FOREIGN KEY (subscription_id) REFERENCES subscription (id)
);

CREATE INDEX company_sub_company_idx ON company_sub (company_id);
CREATE INDEX company_sub_location_idx ON company_sub (location_id);