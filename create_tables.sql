create table if not exists currencies
(
    id        integer primary key autoincrement not null,
    code      varchar                           not null,
    full_name varchar                           not null,
    sign      varchar                           not null,
    unique (code, full_name, sign)
);

create table if not exists exchange_rates
(
    id                 integer primary key autoincrement not null,
    base_currency_id   int references currencies (id)    not null,
    target_currency_id int references currencies (id)    not null,
    rate               decimal(6)                        not null,
    unique (base_currency_id, target_currency_id, rate)
);

insert into currencies (code, full_name, sign)
values ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€');

insert into exchange_rates(base_currency_id, target_currency_id, rate)
values (1, 3, 0.00953),
       (2, 3, 0.9263);