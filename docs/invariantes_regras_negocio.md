# Invariantes e Regras de Negocio (Ledger + Wallet + Customer)

Este documento consolida as invariantes e regras de negocio do sistema para evitar ambiguidade
e manter o entendimento comum entre times e codigo. Ele resume e normaliza o que ja existe
na documentacao de `docs/` e no `AGENTS.md`.

## 1) Regras Globais (nao negociaveis)

1. Imutabilidade
   - `ledger_transactions` e `entries` sao append-only.
   - Nao pode haver UPDATE/DELETE nessas tabelas via aplicacao.
   - Correcoes e ajustes sao feitas por novas transacoes (compensacao).

2. Double-entry por moeda
   - Para cada ledger transaction, a soma de CREDIT deve igualar a soma de DEBIT por moeda.

3. Idempotencia
   - `idempotencyKey` garante que uma mesma operacao nao seja postada duas vezes.
   - A chave e unica por tenant.

4. Isolamento multi-tenant
   - Nenhuma Account, Entry ou LedgerTransaction existe sem `tenantId`.
   - Uma transacao nunca pode envolver contas de tenants diferentes.
   - Todas as consultas e comandos devem sempre filtrar por `tenantId`.

5. Auditabilidade
   - Manter `created_at` e `external_reference` quando aplicavel.
   - Logs devem incluir `tenantId` e `idempotencyKey`.

6. Moeda e dinheiro
   - Valores monetarios devem usar `long` em unidades menores (`amountMinor`).
   - Moeda deve ser consistente entre conta e entries.

## 2) Ledger Core (contabil)

2.1 Conta (Account)
- Campos obrigatorios: `name`, `type`, `currency`, `allowNegative`, `status`, `tenantId`.
- `status` controla participacao em transacoes (contas inativas nao devem receber entries).

2.2 LedgerTransaction e Entry
- Uma transacao deve conter no minimo 2 entries.
- `occurredAt` pode ser informado; caso nao, default = now.
- `entries.currency` deve bater com a moeda da conta (ou herdar quando nao enviado).

2.3 Double-entry
- Validar fechamento por moeda antes de persistir a transacao.
- Se nao fechar, retornar 400 (validacao).

2.4 Saldo negativo
- Se `allowNegative=false`, a transacao nao pode resultar em saldo negativo.
- Violacao deve resultar em erro de negocio (409).

2.5 Idempotencia
- Requisicoes com mesmo `idempotencyKey` (no mesmo tenant) devem retornar a mesma transacao.
- Nao duplicar entries.

2.6 Extrato e saldo
- Saldo e extrato sao derivados do ledger; nao armazenar saldo no ledger.
- Definir convencao unica de saldo (ex.: CREDIT - DEBIT) e manter consistente.

## 3) Wallet API (camada de produto)

3.1 WalletAccount
- E um espelho funcional de `LedgerAccount`.
- Nao armazena saldo.
- Restricao: 1 WalletAccount principal por (tenantId, ownerType, ownerId, currency).

3.2 Criacao de conta
- Resolver `tenantId` via `X-API-Key`.
- Validar unicidade da WalletAccount.
- Criar LedgerAccount correspondente e vincular via `ledgerAccountId`.

3.3 Transferencias
- Sempre gerar LedgerTransaction com 2 entries (DEBIT origem, CREDIT destino).
- Proibido transferir entre tenants diferentes.
- Aplicar idempotencia por `idempotencyKey` por tenant.

3.4 Consultas
- `balance` e `statement` sao sempre buscados no Ledger.

## 4) Customer (MVP)

4.1 Regras de dominio
- `type`: `INDIVIDUAL` | `BUSINESS`.
- `documentType`: `CPF` | `CNPJ`.
- `documentNumber` deve ser normalizado (somente digitos na resposta).
- `status`: `ACTIVE` | `INACTIVE`.

4.2 Unicidade
- Nao deve existir mais de um customer com o mesmo documento no mesmo tenant.
- Busca por documento deve retornar `items=[]` quando vazio (nao usar 204).

4.3 Headers obrigatorios
- Todas as rotas exigem `X-API-Key`.

## 5) Erros e contrato HTTP

- Erros seguem Problem Details:
  - `type`, `title`, `status`, `detail`, `instance`
  - `errorCode` por modulo
  - `violations[]` para validacao
  - `traceId` sempre presente (usa `X-Request-Id` se informado)

Mapa de status (resumo):
- 400: validacoes de DTO/regra (ex.: double-entry, moeda invalida)
- 401: API key invalida
- 404: entidade nao encontrada
- 409: conflito de idempotencia/duplicidade ou regra de negocio (ex.: saldo insuficiente)

## 6) Persistencia e banco (PostgreSQL / H2)

- Tabelas chave: `accounts`, `ledger_transactions`, `entries`, `tenants`, `wallet_accounts`.
- Constraints:
  - `ledger_transactions`: UNIQUE (tenant_id, idempotency_key)
  - `entries`: `tenant_id` deve igualar `ledger_transactions.tenant_id`
  - `accounts`: UNIQUE (tenant_id, owner_type, owner_id, currency, account_type)
- Indices:
  - `entries(tenant_id, account_id, occurred_at)`
  - `ledger_transactions(tenant_id, idempotency_key)`
  - `accounts(tenant_id, owner_id)`

## 7) Observabilidade e testes

- Logs devem incluir `tenantId` e `idempotencyKey`.
- Testes unitarios: double-entry, moeda, saldo negativo, status de conta.
- Testes de integracao: H2 `MODE=PostgreSQL`, limpeza automatica entre testes.
- Multi-tenant:
  - mesmos dados em tenants diferentes devem coexistir
  - cross-tenant deve falhar

## 8) Origem das regras (documentos fonte)

- `AGENTS.md`
- `docs/guidelines_projeto.md`
- `docs/especificacao_nucleo_ledger_separado_do_dominio_de_cartao.md`
- `docs/especificacao_extensao_multi_tenant_para_o_ledger_core.md`
- `docs/especificacao_fase_1_ledger_como_api_de_contas_mvp_vendavel.md`
- `docs/api_customer_mvp_wallet.md`
