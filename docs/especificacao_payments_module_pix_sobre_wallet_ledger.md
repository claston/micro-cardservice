# Especificação — Payments Module (Pix) sobre Wallet + Ledger

> Objetivo: especificar a implementação do **módulo Payments** (cash-in e cash-out via Pix) como camada de integração externa e orquestração de estados, usando **Wallet** (contas/produto) e **Ledger** (lançamentos imutáveis), **sem modificar o Ledger Core**.

Este documento é orientado a implementação (Codex/dev), com requisitos funcionais, modelo de dados, APIs, fluxos, idempotência, segurança de webhooks e integração com PSP.

---

## 1. Escopo

### Em escopo (MVP)
- Pix **cash-in** via PSP (criar cobrança/QR, receber confirmação via webhook)
- Pix **cash-out** (payout) via PSP (solicitar envio, receber confirmação via webhook)
- Estados do pagamento: `PENDING`, `CONFIRMED`, `FAILED`, `CANCELED`
- Idempotência end-to-end por tenant
- Integração Payments → Wallet (créditos/débitos) usando transferências internas
- Segurança: verificação de assinatura do webhook do PSP
- Observabilidade: `traceId` e logs estruturados

### Fora de escopo (MVP)
- Chargeback/disputas
- Boleto/cartão
- Split automático (fica no módulo de domínio)
- Conciliação bancária completa (apenas base)

---

## 2. Arquitetura e Responsabilidades

- **Ledger**: registra `LedgerTransaction` e `Entries` (imutável) e calcula saldo.
- **Wallet**: expõe `WalletAccount` e `POST /transfers` (interno) para movimentar saldo entre contas.
- **Payments**: fala com PSP, mantém o **estado** do pagamento e decide **quando** lançar no Wallet/Ledger (somente após confirmação).

Fluxo alto nível:

```
Client (domain/app)
   │
   ▼
Payments API  ──► PSP API
   │              │
   │              └──► Webhook (CONFIRMED/FAILED)
   ▼
Wallet (transfers) ──► Ledger (entries)
```

---

## 3. Modelo de Dados (Payments)

### 3.1 Payment (abstrato)
Tabela: `payments`

Campos:
- `id` (UUID)
- `tenant_id` (UUID)
- `type` (`PIX_CASHIN`, `PIX_PAYOUT`)
- `status` (`PENDING`, `CONFIRMED`, `FAILED`, `CANCELED`)
- `amount_minor` (long)
- `currency` (string, ex: BRL)
- `reference_type` (string) — ex: `PLATFORM_TRANSACTION`, `SETTLEMENT`, `ORDER`
- `reference_id` (string)
- `idempotency_key` (string)
- `external_provider` (string, ex: `ASAAS`, `PAGARME`)
- `external_payment_id` (string)
- `external_txid` (string) — no caso Pix
- `created_at`, `updated_at`
- `confirmed_at` (nullable)
- `failure_reason` (nullable)
- `wallet_from_account_id` (UUID, nullable)
- `wallet_to_account_id` (UUID, nullable)
- `ledger_transaction_id` (UUID, nullable) — opcional se wallet retorna

Constraints/Índices:
- UNIQUE (`tenant_id`, `idempotency_key`)
- INDEX (`tenant_id`, `reference_type`, `reference_id`)
- INDEX (`tenant_id`, `external_payment_id`)

> Observação: `wallet_from_account_id`/`wallet_to_account_id` são IDs de **WalletAccount** (não ledger).

---

## 4. Contas Internas (Wallet) usadas por Payments

Por tenant, deve existir (criada sob demanda):

- `CASH_AT_PSP` (INTERNAL): representa o “caixa real”/saldo no PSP/banco (conceitual para conciliação)
- `OUTBOUND_CLEARING` (INTERNAL): conta de compensação para payouts (recomendado)

A criação dessas contas segue a regra da Wallet:
- `POST /accounts` com `ownerType=INTERNAL` e `ownerId` fixos

---

## 5. Requisitos Funcionais

### RF-PAY-01 — Criar cobrança Pix (Cash-in)
Endpoint:
- `POST /payments/pix/charges`

Headers:
- `X-API-Key`
- `Idempotency-Key` (opcional; se não vier, usar `reference_id` como base)

Request:
```json
{
  "referenceType": "PLATFORM_TRANSACTION",
  "referenceId": "txn-123",
  "amountMinor": 12000,
  "currency": "BRL",
  "payer": {
    "name": "João",
    "document": "12345678900"
  },
  "creditToWalletAccountId": "wallet-account-uuid"
}
```

Comportamento:
1. Resolver `tenantId`.
2. Persistir `Payment` com `type=PIX_CASHIN`, `status=PENDING`.
3. Chamar PSP para criar cobrança Pix e obter QR/copia-e-cola + `externalPaymentId` + `txid`.
4. Atualizar Payment com dados externos.
5. Retornar payload para pagamento.

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

---

### RF-PAY-02 — Confirmar cobrança via Webhook (Cash-in)
Endpoint:
- `POST /payments/webhooks/psp`

Regras:
- Validar assinatura do webhook (ver seção 7).
- Resolver `tenantId` por configuração do PSP (não via API key).
- Idempotência: processar evento uma vez por `externalPaymentId + eventType`.

Ao receber confirmação:
1. Carregar `Payment` pelo `externalPaymentId`.
2. Se já `CONFIRMED`, retornar 200.
3. Marcar `CONFIRMED`.
4. Executar crédito na Wallet usando transferência interna:
   - `CASH_AT_PSP` → `creditToWalletAccountId`
   - usar `idempotencyKey` derivada do payment (ex: `pay_<paymentId>_confirm`)
5. Registrar `ledger_transaction_id` se disponível.

---

### RF-PAY-03 — Solicitar payout Pix (Cash-out)
Endpoint:
- `POST /payments/pix/payouts`

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

Comportamento (recomendado com clearing):
1. Resolver `tenantId`.
2. Validar saldo suficiente no `debitFromWalletAccountId`.
3. Criar `Payment` `PIX_PAYOUT` com `PENDING`.
4. Movimentar saldo para clearing (reserva):
   - `debitFromWalletAccountId` → `OUTBOUND_CLEARING`
5. Chamar PSP para enviar Pix.
6. Salvar `externalPaymentId`.

Response:
```json
{
  "paymentId": "uuid",
  "status": "PENDING",
  "externalPaymentId": "..."
}
```

---

### RF-PAY-04 — Confirmar payout via Webhook
Ao receber evento do PSP:

**CONFIRMED**
1. Marcar payment `CONFIRMED`.
2. Baixar clearing:
   - `OUTBOUND_CLEARING` → `CASH_AT_PSP`

**FAILED/CANCELED**
1. Marcar payment `FAILED` ou `CANCELED`.
2. Estornar a reserva:
   - `OUTBOUND_CLEARING` → `debitFromWalletAccountId`

Tudo com idempotência.

---

### RF-PAY-05 — Consultar payment
Endpoints:
- `GET /payments/{paymentId}`
- `GET /payments/by-reference?referenceType=...&referenceId=...`

Resposta deve incluir status e informações externas principais.

---

## 6. Requisitos Não Funcionais

- **Imutabilidade**: Payments nunca altera o Ledger diretamente; sempre via Wallet transfers.
- **Atomicidade**:
  - Atualização de `Payment.status` + criação de transfer devem ser consistentes.
  - Preferir transação DB local + idempotência no Wallet.
- **Idempotência**:
  - `payments(tenant_id, idempotency_key)`
  - webhook dedup por `externalPaymentId` e `eventType`
- **Observabilidade**:
  - `traceId` em logs, responses e propagação para chamadas ao PSP.
- **Erros padronizados**:
  - seguir ADR de Problem Details (400/401/404/409/500).

---

## 7. Segurança de Webhooks

Implementar validação de autenticidade do webhook:

Opções:
1) **HMAC signature** (recomendado):
- PSP envia header `X-Signature`.
- Seu sistema calcula HMAC SHA-256 do body com secret do tenant/psp.
- Compara de forma segura (timing-safe).

2) **mTLS** (se disponível) — fora do MVP.

Regras:
- Rejeitar webhooks sem assinatura válida (401/403).
- Logar `traceId`, `externalPaymentId`, `eventType`.

---

## 8. Integração com PSP (Adapter)

Criar interface:

- `PixProviderClient`
  - `createCharge(CreateChargeParams) -> ChargeCreated`
  - `createPayout(CreatePayoutParams) -> PayoutCreated`
  - (opcional) `getChargeStatus(txid)`

Implementar um adapter real (PSP escolhido) e um stub/fake para testes.

Configuração:
- `payments.provider = ASAAS | PAGARME | ...`
- credenciais por tenant (futuro). No MVP pode ser global.

---

## 9. APIs (Contrato)

### 9.1 POST /payments/pix/charges
- cria cobrança e retorna QR

### 9.2 POST /payments/pix/payouts
- solicita payout e reserva saldo

### 9.3 POST /payments/webhooks/psp
- recebe eventos e consolida no Wallet

### 9.4 GET /payments/{id}
- consulta status

---

## 10. Testes (mínimos)

### Integração
1. Criar charge → retorna PENDING + QR
2. Webhook CONFIRMED → saldo da Wallet aumenta
3. Criar payout → reserva em OUTBOUND_CLEARING
4. Webhook FAILED → saldo volta para Wallet
5. Webhook CONFIRMED → baixa clearing e mantém débito

### Contrato de erro
- Enum/currency inválidos → 400 com violations
- Saldo insuficiente no payout → 409 ou 422 (decidir) com errorCode
- Webhook assinatura inválida → 401/403

---

## 11. Backlog de Implementação (ordem sugerida)

1) Modelos/tabelas `payments`
2) Interface `PixProviderClient` + Fake
3) Endpoints charges/payouts (PENDING)
4) Contas internas `CASH_AT_PSP` e `OUTBOUND_CLEARING` (criar sob demanda via Wallet)
5) Webhook + assinatura + idempotência
6) Integração com Wallet transfers
7) Testes E2E

---

**Fim da Especificação — Payments Module (Pix) sobre Wallet + Ledger**

