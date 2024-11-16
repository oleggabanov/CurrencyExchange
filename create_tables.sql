create table if not exists currencies
(
    id        integer primary key autoincrement not null,
    code      varchar(3)                        not null,
    full_name varchar(30)                       not null,
    sign      varchar(1)                        not null,
    unique (code, full_name, sign)
);

create table if not exists exchange_rates
(
    id                 integer primary key autoincrement not null,
    base_currency_id   int references currencies (id)    not null,
    target_currency_id int references currencies (id)    not null,
    rate               decimal(7) check ( rate >= 0 )    not null,
    unique (base_currency_id, target_currency_id, rate)
);

insert into currencies (code, full_name, sign)
values ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€'),
       ('RUB', 'Russian Rubble', '₽');

insert into exchange_rates(base_currency_id, target_currency_id, rate)
values (1, 2, 0.9288),
       (2, 1, 1.0769);