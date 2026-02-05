create table if not exists payments (
    id uuid primary key,
    tenant_id uuid not null,
    type varchar(32) not null,
    status varchar(32) not null,
    amount_minor bigint not null,
    currency varchar(16) not null,
    reference_type varchar(64) not null,
    reference_id varchar(128) not null,
    idempotency_key varchar(128) not null,
    external_provider varchar(64),
    external_payment_id varchar(128),
    external_txid varchar(128),
    qr_code varchar(2048),
    copy_paste varchar(2048),
    expires_at timestamp,
    created_at timestamp not null,
    updated_at timestamp not null,
    confirmed_at timestamp,
    failure_reason varchar(255),
    wallet_from_account_id uuid,
    wallet_to_account_id uuid,
    ledger_transaction_id uuid,
    constraint fk_payments_tenant
        foreign key (tenant_id) references tenants (id)
);

create unique index if not exists uq_payments_tenant_idempotency
    on payments (tenant_id, idempotency_key);

create index if not exists idx_payments_reference
    on payments (tenant_id, reference_type, reference_id);

create index if not exists idx_payments_external_id
    on payments (tenant_id, external_payment_id);

create table if not exists payment_webhook_events (
    id uuid primary key,
    tenant_id uuid not null,
    external_payment_id varchar(128) not null,
    event_type varchar(64) not null,
    received_at timestamp not null,
    constraint fk_payment_webhook_events_tenant
        foreign key (tenant_id) references tenants (id)
);

create unique index if not exists uq_payment_webhook_event
    on payment_webhook_events (tenant_id, external_payment_id, event_type);
