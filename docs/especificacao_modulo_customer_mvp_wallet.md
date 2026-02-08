# Especificacao - Modulo Customer (MVP para Wallet)

> Objetivo: definir um modulo Customer alinhado ao padrao do projeto (API/Application/Domain/Infra) e pronto para multi-tenancy, atendendo o MVP de Wallet com suporte a PF e PJ.

---

## 1. Conceitos

- Tenant: cliente B2B do sistema.
- Customer: cliente final dentro de um tenant.

---

## 2. Escopo do MVP

### Em escopo
- Criar Customer PF/PJ com nome e documento.
- Consultar Customer por id.
- Buscar Customer por documento.

### Fora de escopo (por enquanto)
- Enderecos detalhados, e-mails/telefones obrigatorios.
- Historico completo de alteracoes/auditoria avancada.
- Integracao com provedores KYC.

---

## 3. Modelo de Dominio

Campos principais:
- id (UUID)
- tenantId (UUID)
- type (INDIVIDUAL | BUSINESS)
- name
- documentType (CPF | CNPJ)
- documentNumber
- status (ACTIVE | INACTIVE)
- createdAt, updatedAt

Opcionais:
- tradeName
- externalReference
- metadata
- kycStatus, kycUpdatedAt, kycProvider, kycReference

---

## 4. Persistencia (PostgreSQL / H2 modo PostgreSQL)

Tabela `customers` (campos minimos):
- id UUID PK
- tenant_id UUID NOT NULL
- type VARCHAR NOT NULL
- name VARCHAR NOT NULL
- document_type VARCHAR NOT NULL
- document_number VARCHAR NOT NULL
- status VARCHAR NOT NULL
- created_at TIMESTAMP NOT NULL
- updated_at TIMESTAMP NOT NULL

Campos opcionais:
- trade_name, external_reference, metadata, kyc_status, kyc_updated_at, kyc_provider, kyc_reference

---

## 5. APIs (Customer)

> Exemplos completos de uso (curl) em `docs/api_customer_mvp_wallet.md`.

### POST /customers
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

### GET /customers/{customerId}
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

### GET /customers?documentType=...&documentNumber=...
Response (200):
```json
{
  "items": [ { "...": "..." } ],
  "page": 0,
  "size": 20,
  "total": 1
}
```

---

## 6. Integracao com Wallet

Wallet pode referenciar customer por:
- ownerType = CUSTOMER
- ownerId = <customerId>

---

**Fim da Especificacao do Modulo Customer**
