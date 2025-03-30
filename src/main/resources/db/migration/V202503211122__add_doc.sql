-- auto-generated definition
CREATE TABLE document
(
    id         UUID PRIMARY KEY default gen_random_uuid(),
    start_date  date
);

INSERT INTO document (id, start_date)
VALUES ('d9090160-8777-4b0f-8cc7-40390ec3d4ef', '1001-01-01');