# AGENTS.md

Este arquivo define instrucoes para agentes/assistentes ao trabalhar neste repositorio.
Baseado na documentacao em `docs/`.

## Contexto e objetivo
- O projeto implementa um **Ledger Core** (motor contabil) separado do dominio de cartao.
- A fase 1 adiciona um **Wallet API** como camada de produto sobre o Ledger.
- O sistema deve suportar **multi-tenancy** com isolamento forte por `tenantId`.

## Principios e regras contabeis (nao negociaveis)
- Imutabilidade: `ledger_transactions` e `entries` sao **append-only** (sem UPDATE/DELETE).
- Double-entry: soma de CREDIT deve igualar soma de DEBIT **por moeda**.
- Idempotencia: `idempotencyKey` garante nao duplicar lancamentos.
- Auditabilidade: manter `created_at`, `external_reference` e rastreio.
- Isolamento: **nenhuma** account/entry/transaction existe sem `tenantId`.

## Arquitetura esperada
- Camadas:
  - API (controllers/resources + DTOs)
  - Application (use cases, transacoes, idempotencia)
  - Domain (entidades, VOs, validacoes)
  - Infra (persistencia, migrations, config)
- Modulos:
  - `ledger` (core contabil)
  - `wallet` (produto/API sobre ledger)

## Modelo de dominio (resumo)
- Account (ledger): id, name, type, currency, allowNegative, status, tenantId
- LedgerTransaction: id, idempotencyKey, externalReference, description, occurredAt, tenantId
- Entry: id, transactionId, accountId, direction, amountMinor, currency, occurredAt, tenantId
- WalletAccount: accountId, tenantId, ownerType, ownerId, currency, status, label, ledgerAccountId

## Regras de negocio chave
- Contas envolvidas em uma transacao devem ser do **mesmo tenant**.
- `idempotencyKey` e unica **por tenant**.
- `allowNegative=false` bloqueia transacao que gere saldo negativo.
- Wallet nunca altera saldo direto; sempre gera `LedgerTransaction`.
- 1 WalletAccount principal por (tenantId, ownerType, ownerId, currency).

## APIs esperadas (resumo)
Ledger:
- `POST /ledger/accounts`
- `POST /ledger/transactions` (idempotente)
- `GET /ledger/accounts/{id}/balance`
- `GET /ledger/accounts/{id}/statement`

Wallet (fase 1):
- `POST /accounts`
- `GET /accounts/{accountId}/balance`
- `GET /accounts/{accountId}/statement`
- `POST /transfers`

Todas as chamadas do Wallet exigem `X-API-Key` para resolver `tenantId`.

## Persistencia e migrations
- Banco alvo: PostgreSQL (H2 em modo PostgreSQL para testes).
- Tabelas chave: `accounts`, `ledger_transactions`, `entries`, `tenants`, `wallet_accounts` (se aplicavel).
- Constraints:
  - `ledger_transactions`: UNIQUE(tenant_id, idempotency_key)
  - `entries`: `tenant_id` deve igualar `ledger_transactions.tenant_id`
  - `accounts`: UNIQUE(tenant_id, owner_type, owner_id, currency, account_type)
- Indices:
  - `entries(tenant_id, account_id, occurred_at)`
  - `ledger_transactions(tenant_id, idempotency_key)`
  - `accounts(tenant_id, owner_id)`

## Imutabilidade no banco
- Bloquear UPDATE/DELETE em `ledger_transactions` e `entries` (triggers).
- Usuario da aplicacao com permissoes apenas de SELECT/INSERT nessas tabelas.

## Testes
- Unitarios: double-entry, moeda, saldo negativo, status de conta.
- Integracao: H2 `MODE=PostgreSQL`, limpeza automatica entre testes.
- Multi-tenant:
  - mesmos dados em tenants diferentes devem coexistir
  - cross-tenant deve falhar

## Convencoes e padroes
- Saldo: definir uma convencao unica (ex: CREDIT - DEBIT) e manter consistente.
- Dinheiro: preferir `long` em unidades menores (amountMinor).
- Logs: incluir `tenantId` e `idempotencyKey`.

## Como contribuir (agentes)
- Respeitar principios de imutabilidade e isolamento antes de qualquer mudanca.
- Evitar alterar historico; usar transacoes de compensacao para correcao.
- Ao criar endpoints, sempre propagar `tenantId` para o Ledger.
- Manter compatibilidade com PostgreSQL.

