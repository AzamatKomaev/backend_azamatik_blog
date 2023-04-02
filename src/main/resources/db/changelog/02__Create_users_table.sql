-- liquibase formatted sql

-- changeset azamat_komaev:02
create table users
(
    id         serial       not null,
    username   varchar(255) not null unique,
    password   varchar(255) not null,
    is_active  boolean      not null default true,
    created_at timestamp    not null default now(),
    primary key (id)

);
-- rollback drop table users;