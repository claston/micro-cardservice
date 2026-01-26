# ADR — Erros Padronizados e Validação Declarativa na Wallet API

- **Status:** Accepted
- **Data:** 2026-01-25
- **Contexto:** Wallet API (Fase 1) sobre Ledger Core

---

## Contexto

Durante testes do MVP da Wallet, ocorreram respostas **400 genéricas** (ex.: “Solicitação Inválida”) quando o payload possuía valores inválidos (ex.: `ownerType` diferente de `CUSTOMER`). Isso dificulta o uso por clientes externos, aumenta tempo de integração e gera suporte desnecessário.

O comportamento atual usa `try/catch` de `IllegalArgumentException` no resource e retorna `WebApplicationException` sem um contrato de erro consistente.

A especificação do MVP introduziu o requisito **RF-06 — Modelo de Erro (Developer-friendly)**, recomendando um formato estruturado baseado em **Problem Details (RFC 7807)** com `errorCode`, `violations[]` e `traceId`.

---

## Decisão

Adotar na Wallet API:

1) **Validação declarativa (Bean Validation)** nos DTOs de request (ex.: `CreateWalletAccountRequest`, `CreateTransferRequest`).

2) **Erros padronizados** no formato **Problem Details** (RFC 7807 ou equivalente), contendo:
- `type`, `title`, `status`, `detail`, `instance`
- `errorCode`
- `violations[]` (quando aplicável)
- `traceId`

3) **Exception Mappers (JAX-RS)** para transformar exceções em respostas consistentes:
- validação de payload → 400 com `violations`
- conflitos de negócio (ex.: conta já existe) → 409
- não encontrado → 404
- não autorizado (API key inválida) → 401

4) **Correlações de requisição**: gerar/propagar `traceId` e incluir em logs + resposta.

---

## Motivações

- Melhorar DX (Developer Experience) e reduzir fricção de integração
- Tornar o MVP vendável (API autoexplicativa)
- Evitar “debug lendo código” em situações comuns
- Padronizar o tratamento de erros desde o início

---

## Alternativas consideradas

### A) Manter `try/catch` no resource com mensagens simples
- **Prós:** rápido
- **Contras:** inconsistência, difícil padronizar, não escala

### B) Retornar apenas mensagens de texto
- **Prós:** simples
- **Contras:** não suporta erros por campo e automação do cliente

### C) Expor o ledger para debug
- **Prós:** facilita testes internos
- **Contras:** quebra encapsulamento e segurança do produto

---

## Consequências

### Positivas
- Contrato claro para clientes externos
- Redução de suporte e tempo de integração
- Melhor observabilidade com `traceId`

### Negativas / Custos
- Implementar DTO validations + exception mappers
- Manter catálogo de `errorCode`

---

## Especificação de Implementação (para Codex)

### Objetivo
Implementar validação declarativa e erros padronizados no **módulo Wallet**, atendendo o requisito **RF-06** do documento “Especificação — Fase 1: Wallet API sobre Ledger (MVP Vendável)”.

### Escopo
Aplicar em endpoints:
- `POST /accounts`
- `GET /accounts/{id}/balance`
- `GET /accounts/{id}/statement`
- `POST /transfers`

### Regras de contrato de erro
Todas as respostas de erro DEVEM retornar JSON no formato abaixo (Problem Details + extensões):

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

- `violations` é obrigatório apenas para erros de validação.
- `traceId` deve sempre existir.

### Mapeamento de erros

#### 400 — Validation error
- Quando Bean Validation falhar (ex.: `ConstraintViolationException`, `ResteasyReactiveViolationException`)
- `errorCode = WALLET_VALIDATION_ERROR`
- Preencher `violations[]`

Casos mínimos:
- `ownerType` inválido → mensagem deve listar valores permitidos (`CUSTOMER`, `INTERNAL`)
- `ownerId` vazio
- `currency` inválida

#### 401 — Unauthorized
- API key ausente/inválida
- `errorCode = WALLET_UNAUTHORIZED`

#### 404 — Not Found
- WalletAccount não encontrada (no tenant)
- `errorCode = WALLET_ACCOUNT_NOT_FOUND`

#### 409 — Conflict
- Conta já existe (unicidade)
- `errorCode = WALLET_ACCOUNT_ALREADY_EXISTS`

#### 500 — Unexpected
- Qualquer exceção não mapeada
- `errorCode = WALLET_INTERNAL_ERROR`

### DTOs: adicionar Bean Validation

#### CreateWalletAccountRequest
- `ownerType`: `@NotBlank` + validação de enum (CUSTOMER/INTERNAL)
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
- Implementar um `@ValidEnum` custom annotation OU um `ParamConverter`/`JsonbAdapter` para mapear string → enum com erro amigável.
- Se `ownerType` for inválido, retornar 400 com `violations` e valores permitidos.

### Exception Mappers
Implementar `@Provider` mappers:

1) `ValidationExceptionMapper`
- Converte violations em lista ordenada por campo

2) `WebApplicationExceptionMapper` (ou específico)
- Padroniza respostas para 401/404/409 lançadas pela aplicação

3) `ThrowableMapper`
- Fallback 500 com `traceId`

### TraceId / Correlation
- Se request tiver header `X-Request-Id`, reutilizar
- Senão gerar UUID
- Incluir `traceId` na resposta e log
- Ideal: `ContainerRequestFilter` + `ContainerResponseFilter`

### Refatoração do resource
- Remover `try/catch IllegalArgumentException` do resource
- Deixar o resource “fino”, delegando validação ao Bean Validation e regras ao use case

### Testes de integração (mínimos)
Criar testes que validem payload e formato de erro:
- `POST /accounts` com `ownerType=FUNDER` → 400 + violations contendo ownerType
- `POST /accounts` sem `ownerId` → 400 + violations
- `POST /accounts` duplicado → 409 + errorCode
- API key ausente → 401 + errorCode

### Definition of Done
- Todas as rotas retornam erros no mesmo formato
- `traceId` presente sempre
- Sem mensagens genéricas sem detalhe
- Testes cobrindo os principais cenários

---

## Notas

- O domínio do Ledger não deve ser exposto em erros (sem IDs/entidades internas além do necessário).
- Preferir mensagens úteis, mas não vazar detalhes sensíveis (ex.: stacktrace).

