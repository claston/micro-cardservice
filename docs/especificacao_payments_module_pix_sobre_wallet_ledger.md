# Especificacao - Payments Module (Pix) sobre Wallet + Ledger

> Objetivo: implementar o modulo Payments (cash-in e cash-out via Pix) como camada de integracao externa, usando Wallet e Ledger sem modificar o Ledger Core.

---

## 1. Escopo

### Em escopo (MVP)
- Pix cash-in via PSP (criar cobranca/QR, receber confirmacao via webhook)
- Pix cash-out (payout) via PSP
- Estados do pagamento: PENDING, CONFIRMED, FAILED, CANCELED
- Integracao Payments -> Wallet usando transferencias internas

### Fora de escopo (MVP)
- Chargeback/disputas
- Boleto/cartao
- Split automatico
- Conciliacao bancaria completa

---

## 2. Arquitetura e Responsabilidades

- Ledger registra LedgerTransaction e Entries e calcula saldo.
- Wallet expoe WalletAccount e POST /transfers.
- Payments fala com PSP, mantem o estado do pagamento e decide quando lancar no Wallet/Ledger.

Fluxo alto nivel:
```
Client
  |
Payments API -> PSP API
  |
Wallet (transfers) -> Ledger (entries)
```

---

## 3. Modelo de Dados (Payments)

Tabela: payments

Campos:
- id (UUID)
- tenant_id (UUID)
- type (PIX_CASHIN, PIX_PAYOUT)
- status (PENDING, CONFIRMED, FAILED, CANCELED)
- amount_minor (long)
- currency (string)
- reference_type (string)
- reference_id (string)
- idempotency_key (string)
- external_provider (string)
- external_payment_id (string)
- external_txid (string)
- created_at, updated_at
- confirmed_at (nullable)
- failure_reason (nullable)
- wallet_from_account_id (UUID, nullable)
- wallet_to_account_id (UUID, nullable)
- ledger_transaction_id (UUID, nullable)

Tabela: payment_webhook_events

Campos:
- id (UUID)
- tenant_id (UUID)
- external_payment_id (string)
- event_type (string)
- received_at (timestamp)

---

## 4. Contas Internas (Wallet) usadas por Payments

- CASH_AT_PSP (INTERNAL)
- OUTBOUND_CLEARING (INTERNAL)

---

## 5. APIs (Contrato)

### POST /payments/pix/charges
Request:
```json
{
  "referenceType": "PLATFORM_TRANSACTION",
  "referenceId": "txn-123",
  "amountMinor": 12000,
  "currency": "BRL",
  "payer": {
    "name": "Joao",
    "document": "12345678900"
  },
  "creditToWalletAccountId": "wallet-account-uuid"
}
```

Response:
```json
{
  "paymentId": "uuid",
  "status": "PENDING",
  "externalPaymentId": "...",
  "txid": "...",
  "qrCode": "<base64 ou url>",
  "copyPaste": "000201...",
  "expiresAt": "..."
}
```

### POST /payments/pix/payouts
Request:
```json
{
  "referenceType": "SETTLEMENT",
  "referenceId": "settlement-456",
  "amountMinor": 50000,
  "currency": "BRL",
  "pixKey": "user@email.com",
  "debitFromWalletAccountId": "wallet-account-uuid",
  "description": "Payout"
}
```

Response:
```json
{
  "paymentId": "uuid",
  "status": "PENDING",
  "externalPaymentId": "..."
}
```

### POST /payments/webhooks/psp
Recebe eventos do PSP para atualizar o estado do pagamento.

### GET /payments/{id}
Consulta status e detalhes do pagamento.

### GET /payments/by-reference
Consulta pagamento por referenceType/referenceId.

---

**Fim da Especificacao - Payments Module**
