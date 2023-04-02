-- liquibase formatted sql

-- changeset azamat_komaev:01
create table posts
(
    id         serial primary key not null,
    title      varchar(255)       not null,
    content    text               not null,
    created_at timestamp          not null default now()
);
-- rollback drop table posts;