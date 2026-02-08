# API Headers Rule

## Public APIs
- X-API-Key e obrigatorio para resolver tenantId.
- X-Request-Id e opcional e deve virar traceId em erros.

## Internal APIs
- TenantId deve ser informado explicitamente.
- Nunca inferir tenant do banco.

## Idempotencia
- Quando suportado, aceitar Idempotency-Key e registrar por tenant.
