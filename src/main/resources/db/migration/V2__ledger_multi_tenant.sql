create table if not exists tenants (
    id uuid primary key,
    name varchar(255) not null,
    status varchar(32) not null,
    created_at timestamp not null
);

insert into tenants (id, name, status, created_at)
select '00000000-0000-0000-0000-000000000000', 'DEFAULT', 'ACTIVE', current_timestamp
where not exists (
    select 1 from tenants where id = '00000000-0000-0000-0000-000000000000'
);

alter table ledger_accounts add column if not exists tenant_id uuid;
alter table ledger_transactions add column if not exists tenant_id uuid;
alter table ledger_entries add column if not exists tenant_id uuid;

update ledger_accounts
set tenant_id = '00000000-0000-0000-0000-000000000000'
where tenant_id is null;

update ledger_transactions
set tenant_id = '00000000-0000-0000-0000-000000000000'
where tenant_id is null;

update ledger_entries
set tenant_id = '00000000-0000-0000-0000-000000000000'
where tenant_id is null;

alter table ledger_accounts alter column tenant_id set not null;
alter table ledger_transactions alter column tenant_id set not null;
alter table ledger_entries alter column tenant_id set not null;

alter table ledger_accounts
    add constraint fk_ledger_accounts_tenant
        foreign key (tenant_id) references tenants (id);

alter table ledger_transactions
    add constraint fk_ledger_transactions_tenant
        foreign key (tenant_id) references tenants (id);

alter table ledger_entries
    add constraint fk_ledger_entries_tenant
        foreign key (tenant_id) references tenants (id);

drop index if exists uq_ledger_transactions_idempotency;
create unique index if not exists uq_ledger_transactions_tenant_idempotency
    on ledger_transactions (tenant_id, idempotency_key);

create index if not exists idx_entries_tenant_account_date
    on ledger_entries (tenant_id, account_id, occurred_at);

create index if not exists idx_accounts_tenant
    on ledger_accounts (tenant_id);
