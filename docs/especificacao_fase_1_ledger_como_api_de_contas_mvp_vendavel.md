# Especificacao - Fase 1: Wallet API sobre Ledger (MVP Vendavel)

> Objetivo: expor o Ledger Core via modulo Wallet, permitindo criar contas, transferir saldo e consultar saldo/extrato.

---

## 1. Proposta de Valor do MVP

- Contas virtuais para usuarios finais
- Transferencias internas entre usuarios
- Saldo confiavel e auditavel
- Extrato de movimentacoes

---

## 2. Escopo da Fase 1

### Em escopo
- Modulo Wallet como camada sobre o Ledger
- API para criacao de contas (WalletAccounts)
- API de consulta de saldo
- API de extrato
- API de transferencia entre contas

### Fora de escopo
- Cartao de credito
- Juros e taxas
- Integracao bancaria real
- Split entre multiplas partes
- Webhooks

---

## 3. Conceitos Principais

### 3.1 LedgerAccount
Conta contabil interna do ledger que participa de entries e calculo de saldo.

### 3.2 WalletAccount
Conta exposta ao cliente da API, espelhando uma LedgerAccount.

Campos principais:
- accountId
- tenantId
- ownerType
- ownerId
- currency
- status
- label (opcional)
- ledgerAccountId

### 3.3 Transferencia
Movimentacao entre duas WalletAccounts do mesmo tenant, traduzida em duas entries no Ledger.

---

## 4. Requisitos Funcionais

### RF-01 - Criar Conta (WalletAccount)
POST /accounts

Request:
```json
{
  "ownerType": "CUSTOMER",
  "ownerId": "user-123",
  "currency": "BRL",
  "label": "Main Wallet"
}
```

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

### RF-02 - Consultar Saldo
GET /accounts/{accountId}/balance

Response:
```json
{
  "accountId": "wallet-account-uuid",
  "balanceMinor": 100000,
  "currency": "BRL"
}
```

### RF-03 - Extrato da Conta
GET /accounts/{accountId}/statement

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

### RF-04 - Transferencia entre Contas
POST /transfers

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

Resposta:
```json
{
  "transactionId": "uuid",
  "status": "POSTED"
}
```

---

## 5. Estrutura Tecnica Esperada

Novos modulos:
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

## 6. Entregaveis da Fase 1

- Entidade WalletAccount
- Integracao Wallet -> LedgerAccount
- Endpoints REST de accounts e transfers
- Use case TransferBetweenAccounts
- Testes de integracao multi-tenant

---

## 7. Resultado Esperado

Criar contas de usuarios, movimentar saldo entre eles e consultar extratos via API multi-tenant.
