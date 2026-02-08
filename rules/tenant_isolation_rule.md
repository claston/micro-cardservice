# Tenant Isolation Rule

## Escopo
- Aplica-se a todos os modulos e APIs (ledger, wallet, customer, payments, creditcard).

## Regras obrigatorias
- Toda entidade contabil ou financeira deve ter tenantId.
- Toda operacao deve ser escopada por tenantId em API, aplicacao e banco.
- Operacoes cross-tenant sao proibidas.

## API
- APIs publicas resolvem tenant via X-API-Key.
- APIs internas exigem tenantId explicito.
- Nunca inferir tenant pelo banco.

## Persistencia
- Toda query filtra por tenant_id.
- Unicidade e idempotencia sao por tenant.
- Indices com tenant_id como primeira coluna.

## Webhooks
- Webhooks resolvem tenantId via configuracao do PSP.
- Nunca buscar por external id sem tenant.

## Testes
- Dados iguais em tenants diferentes devem coexistir.
- Cross-tenant deve falhar.
