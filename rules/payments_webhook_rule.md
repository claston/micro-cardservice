# Payments Webhook Rule

## Escopo
- Endpoint de webhook PSP e processamento interno.

## Seguranca
- Validar assinatura X-Signature.
- Rejeitar assinatura invalida com 401/403.

## Tenant
- Resolver tenantId por configuracao do PSP.
- Buscar e atualizar pagamentos sempre com tenantId.

## Idempotencia
- Deduplicar por (tenant_id, external_payment_id, event_type).

## Side effects
- Criar transferencias somente apos confirmacao.
- Idempotency keys deterministicas: pay_<paymentId>_<acao>.

## Logging
- Logar traceId, externalPaymentId e eventType.
