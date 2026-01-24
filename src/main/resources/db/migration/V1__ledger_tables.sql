create table if not exists ledger_accounts (
    id uuid primary key,
    name varchar(255) not null,
    type varchar(32) not null,
    currency varchar(16) not null,
    allow_negative boolean not null,
    status varchar(32) not null,
    created_at timestamp not null
);

create table if not exists ledger_transactions (
    id uuid primary key,
    idempotency_key varchar(255) not null,
    external_reference varchar(255),
    description varchar(255),
    occurred_at timestamp not null,
    created_at timestamp not null
);

create unique index if not exists uq_ledger_transactions_idempotency
    on ledger_transactions (idempotency_key);

create table if not exists ledger_entries (
    id uuid primary key,
    transaction_id uuid not null,
    account_id uuid not null,
    direction varchar(16) not null,
    amount_minor bigint not null,
    currency varchar(16) not null,
    occurred_at timestamp not null,
    created_at timestamp not null,
    constraint fk_entries_transaction
        foreign key (transaction_id) references ledger_transactions (id),
    constraint fk_entries_account
        foreign key (account_id) references ledger_accounts (id)
);

create index if not exists idx_entries_account_date
    on ledger_entries (account_id, occurred_at);

create index if not exists idx_entries_transaction
    on ledger_entries (transaction_id);
