# EspecificaÃ§Ã£o â€” Fase 1: Wallet API sobre Ledger (MVP VendÃ¡vel)

> Objetivo: transformar o Ledger Core jÃ¡ existente em um **produto utilizÃ¡vel via API**, atravÃ©s de um mÃ³dulo de **Wallet**, permitindo que plataformas criem contas para seus usuÃ¡rios, movimentem saldo entre eles e consultem saldo/extrato de forma segura e multi-tenant.

O Ledger continua sendo o **motor contÃ¡bil interno**. A Wallet API Ã© a **camada de produto** exposta a clientes externos.

---

## 1. Proposta de Valor do MVP

Este MVP permite que uma plataforma terceira tenha:

- Contas virtuais para seus usuÃ¡rios finais
- TransferÃªncias internas entre usuÃ¡rios
- Saldo confiÃ¡vel e auditÃ¡vel
- Extrato de movimentaÃ§Ãµes

Sem precisar construir um motor contÃ¡bil prÃ³prio.

Produto posicionÃ¡vel como:

> **Wallet / Ledger API para plataformas digitais**

---

## 2. Escopo da Fase 1

### Em escopo

- MÃ³dulo **Wallet** como camada sobre o Ledger
- API para criaÃ§Ã£o de contas de usuÃ¡rios (WalletAccounts)
- API de consulta de saldo (derivado do Ledger)
- API de extrato (derivado do Ledger)
- API de transferÃªncia entre contas
- IdempotÃªncia em transferÃªncias
- Multi-tenant via API Key

### Fora de escopo

- CartÃ£o de crÃ©dito
- Juros e taxas
- IntegraÃ§Ã£o bancÃ¡ria real
- Split entre mÃºltiplas partes
- Webhooks

---

## 3. Arquitetura LÃ³gica

```
Cliente da API
       â”‚
       â–¼
   Wallet API  (produto)
       â”‚
       â–¼
   Ledger Core (motor contÃ¡bil)
```

- A Wallet **nunca altera saldo diretamente**.
- Toda movimentaÃ§Ã£o financeira Ã© feita via **LedgerTransaction**.

---

## 4. Conceitos Principais

### 4.1 LedgerAccount

Conta contÃ¡bil real do nÃºcleo do ledger que participa de lanÃ§amentos (entries) e cÃ¡lculo de saldo.

CaracterÃ­sticas:

- Pertence a um `tenantId`
- Possui `currency`, `type`, `allowNegative`
- NÃ£o conhece usuÃ¡rio, produto ou wallet

---

### 4.2 WalletAccount (novo â€” camada de produto)

Representa a conta exposta ao cliente da API.

Ela Ã© um **espelho funcional** de uma `LedgerAccount`.

Campos principais:

- `accountId` (UUID da Wallet)
- `tenantId`
- `ownerType` (ex: CUSTOMER)
- `ownerId` (ID do usuÃ¡rio no sistema do cliente)
- `currency`
- `status`
- `label` (opcional)
- `ledgerAccountId` (FK lÃ³gica para LedgerAccount)

**Regra:** WalletAccount nÃ£o armazena saldo. Saldo e extrato sÃ£o sempre consultados no Ledger.

RestriÃ§Ã£o inicial:

> 1 WalletAccount principal por (tenantId, ownerType, ownerId, currency)

---

### 4.3 TransferÃªncia

MovimentaÃ§Ã£o de saldo entre duas WalletAccounts do mesmo tenant.

A Wallet traduz a operaÃ§Ã£o para o Ledger como:

- DEBIT na LedgerAccount de origem
- CREDIT na LedgerAccount de destino

---

### 4.4 ResoluÃ§Ã£o de Tenant

Cada requisiÃ§Ã£o deve conter:

```
X-API-Key: <apiKey>
```

Fluxo:

1. API Key â†’ resolve `tenantId`
2. `tenantId` Ã© propagado para todos os use cases do Ledger
3. Ã‰ proibida qualquer operaÃ§Ã£o entre tenants diferentes

---

## 5. Requisitos Funcionais

### RF-01 â€” Criar Conta (WalletAccount)

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

### RF-02 â€” Consultar Saldo

**GET /accounts/{accountId}/balance**

Processo:

- Resolver `tenantId`
- Buscar WalletAccount â†’ obter `ledgerAccountId`
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

### RF-03 â€” Extrato da Conta

**GET /accounts/{accountId}/statement**

Processo:

- Resolver `tenantId`
- Buscar WalletAccount â†’ `ledgerAccountId`
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

### RF-04 â€” TransferÃªncia entre Contas

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

Em caso de `idempotencyKey` ja usado no mesmo tenant, retornar **409 Conflict** com:
- `errorCode`: `WALLET_IDEMPOTENCY_CONFLICT`
- `meta.transactionId`: ID da transacao existente
- `meta.idempotencyKey`: chave enviada

Exemplo de resposta 409:

```json
{
  "type": "https://errors.yourdomain.com/conflict",
  "title": "Conflict",
  "status": 409,
  "detail": "idempotencyKey already used for this tenant",
  "instance": "/transfers",
  "errorCode": "WALLET_IDEMPOTENCY_CONFLICT",
  "meta": {
    "transactionId": "existing-transaction-uuid",
    "idempotencyKey": "txn-123"
  },
  "traceId": "request-trace-id"
}
```

---

## 6. SeguranÃ§a

- AutenticaÃ§Ã£o via API Key
- Todas as operaÃ§Ãµes isoladas por `tenantId`
- Proibido acesso a contas de outro tenant

---

## 7. Requisitos NÃ£o Funcionais

- OperaÃ§Ãµes financeiras atÃ´micas
- Ledger permanece imutÃ¡vel
- Logs com tenantId + idempotencyKey
- Rate limit por API Key

---

## 8. Estrutura TÃ©cnica Esperada

Novos mÃ³dulos:

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

## 9. EntregÃ¡veis da Fase 1

- Entidade WalletAccount
- IntegraÃ§Ã£o Wallet â†’ LedgerAccount
- Endpoints REST de accounts e transfers
- Use case TransferBetweenAccounts
- Testes de integraÃ§Ã£o multi-tenant

---

## 10. Resultado Esperado

Ao final da Fase 1 serÃ¡ possÃ­vel demonstrar:

> â€œCriar contas de usuÃ¡rios, movimentar saldo entre eles e consultar extratos via API multi-tenant.â€

Base pronta para monetizaÃ§Ã£o como **Wallet / Ledger API**.


