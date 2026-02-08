# Especificacao - Nucleo Ledger (separado do dominio de Cartao)

> Objetivo: criar um nucleo de Ledger independente do dominio de cartao, com APIs e modelo de dados proprios, permitindo que o dominio de cartao poste efeitos contabeis sem acoplamento.

---

## 1. Escopo do MVP

### Em escopo
1) Criar e gerenciar Accounts
2) Postar Ledger Transactions com Entries
3) Consultar saldo por conta
4) Consultar extrato por conta
5) Integracao com H2 para testes e PostgreSQL para producao

### Fora de escopo (por enquanto)
- Multi-moeda avancada (FX)
- Conciliacao bancaria completa
- Event streaming / outbox
- Regras complexas de limite/credito
- Split payments avancado

---

## 2. Arquitetura Proposta

Camadas:
- API (Resource/Controller)
- Application (Use Cases)
- Domain (Entidades/VOs)
- Infrastructure (Persistencia, migrations, config)

Estrutura sugerida:
```
/ledger
  /api
  /application
  /domain
  /infra
```

---

## 3. Modelo de Dados (sugestao)

Tabelas:
- accounts
- ledger_transactions
- entries

Campos minimos (proposta):

accounts
- id (UUID)
- name
- type
- currency
- allow_negative
- status
- created_at

ledger_transactions
- id (UUID)
- idempotency_key
- external_reference (nullable)
- description (nullable)
- occurred_at
- created_at

entries
- id (UUID)
- transaction_id (FK)
- account_id (FK)
- direction (DEBIT/CREDIT)
- amount_minor (BIGINT)
- currency
- occurred_at
- created_at

---

## 4. Contratos de API (proposta)

### POST /ledger/accounts
Request:
```json
{ "name": "Customer Wallet", "type": "ASSET", "currency": "BRL", "allowNegative": false }
```
Response:
```json
{ "accountId": "..." }
```

### POST /ledger/transactions
Request:
```json
{
  "idempotencyKey": "card-txn-123",
  "externalReference": "cardTxnId-123",
  "description": "Compra no merchant X",
  "occurredAt": "2026-01-24T10:00:00Z",
  "entries": [
    { "accountId": "A1", "direction": "DEBIT",  "amountMinor": 10000, "currency": "BRL" },
    { "accountId": "A2", "direction": "CREDIT", "amountMinor": 10000, "currency": "BRL" }
  ]
}
```
Responses:
- 201 Created com transactionId
- 200 OK quando idempotente

### GET /ledger/accounts/{id}/balance
Response:
```json
{ "accountId": "A1", "balanceMinor": 25000, "currency": "BRL" }
```

### GET /ledger/accounts/{id}/statement?from=&to=&page=&size=
Response:
```json
{
  "accountId": "A1",
  "items": [
    { "occurredAt": "...", "transactionId": "...", "description": "...", "direction": "DEBIT", "amountMinor": 10000, "currency": "BRL" }
  ],
  "page": 0,
  "size": 20,
  "total": 123
}
```

---

## 5. Entregaveis para o Codex

- Pacotes /ledger/api, /ledger/application, /ledger/domain, /ledger/infra
- DTOs e Resources para accounts/transactions
- Use cases (CreateAccount, PostTransaction, GetBalance, GetStatement)
- Entidades de persistencia + repositories
- Migrations Flyway
- Testes unitarios e de integracao + DbCleanIT

---

## 6. Perguntas em aberto

1) Convencao de saldo
2) Forma de armazenar dinheiro (long minor units ou BigDecimal)
3) Necessidade de endpoint de reversao no MVP

---

**Fim da Especificacao do Nucleo Ledger**
