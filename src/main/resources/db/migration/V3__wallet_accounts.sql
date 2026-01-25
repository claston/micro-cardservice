create table if not exists wallet_accounts (
    id uuid primary key,
    tenant_id uuid not null,
    owner_type varchar(32) not null,
    owner_id varchar(255) not null,
    currency varchar(16) not null,
    status varchar(32) not null,
    label varchar(255),
    ledger_account_id uuid not null,
    created_at timestamp not null,
    constraint fk_wallet_accounts_tenant
        foreign key (tenant_id) references tenants (id),
    constraint fk_wallet_accounts_ledger_account
        foreign key (ledger_account_id) references ledger_accounts (id)
);

create unique index if not exists uq_wallet_accounts_owner_currency
    on wallet_accounts (tenant_id, owner_type, owner_id, currency);

create index if not exists idx_wallet_accounts_tenant_owner
    on wallet_accounts (tenant_id, owner_id);

create index if not exists idx_wallet_accounts_tenant_ledger_account
    on wallet_accounts (tenant_id, ledger_account_id);
