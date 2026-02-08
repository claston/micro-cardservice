# API Rule - Criacao de APIs

Este rule define regras de criacao de APIs no projeto.

## Escopo
- Aplica-se a endpoints dos modulos ledger, wallet, customer, payments e futuros produtos sobre o ledger.

## Regras obrigatorias
- Tenant: toda operacao exige tenantId; nunca cruzar tenants.
- Ledger e entries sao append-only; sem UPDATE/DELETE.
- Double-entry: CREDIT e DEBIT fecham por moeda.
- Idempotencia: idempotencyKey unica por tenant.
- Saldo: nunca atualizar saldo direto; sempre via LedgerTransaction.
- allowNegative=false bloqueia saldo negativo.
- Money: usar amountMinor (long) e moeda ISO 4217 (3 letras).
- Webhooks: sempre resolver tenantId e deduplicar eventos por tenant.

## Design de API
- Header obrigatorio: X-API-Key (resolve tenantId).
- X-Request-Id opcional; virar traceId em respostas de erro.
- Erros seguem Problem Details + extensoes:
  - type, title, status, detail, instance, errorCode, traceId
  - violations[] somente para validacao.
- HTTP status padrao:
  - 400 validacao
  - 401 api key invalida
  - 404 recurso nao encontrado (no tenant)
  - 409 conflito de idempotencia ou regra de negocio
- Recursos devem ser finos; regras de negocio nos use cases.
- APIs internas ainda devem exigir tenantId explicito (sem inferencia).

## Camadas e pacotes
- API: resources + DTOs (validacao declarativa).
- Application: use cases e orquestracao transacional.
- Domain: entidades, VOs e validacoes.
- Infra: persistencia, mappers, migrations.

## Contratos de erros (minimo)
- Validation: *_VALIDATION_ERROR.
- Unauthorized: *_UNAUTHORIZED.
- Not found: *_NOT_FOUND.
- Conflict: *_ALREADY_EXISTS, *_IDEMPOTENCY_CONFLICT, ou regra de negocio.
- Fallback 500: *_INTERNAL_ERROR.
- Sempre incluir traceId e instance.

## Padrao de validacao
- Bean Validation nos DTOs.
- Evitar valueOf direto; validar enums com mensagens amigaveis.
- Normalizar campos quando o contrato pedir (ex.: documento so digitos).

## Regras para Ledger/Wallet
- WalletAccount: 1 principal por (tenantId, ownerType, ownerId, currency).
- Transferencia: duas entries (DEBIT origem, CREDIT destino).
- Saldo/extrato sempre consultam Ledger (nao armazenar saldo).

## Persistencia e constraints
- Sempre incluir tenant_id nos filtros e indices.
- Constraints criticas:
  - ledger_transactions: UNIQUE(tenant_id, idempotency_key)
  - entries: tenant_id igual ao da transaction
  - accounts: UNIQUE(tenant_id, owner_type, owner_id, currency, account_type)
- PostgreSQL alvo; testes em H2 MODE=PostgreSQL.
- Triggers para bloquear UPDATE/DELETE em ledger_transactions e entries.

## Observabilidade
- Logs devem incluir tenantId e idempotencyKey quando existir.
- traceId sempre presente em respostas de erro.

## Checklist rapido para novo endpoint
- [ ] Resolve tenantId via X-API-Key
- [ ] DTO com Bean Validation
- [ ] Use case recebe tenantId explicitamente
- [ ] Erros Problem Details + traceId
- [ ] Teste multi-tenant e regras de negocio
- [ ] Sem UPDATE/DELETE em tabelas de ledger
