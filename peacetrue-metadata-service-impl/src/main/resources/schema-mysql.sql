drop table if exists entity;
create table entity
(
    id            bigint auto_increment primary key comment '主键',
    code          varchar(255) not null comment '编码',
    name          varchar(255) not null comment '名称',
    many_to_many  bit          not null comment '多对多关联',
    remark        varchar(255) not null comment '备注',
    serial_number bigint       not null comment '序号',
    creator_id    bigint       not null comment '创建者主键',
    created_time  datetime     not null comment '创建时间',
    modifier_id   bigint       not null comment '修改者主键',
    modified_time timestamp    not null comment '最近修改时间',
    constraint entity_code_uindex unique (code)
) comment '实体';

drop table if exists property;
create table property
(
    id            bigint auto_increment primary key comment '主键',
    entity_id     bigint       not null comment '实体. 属性所属实体',
#     entity_code         varchar(64)  not null comment '实体编码',
    code          varchar(32)  not null comment '编码',
    name          varchar(255) not null comment '名称',
    type_id       bigint       not null comment '类型. 关联字典主键',
    reference_id  bigint       not null comment '关联实体. 关联的实体，若无设置为 0',
    remark        varchar(255) not null comment '备注',
    serial_number int          not null comment '序号',
    creator_id    bigint       not null comment '创建者主键',
    created_time  datetime     not null comment '创建时间',
    modifier_id   bigint       not null comment '修改者主键',
    modified_time timestamp    not null comment '最近修改时间',
    constraint property_entity_id_code_uindex unique (entity_id, code)
) comment '属性';

drop table if exists record;
create table record
(
    id            bigint auto_increment primary key comment '主键',
    entity_id     bigint       not null comment '实体. 属性所属实体',
    remark        varchar(255) not null comment '备注',
    creator_id    bigint       not null comment '创建者主键',
    created_time  datetime     not null comment '创建时间',
    modifier_id   bigint       not null comment '修改者主键',
    modified_time timestamp    not null comment '最近修改时间'
) comment '记录';

drop table if exists property_value;
create table property_value
(
    id            bigint auto_increment primary key comment '主键',
    record_id     bigint        not null comment '记录',
    property_id   bigint        not null comment '属性',
#     property_code varchar(32)   not null comment '属性编码. 冗余字段',
    entity_id     bigint        not null comment '实体. 冗余字段',
    value         varchar(2048) not null comment '属性值',
    remark        varchar(255)  not null comment '备注',
    creator_id    bigint        not null comment '创建者主键',
    created_time  datetime      not null comment '创建时间',
    modifier_id   bigint        not null comment '修改者主键',
    modified_time timestamp     not null comment '最近修改时间'
) comment '属性值';

drop table if exists `constraint`;
create table `constraint`
(
    id            bigint auto_increment primary key comment '主键',
    type_id       bigint       not null comment '类型. 不同属性类型使用的约束不同',
    code          varchar(32)  not null comment '编码',
    name          varchar(255) not null comment '名称',
    message       varchar(255) not null comment '消息',
    options_id    bigint       not null comment '选项. 关联实体类',
    remark        varchar(255) not null comment '备注',
    creator_id    bigint       not null comment '创建者主键',
    created_time  datetime     not null comment '创建时间',
    modifier_id   bigint       not null comment '修改者主键',
    modified_time timestamp    not null comment '最近修改时间',
    constraint constraint_code_uindex unique (code)
) comment '约束';
