create extension if not exists pgcrypto;


create type category_kind as enum ('INCOME', 'EXPENSE');


create table users
(
    id            uuid primary key default gen_random_uuid(),
    login         varchar(128) not null unique,
    password_hash varchar(100) not null
);


create table categories
(
    id            uuid primary key default gen_random_uuid(),
    user_id       uuid          not null references users (id) on delete cascade,
    name          varchar(255)  not null,
    kind          category_kind not null,
    budget_amount numeric(19, 2),
    constraint uq_categories_user_name unique (user_id, name),
    constraint chk_categories_income_without_budget
        check (
            (kind = 'INCOME' and budget_amount is null)
                or
            (kind = 'EXPENSE')
            ),
    constraint chk_categories_budget_positive
        check (budget_amount is null or budget_amount > 0)
);


create table operations
(
    id          uuid primary key        default gen_random_uuid(),
    category_id uuid           not null references categories (id) on delete restrict,
    amount      numeric(19, 2) not null,
    description text,
    happened_at timestamptz    not null default now(),
    constraint chk_operations_amount_positive
        check (amount > 0)
);
