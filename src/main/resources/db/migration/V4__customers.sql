create table if not exists customers (
    id uuid primary key,
    tenant_id uuid not null,
    type varchar(32) not null,
    name varchar(255) not null,
    document_type varchar(16) not null,
    document_number varchar(32) not null,
    status varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_customers_tenant
        foreign key (tenant_id) references tenants (id)
);

create unique index if not exists uq_customers_document
    on customers (tenant_id, document_type, document_number);

create index if not exists idx_customers_tenant_created_at
    on customers (tenant_id, created_at);

