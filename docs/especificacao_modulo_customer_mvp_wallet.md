# Especificação — Módulo Customer (MVP para Wallet)

> Objetivo: definir um **módulo Customer** alinhado ao padrão do projeto (API/Application/Domain/Infra) e pronto para **multi-tenancy**, atendendo o MVP de Wallet com suporte a **Pessoa Física (PF)** e **Pessoa Jurídica (PJ)**.

Este documento complementa `docs/customer_refactor_phases.md` com uma especificação de domínio, dados e APIs.

---

## 1. Conceitos

- **Tenant**: cliente B2B do sistema (organização/plataforma).
- **Customer**: cliente final dentro de um tenant (B2C/B2B2C).

Regra central:
> O mesmo documento pode existir em tenants diferentes, mas é **único dentro do mesmo tenant**.

---

## 2. Escopo do MVP

### Em escopo
- Criar Customer PF/PJ com **nome** e **documento**.
- Consultar Customer por `id`.
- Listar Customers (paginado) e filtrar por documento (opcional).
- Multi-tenancy com isolamento por `tenantId`.
- Padronização de erro (Problem Details + `errorCode` + `traceId`) seguindo `docs/adr_erros_padronizados_e_validacao_declarativa_na_wallet_api.md`.
- Validação declarativa (Bean Validation) nos DTOs.

### Fora de escopo (por enquanto)
- Endereços detalhados, e-mails/telefones obrigatórios.
- Histórico completo de alterações/auditoria avançada.
- Integração com provedores KYC (apenas status básico, se habilitado).

---

## 3. Modelo de Domínio

### 3.1 Customer

Campos (MVP e extensões seguras):

- `id` (UUID)
- `tenantId` (UUID) — obrigatório
- `type` — `INDIVIDUAL` (PF) | `BUSINESS` (PJ) — obrigatório
- `name` — obrigatório
  - PF: nome completo
  - PJ: razão social (nome legal)
- `documentType` — `CPF` | `CNPJ` — obrigatório
- `documentNumber` — obrigatório (string; armazenar normalizado)
- `status` — `ACTIVE` | `INACTIVE` — default `ACTIVE`
- `createdAt` (timestamp)
- `updatedAt` (timestamp)

Opcionais (não obrigatórios no MVP, mas já previstos):
- `tradeName` (PJ) — nome fantasia
- `externalReference` — id do customer no sistema do tenant (útil para integrações)
- `metadata` (JSON) — chaves livres do tenant (sem semântica para o domínio)

### 3.2 Regras de Negócio (Customer)

- **Isolamento**: toda operação deve receber/derivar `tenantId` e filtrar por `tenantId`.
- **Unicidade**: `(tenantId, documentType, documentNumber)` deve ser único.
- **Consistência PF/PJ**:
  - `type=INDIVIDUAL` → `documentType` deve ser `CPF`
  - `type=BUSINESS` → `documentType` deve ser `CNPJ`
- **Normalização de documento**:
  - Armazenar `documentNumber` apenas com dígitos (ex.: `"12345678901"`).
  - Validar tamanho conforme tipo (CPF=11, CNPJ=14). (Validação do dígito verificador pode entrar depois.)

---

## 4. KYC: no Customer ou módulo separado?

### Decisão para MVP (recomendado)
Manter KYC **como estado mínimo dentro do Customer**, sem armazenar artefatos/documentos.

Campos sugeridos (opcionais no MVP):
- `kycStatus`: `NOT_STARTED` | `PENDING` | `VERIFIED` | `REJECTED`
- `kycUpdatedAt`
- `kycProvider` (string)
- `kycReference` (string) — id/caso no provedor

### Evolução (fase 2+)
Criar módulo `kyc` próprio quando houver:
- múltiplas etapas por customer, reprocessamentos, webhooks,
- histórico de decisões (audit trail),
- anexos/documentos e evidências.

---

## 5. Persistência (PostgreSQL / H2 modo PostgreSQL)

### 5.1 Tabela `customers`

Campos mínimos:
- `id` UUID PK
- `tenant_id` UUID NOT NULL
- `type` VARCHAR NOT NULL
- `name` VARCHAR NOT NULL
- `document_type` VARCHAR NOT NULL
- `document_number` VARCHAR NOT NULL
- `status` VARCHAR NOT NULL
- `created_at` TIMESTAMP NOT NULL DEFAULT now()
- `updated_at` TIMESTAMP NOT NULL DEFAULT now()

Campos opcionais:
- `trade_name` VARCHAR NULL
- `external_reference` VARCHAR NULL
- `metadata` JSONB NULL (em H2 pode ser VARCHAR/TEXT)
- `kyc_status` VARCHAR NULL
- `kyc_updated_at` TIMESTAMP NULL
- `kyc_provider` VARCHAR NULL
- `kyc_reference` VARCHAR NULL

Constraints:
- `UNIQUE (tenant_id, document_type, document_number)`

Índices sugeridos:
- `customers(tenant_id, document_type, document_number)`
- `customers(tenant_id, created_at)`

Observações:
- Diferente do ledger, **Customer pode ser mutável** (UPDATE permitido), mas ainda deve manter `updated_at`.
- Se adotarem "soft delete", preferir `status=INACTIVE` no MVP; `deleted_at` pode ser adicionado depois se necessário.

---

## 6. APIs (Customer)

### 6.1 Autenticação / tenant

Para manter consistência com Wallet:
- Requests devem conter `X-API-Key`.
- A API Key resolve `tenantId`, que é propagado para os use cases.
- Se ausente/inválida: 401.

> Alternativa interna (opcional): suportar `X-Tenant-Id` apenas em rotas internas/admin. Fora do MVP.

### 6.2 Endpoints

> Exemplos completos de uso (curl) em `docs/api_customer_mvp_wallet.md`.

#### POST `/customers`
Cria um Customer (PF/PJ).

Request (MVP):
```json
{
  "type": "INDIVIDUAL",
  "name": "Maria da Silva",
  "documentType": "CPF",
  "documentNumber": "123.456.789-01"
}
```

Response (201):
```json
{
  "id": "uuid",
  "type": "INDIVIDUAL",
  "name": "Maria da Silva",
  "documentType": "CPF",
  "documentNumber": "12345678901",
  "status": "ACTIVE",
  "createdAt": "2026-01-28T12:00:00Z"
}
```

Regras:
- Normalizar `documentNumber` (remover não dígitos) antes de persistir.
- Se já existir customer com mesmo `(tenantId, documentType, documentNumber)`: 409.

#### GET `/customers/{customerId}`
Busca um customer (no tenant).

Response (200):
```json
{
  "id": "uuid",
  "type": "BUSINESS",
  "name": "ACME LTDA",
  "documentType": "CNPJ",
  "documentNumber": "12345678000199",
  "status": "ACTIVE"
}
```

Se não encontrado no tenant: 404.

#### GET `/customers?documentType=...&documentNumber=...`
Busca customer por documento (no tenant), para evitar duplicidade de cadastro.

Query params (MVP):
- `documentType` (CPF|CNPJ)
- `documentNumber` (qualquer formato; a API normaliza para dígitos antes de consultar)

Response (200):
```json
{
  "items": [ { "...": "..." } ],
  "page": 0,
  "size": 20,
  "total": 1
}
```

Decisão do MVP:
- Não expor listagem geral de customers sem filtros (evita vazamento de dados e simplifica).
- Para `items` vazio, retornar `200` com `items=[]` (não usar `204`).

---

## 7. Erros (Problem Details)

Formato base (alinhado ao ADR):
- `type`, `title`, `status`, `detail`, `instance`
- `errorCode`
- `violations[]` (somente validação)
- `traceId` (sempre presente; reutiliza `X-Request-Id` se enviado)

Mapeamento mínimo:
- 400: `CUSTOMER_VALIDATION_ERROR`
- 401: `CUSTOMER_UNAUTHORIZED`
- 404: `CUSTOMER_NOT_FOUND`
- 409: `CUSTOMER_ALREADY_EXISTS`
- 500: `CUSTOMER_INTERNAL_ERROR`

Violações mínimas:
- `type` inválido (INDIVIDUAL|BUSINESS)
- `documentType` inválido (CPF|CNPJ)
- inconsistência `type` vs `documentType`
- `documentNumber` vazio ou tamanho inválido após normalização
- `name` vazio

---

## 8. Integração com Wallet

Wallet pode referenciar customer por:
- `ownerType = CUSTOMER`
- `ownerId = <customerId>` (UUID do Customer no tenant)

Isso permite:
- validar existência do customer ao criar WalletAccount (opcional no MVP)
- evoluir para limites/risco/kyc por customer sem mudar o ledger

---

## 9. Testes (mínimos)

### Integração / multi-tenant
- criar o mesmo documento em tenants diferentes → permitido
- tentar buscar customer de outro tenant → 404 (ou 403, se preferirem distinguir)

### Validação / contrato de erro
- `documentNumber` vazio → 400 + `violations[]` + `traceId`
- `type=INDIVIDUAL` + `documentType=CNPJ` → 400 + `violations[]`
- documento duplicado no mesmo tenant → 409 + `errorCode=CUSTOMER_ALREADY_EXISTS`

---

## 10. Definition of Done

- Customer sempre tem `tenantId`.
- Unicidade por documento funciona por tenant.
- Endpoints do MVP implementados com DTO validation e erros padronizados.
- `traceId` sempre presente nas respostas de erro.

---

## 11. Componentes comuns (centralização)

Decisão:
- `traceId` e o contrato de erro (Problem Details + extensões) devem ser **centralizados** em um pacote comum, para reutilização por `wallet`, `customer` e futuros módulos.

Escopo do comum:
- `RequestTraceFilter` (gera/propaga `X-Request-Id`)
- `TraceIdProvider`
- `ErrorResponse`, `ErrorViolation`, `ErrorResponseFactory`
- Exception mappers base: validação, `IllegalArgumentException`, fallback `Throwable`

Cada módulo mantém seus próprios:
- `ErrorCodes` (estáveis por módulo)
- `ErrorTypes` (se forem diferentes; se forem comuns, também podem ser centralizados)
- exceções tipadas de negócio na camada Application
