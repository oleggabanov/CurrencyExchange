insert into currencies (code, full_name, sign)
values ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€'),
       ('RUB', 'Russian Rubble', '₽');

insert into exchange_rates(base_currency_id, target_currency_id, rate)
values (1, 2, 0.9288),
       (2, 1, 1.0769);