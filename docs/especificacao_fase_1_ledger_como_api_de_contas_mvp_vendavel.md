# Especificação — Fase 1: Wallet API sobre Ledger (MVP Vendável)

> Objetivo: transformar o Ledger Core já existente em um **produto utilizável via API**, através de um módulo de **Wallet**, permitindo que plataformas criem contas para seus usuários, movimentem saldo entre eles e consultem saldo/extrato de forma segura e multi-tenant.

O Ledger continua sendo o **motor contábil interno**. A Wallet API é a **camada de produto** exposta a clientes externos.

---

## 1. Proposta de Valor do MVP

Este MVP permite que uma plataforma terceira tenha:

- Contas virtuais para seus usuários finais
- Transferências internas entre usuários
- Saldo confiável e auditável
- Extrato de movimentações

Sem precisar construir um motor contábil próprio.

Produto posicionável como:

> **Wallet / Ledger API para plataformas digitais**

---

## 2. Escopo da Fase 1

### Em escopo

- Módulo **Wallet** como camada sobre o Ledger
- API para criação de contas de usuários (WalletAccounts)
- API de consulta de saldo (derivado do Ledger)
- API de extrato (derivado do Ledger)
- API de transferência entre contas
- Idempotência em transferências
- Multi-tenant via API Key

### Fora de escopo

- Cartão de crédito
- Juros e taxas
- Integração bancária real
- Split entre múltiplas partes
- Webhooks

---

## 3. Arquitetura Lógica

```
Cliente da API
       │
       ▼
   Wallet API  (produto)
       │
       ▼
   Ledger Core (motor contábil)
```

- A Wallet **nunca altera saldo diretamente**.
- Toda movimentação financeira é feita via **LedgerTransaction**.

---

## 4. Conceitos Principais

### 4.1 LedgerAccount

Conta contábil real do núcleo do ledger que participa de lançamentos (entries) e cálculo de saldo.

Características:

- Pertence a um `tenantId`
- Possui `currency`, `type`, `allowNegative`
- Não conhece usuário, produto ou wallet

---

### 4.2 WalletAccount (novo — camada de produto)

Representa a conta exposta ao cliente da API.

Ela é um **espelho funcional** de uma `LedgerAccount`.

Campos principais:

- `accountId` (UUID da Wallet)
- `tenantId`
- `ownerType` (ex: CUSTOMER)
- `ownerId` (ID do usuário no sistema do cliente)
- `currency`
- `status`
- `label` (opcional)
- `ledgerAccountId` (FK lógica para LedgerAccount)

**Regra:** WalletAccount não armazena saldo. Saldo e extrato são sempre consultados no Ledger.

Restrição inicial:

> 1 WalletAccount principal por (tenantId, ownerType, ownerId, currency)

---

### 4.3 Transferência

Movimentação de saldo entre duas WalletAccounts do mesmo tenant.

A Wallet traduz a operação para o Ledger como:

- DEBIT na LedgerAccount de origem
- CREDIT na LedgerAccount de destino

---

### 4.4 Resolução de Tenant

Cada requisição deve conter:

```
X-API-Key: <apiKey>
```

Fluxo:

1. API Key → resolve `tenantId`
2. `tenantId` é propagado para todos os use cases do Ledger
3. É proibida qualquer operação entre tenants diferentes

---

## 5. Requisitos Funcionais

### RF-01 — Criar Conta (WalletAccount)

**POST /accounts**

Headers:

- `X-API-Key: <apiKey>`

Request:

```json
{
  "ownerType": "CUSTOMER",
  "ownerId": "user-123",
  "currency": "BRL",
  "label": "Main Wallet"
}
```

Processo:

1. Resolver `tenantId`
2. Validar unicidade (tenantId, ownerType, ownerId, currency)
3. Criar LedgerAccount correspondente (ex: LIABILITY, allowNegative=false)
4. Criar WalletAccount vinculando `ledgerAccountId`

Response:

```json
{
  "accountId": "wallet-account-uuid",
  "ownerType": "CUSTOMER",
  "ownerId": "user-123",
  "currency": "BRL",
  "status": "ACTIVE"
}
```

---

### RF-02 — Consultar Saldo

**GET /accounts/{accountId}/balance**

Processo:

- Resolver `tenantId`
- Buscar WalletAccount → obter `ledgerAccountId`
- Chamar Ledger: `GetBalance(tenantId, ledgerAccountId)`

Response:

```json
{
  "accountId": "wallet-account-uuid",
  "balanceMinor": 100000,
  "currency": "BRL"
}
```

---

### RF-03 — Extrato da Conta

**GET /accounts/{accountId}/statement**

Processo:

- Resolver `tenantId`
- Buscar WalletAccount → `ledgerAccountId`
- Consultar Ledger por entries da conta

Response:

```json
{
  "accountId": "wallet-account-uuid",
  "items": [
    {
      "transactionId": "uuid",
      "occurredAt": "...",
      "description": "Transfer",
      "direction": "DEBIT",
      "amountMinor": 5000,
      "currency": "BRL"
    }
  ]
}
```

---

### RF-04 — Transferência entre Contas

**POST /transfers**

Headers:

- `X-API-Key: <apiKey>`

Request:

```json
{
  "idempotencyKey": "txn-123",
  "fromAccountId": "wallet-account-uuid",
  "toAccountId": "wallet-account-uuid",
  "amountMinor": 5000,
  "currency": "BRL",
  "description": "User transfer"
}
```

Processo:

1. Resolver `tenantId`
2. Buscar WalletAccounts (origem e destino)
3. Validar saldo suficiente na origem
4. Chamar Ledger para criar LedgerTransaction (2 entries)

Resposta:

```json
{
  "transactionId": "uuid",
  "status": "POSTED"
}
```

---

## 6. Segurança

- Autenticação via API Key
- Todas as operações isoladas por `tenantId`
- Proibido acesso a contas de outro tenant

---

## 7. Requisitos Não Funcionais

- Operações financeiras atômicas
- Ledger permanece imutável
- Logs com tenantId + idempotencyKey
- Rate limit por API Key

---

## 8. Estrutura Técnica Esperada

Novos módulos:

```
/wallet/api
/wallet/application
/wallet/domain
```

Reutilizando:

```
/ledger
```

---

## 9. Entregáveis da Fase 1

- Entidade WalletAccount
- Integração Wallet → LedgerAccount
- Endpoints REST de accounts e transfers
- Use case TransferBetweenAccounts
- Testes de integração multi-tenant

---

## 10. Resultado Esperado

Ao final da Fase 1 será possível demonstrar:

> “Criar contas de usuários, movimentar saldo entre eles e consultar extratos via API multi-tenant.”

Base pronta para monetização como **Wallet / Ledger API**.

