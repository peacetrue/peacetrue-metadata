drop table if exists entity;
create table entity
(
    id            bigint auto_increment primary key,
    code          varchar(64)  not null,
    name          varchar(255) not null,
    many_to_many  bit          not null,
    remark        varchar(255) not null,
    serial_number bigint       not null,
    creator_id    bigint       not null,
    created_time  datetime     not null,
    modifier_id   bigint       not null,
    modified_time timestamp    not null
);

drop table if exists property;
create table property
(
    id            bigint auto_increment primary key,
    entity_id     bigint       not null,
    code          varchar(32)  not null,
    name          varchar(255) not null,
    type_id       bigint       not null,
    reference_id  bigint       not null,
    remark        varchar(255) not null,
    serial_number int          not null,
    creator_id    bigint       not null,
    created_time  datetime     not null,
    modifier_id   bigint       not null,
    modified_time timestamp    not null
);

drop table if exists dictionary_type;
create table dictionary_type
(
    id            bigint auto_increment primary key,
    code          varchar(32)  not null,
    name          varchar(32)  not null,
    remark        varchar(255) not null,
    creator_id    bigint       not null,
    created_time  datetime     not null,
    modifier_id   bigint       not null,
    modified_time datetime     not null
);

drop table if exists dictionary_value;
create table dictionary_value
(
    id                   bigint auto_increment primary key,
    dictionary_type_id   bigint       not null,
    dictionary_type_code varchar(32)  not null,
    code                 varchar(32)  not null,
    name                 varchar(255) not null,
    remark               varchar(255) not null,
    serial_number        tinyint      not null,
    creator_id           bigint       not null,
    created_time         datetime     not null,
    modifier_id          bigint       not null,
    modified_time        datetime     not null
)
