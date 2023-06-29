-- remove unsigned as H2 does not support
DROP TABLE IF EXISTS product;
create table product
(
    id             bigint         not null auto_increment comment 'id' primary key,
    product_id     varchar(64)    not null comment 'the product id',
    product_name   varchar(128)   not null comment 'product name',
    description    varchar(256) comment 'product description',
    currency       varchar(15)    not null comment 'currency',
    price          decimal(26, 8) not null comment 'price',
    operator_uid   bigint         not null comment 'the operator uid',
    db_create_time datetime(3) default CURRENT_TIMESTAMP(3) comment 'db create time',
    db_modify_time datetime(3) default CURRENT_TIMESTAMP(3) on update CURRENT_TIMESTAMP(3) comment 'db create time'
);
create unique index IF NOT EXISTS uniq_index_product_id on product (product_id);
create index IF NOT EXISTS idx_operator_time on product (operator_uid, db_modify_time);


DROP TABLE IF EXISTS discount;
create table discount
(
    id                  bigint      not null auto_increment comment 'id' primary key,
    product_id          varchar(64) not null comment 'the product id',
    strategy            varchar(64) not null comment 'discount strategy',
    trigger_threshold   INTEGER comment 'if the total amount of that product bigger than the threashold, will apply the strategy and discount percentage',
    discount_percentage smallint    not null comment 'the discount percentage, eg 50 -> 50% off',
    tirgger_product_ids varchar(1024)  comment 'the trigger product ids',
    db_create_time      datetime(3) default CURRENT_TIMESTAMP(3) comment 'db create time',
    db_modify_time      datetime(3) default CURRENT_TIMESTAMP(3) on update CURRENT_TIMESTAMP(3) comment 'db modify time'
);
create unique index IF NOT EXISTS uniq_index_discount_product on discount (product_id);

DROP TABLE IF EXISTS account;
create table account
(
    id             bigint      not null auto_increment comment 'id' primary key,
    uid            bigint      not null comment 'the administrator uid',
    account_type   varchar(12) not null comment 'retail user or administrator',
    db_create_time datetime(3) default CURRENT_TIMESTAMP(3) comment 'db create time',
    db_modify_time datetime(3) default CURRENT_TIMESTAMP(3) on update CURRENT_TIMESTAMP(3) comment 'db modify time'
);
create unique index IF NOT EXISTS uniq_index_uid on account (uid);

DROP TABLE IF EXISTS cart;
create table cart
(
    id             bigint      not null auto_increment comment 'id' primary key,
    uid            bigint      not null comment 'the customer uid',
    product_id     varchar(64) not null comment 'the product id',
    count          INTEGER comment 'product count',
    db_create_time datetime(3) default CURRENT_TIMESTAMP(3) comment 'db create time',
    db_modify_time datetime(3) default CURRENT_TIMESTAMP(3) on update CURRENT_TIMESTAMP(3) comment 'db modify time'
);
create unique index IF NOT EXISTS uniq_index_uid_product on cart (uid, product_id);
create index IF NOT EXISTS idx_cart_product on cart (product_id);
