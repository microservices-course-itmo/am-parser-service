BEGIN;
create table regions
(
    id        bigint generated by default as identity
        constraint regions_pkey
            primary key,
    import_id varchar(255),
    name      varchar(255),
    actual bool,
    date_rec timestamp
);
create table producers
(
    id        bigint generated by default as identity
        constraint producers_pkey
            primary key,
    import_id varchar(255),
    name      varchar(255),
    actual bool,
    date_rec timestamp
);
alter table if exists wines add column if not exists description text;
alter table if exists wines add column if not exists flavor text;
alter table if exists wines add column if not exists taste text;
alter table if exists wines add column if not exists gastronomy text;
alter table if exists wines add column if not exists old_price double precision;
alter table if exists wines add column if not exists rating double precision;
alter table if exists wines add column if not exists link varchar(255);
alter table if exists wines add column if not exists region_id    bigint
    constraint fkrp9327qxsf5d05l54shhsxuyx
        references regions;
alter table if exists wines add column if not exists producer_id    bigint
    constraint fkl4e0t37chcocch970i2f0jb6
        references producers;
END;