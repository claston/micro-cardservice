# ADR â€” Erros Padronizados e ValidaÃ§Ã£o Declarativa na Wallet API

- **Status:** Accepted
- **Data:** 2026-01-25
- **Contexto:** Wallet API (Fase 1) sobre Ledger Core

---

## Contexto

Durante testes do MVP da Wallet, ocorreram respostas **400 genÃ©ricas** (ex.: â€œSolicitaÃ§Ã£o InvÃ¡lidaâ€) quando o payload possuÃ­a valores invÃ¡lidos (ex.: `ownerType` diferente de `CUSTOMER`). Isso dificulta o uso por clientes externos, aumenta tempo de integraÃ§Ã£o e gera suporte desnecessÃ¡rio.

O comportamento atual usa `try/catch` de `IllegalArgumentException` no resource e retorna `WebApplicationException` sem um contrato de erro consistente.

A especificaÃ§Ã£o do MVP introduziu o requisito **RF-06 â€” Modelo de Erro (Developer-friendly)**, recomendando um formato estruturado baseado em **Problem Details (RFC 7807)** com `errorCode`, `violations[]` e `traceId`.

---

## DecisÃ£o

Adotar na Wallet API:

1) **ValidaÃ§Ã£o declarativa (Bean Validation)** nos DTOs de request (ex.: `CreateWalletAccountRequest`, `CreateTransferRequest`).

2) **Erros padronizados** no formato **Problem Details** (RFC 7807 ou equivalente), contendo:
- `type`, `title`, `status`, `detail`, `instance`
- `errorCode`
- `violations[]` (quando aplicÃ¡vel)
- `traceId`

3) **Exception Mappers (JAX-RS)** para transformar exceÃ§Ãµes em respostas consistentes:
- validaÃ§Ã£o de payload â†’ 400 com `violations`
- conflitos de negÃ³cio (ex.: conta jÃ¡ existe) â†’ 409
- nÃ£o encontrado â†’ 404
- nÃ£o autorizado (API key invÃ¡lida) â†’ 401

4) **CorrelaÃ§Ãµes de requisiÃ§Ã£o**: gerar/propagar `traceId` e incluir em logs + resposta.

---

## MotivaÃ§Ãµes

- Melhorar DX (Developer Experience) e reduzir fricÃ§Ã£o de integraÃ§Ã£o
- Tornar o MVP vendÃ¡vel (API autoexplicativa)
- Evitar â€œdebug lendo cÃ³digoâ€ em situaÃ§Ãµes comuns
- Padronizar o tratamento de erros desde o inÃ­cio

---

## Alternativas consideradas

### A) Manter `try/catch` no resource com mensagens simples
- **PrÃ³s:** rÃ¡pido
- **Contras:** inconsistÃªncia, difÃ­cil padronizar, nÃ£o escala

### B) Retornar apenas mensagens de texto
- **PrÃ³s:** simples
- **Contras:** nÃ£o suporta erros por campo e automaÃ§Ã£o do cliente

### C) Expor o ledger para debug
- **PrÃ³s:** facilita testes internos
- **Contras:** quebra encapsulamento e seguranÃ§a do produto

---

## ConsequÃªncias

### Positivas
- Contrato claro para clientes externos
- ReduÃ§Ã£o de suporte e tempo de integraÃ§Ã£o
- Melhor observabilidade com `traceId`

### Negativas / Custos
- Implementar DTO validations + exception mappers
- Manter catÃ¡logo de `errorCode`

---

## EspecificaÃ§Ã£o de ImplementaÃ§Ã£o (para Codex)

### Objetivo
Implementar validaÃ§Ã£o declarativa e erros padronizados no **mÃ³dulo Wallet**, atendendo o requisito **RF-06** do documento â€œEspecificaÃ§Ã£o â€” Fase 1: Wallet API sobre Ledger (MVP VendÃ¡vel)â€.

### Escopo
Aplicar em endpoints:
- `POST /accounts`
- `GET /accounts/{id}/balance`
- `GET /accounts/{id}/statement`
- `POST /transfers`

### Regras de contrato de erro
Todas as respostas de erro DEVEM retornar JSON no formato abaixo (Problem Details + extensÃµes):

```json
{
  "type": "https://errors.yourdomain.com/<category>",
  "title": "<short title>",
  "status": 400,
  "detail": "<human readable>",
  "instance": "/<path>",
  "errorCode": "<STABLE_CODE>",
  "violations": [
    {"field": "<field>", "message": "<message>", "rejectedValue": "<value>"}
  ],
  "traceId": "<request-id>"
}
```

- `violations` Ã© obrigatÃ³rio apenas para erros de validaÃ§Ã£o.
- `traceId` deve sempre existir.

### Mapeamento de erros

#### 400 â€” Validation error
- Quando Bean Validation falhar (ex.: `ConstraintViolationException`, `ResteasyReactiveViolationException`)
- `errorCode = WALLET_VALIDATION_ERROR`
- Preencher `violations[]`

Casos mÃ­nimos:
- `ownerType` invÃ¡lido â†’ mensagem deve listar valores permitidos (`CUSTOMER`, `INTERNAL`)
- `ownerId` vazio
- `currency` invÃ¡lida

#### 401 â€” Unauthorized
- API key ausente/invÃ¡lida
- `errorCode = WALLET_UNAUTHORIZED`

#### 404 â€” Not Found
- WalletAccount nÃ£o encontrada (no tenant)
- `errorCode = WALLET_ACCOUNT_NOT_FOUND`

#### 409 â€” Conflict
- Conta jÃ¡ existe (unicidade)
- `errorCode = WALLET_ACCOUNT_ALREADY_EXISTS`
- IdempotÃªncia: `idempotencyKey` jÃ¡ usada no mesmo tenant
- `errorCode = WALLET_IDEMPOTENCY_CONFLICT`
- Retornar `meta.transactionId` e `meta.idempotencyKey`

#### 500 â€” Unexpected
- Qualquer exceÃ§Ã£o nÃ£o mapeada
- `errorCode = WALLET_INTERNAL_ERROR`

### DTOs: adicionar Bean Validation

#### CreateWalletAccountRequest
- `ownerType`: `@NotBlank` + validaÃ§Ã£o de enum (CUSTOMER/INTERNAL)
- `ownerId`: `@NotBlank`
- `currency`: `@NotBlank` + `@Pattern(regexp = "^[A-Z]{3}$")`
- `label`: opcional

#### CreateTransferRequest
- `idempotencyKey`: `@NotBlank`
- `fromAccountId`: `@NotBlank` + UUID format validation
- `toAccountId`: `@NotBlank` + UUID format validation
- `amountMinor`: `@Positive`
- `currency`: `@NotBlank` + `@Pattern(regexp = "^[A-Z]{3}$")`

### Enum parsing (evitar valueOf direto)
- Implementar um `@ValidEnum` custom annotation OU um `ParamConverter`/`JsonbAdapter` para mapear string â†’ enum com erro amigÃ¡vel.
- Se `ownerType` for invÃ¡lido, retornar 400 com `violations` e valores permitidos.

### Exception Mappers
Implementar `@Provider` mappers:

1) `ValidationExceptionMapper`
- Converte violations em lista ordenada por campo

2) `WebApplicationExceptionMapper` (ou especÃ­fico)
- Padroniza respostas para 401/404/409 lanÃ§adas pela aplicaÃ§Ã£o

3) `ThrowableMapper`
- Fallback 500 com `traceId`

### TraceId / Correlation
- Se request tiver header `X-Request-Id`, reutilizar
- SenÃ£o gerar UUID
- Incluir `traceId` na resposta e log
- Ideal: `ContainerRequestFilter` + `ContainerResponseFilter`

### RefatoraÃ§Ã£o do resource
- Remover `try/catch IllegalArgumentException` do resource
- Deixar o resource â€œfinoâ€, delegando validaÃ§Ã£o ao Bean Validation e regras ao use case

### Testes de integraÃ§Ã£o (mÃ­nimos)
Criar testes que validem payload e formato de erro:
- `POST /accounts` com `ownerType=FUNDER` â†’ 400 + violations contendo ownerType
- `POST /accounts` sem `ownerId` â†’ 400 + violations
- `POST /accounts` duplicado â†’ 409 + errorCode
- `POST /transfers` com `idempotencyKey` repetida â†’ 409 + errorCode + meta
- API key ausente â†’ 401 + errorCode

### Definition of Done
- Todas as rotas retornam erros no mesmo formato
- `traceId` presente sempre
- Sem mensagens genÃ©ricas sem detalhe
- Testes cobrindo os principais cenÃ¡rios

---

## Notas

- O domÃ­nio do Ledger nÃ£o deve ser exposto em erros (sem IDs/entidades internas alÃ©m do necessÃ¡rio).
- Preferir mensagens Ãºteis, mas nÃ£o vazar detalhes sensÃ­veis (ex.: stacktrace).



