create table brands
(
    id        bigint generated by default as identity
        constraint brands_pkey
            primary key,
    import_id varchar(255),
    name      varchar(255)
);

alter table brands
    owner to postgres;

create table sugar
(
    id        bigint generated by default as identity
        constraint sugar_pkey
            primary key,
    import_id varchar(255),
    name      varchar(255)
);

alter table sugar
    owner to postgres;

create table countries
(
    id        bigint generated by default as identity
        constraint countries_pkey
            primary key,
    import_id varchar(255),
    name      varchar(255)
);

alter table countries
    owner to postgres;

create table colors
(
    id        bigint generated by default as identity
        constraint colors_pkey
            primary key,
    import_id varchar(255),
    name      varchar(255)
);

alter table colors
    owner to postgres;

create table grapes
(
    id        bigint generated by default as identity
        constraint grapes_pkey
            primary key,
    import_id varchar(255),
    name      varchar(255)
);

alter table grapes
    owner to postgres;

create table wines
(
    id        bigint generated by default as identity
        constraint wines_pkey
            primary key,
    import_id   varchar(255),
    name        varchar(255),
    picture_url varchar(255),
    price       double precision not null,
    strength    double precision not null,
    volume      double precision not null,
    brand_id    bigint
        constraint fkj979yr7st9lhocxe6yrcf7h8u
            references brands,
    color_id    bigint
        constraint fkasgc6ighwj3n42r823i40mufd
            references colors,
    country_id  bigint
        constraint fkhrh4l9v9kogq84fntot76fqv7
            references countries,
    sugar_id    bigint
        constraint fkdp1347aayi9qotgpop77gl7bi
            references sugar
);

alter table wines
    owner to postgres;

create table wines_grapes 
(
    wine_id   bigint not null
        constraint fkn3sgrx19c8k0onhtdwft0nqy9
            references wines,
    grapes_id bigint not null
        constraint fkey4h64h0nxacix287cn71yorn
            references grapes
);

alter table wines_grapes
    owner to postgres;
