# API — Customer (MVP para Wallet)

> Ambiente dev: `http://localhost:8080`
>
> Escopo: endpoints mínimos para criar/consultar Customer e suportar Wallet (`ownerType=CUSTOMER`, `ownerId=<customerId>`).

---

## Headers

- `X-API-Key: <apiKey>` (obrigatório) — resolve `tenantId`
- `Content-Type: application/json` (quando houver body)
- `X-Request-Id: <id>` (opcional) — correlação; volta em respostas de erro como `traceId`

Decisão do MVP:
- Todas as rotas exigem `X-API-Key` (mesmo padrão da Wallet).
- Em dev/test o exemplo usa `key-dev` (config `app.api-keys=key-dev=00000000-0000-0000-0000-000000000000`).

---

## Modelo (MVP)

- `type`: `INDIVIDUAL` | `BUSINESS`
- `documentType`: `CPF` | `CNPJ`
- `documentNumber`: string (a API normaliza e retorna somente dígitos)
- `status`: `ACTIVE` | `INACTIVE`

---

## 1) Criar Customer

### `POST /customers`

#### Request
```json
{
  "type": "INDIVIDUAL",
  "name": "Maria da Silva",
  "documentType": "CPF",
  "documentNumber": "123.456.789-01"
}
```

#### Response 201
```json
{
  "id": "6f1bf0b6-5c53-4f65-9e31-ff7c31d629c7",
  "type": "INDIVIDUAL",
  "name": "Maria da Silva",
  "documentType": "CPF",
  "documentNumber": "12345678901",
  "status": "ACTIVE",
  "createdAt": "2026-01-28T12:00:00Z"
}
```

#### Exemplo (curl)
```bash
curl -i -X POST "http://localhost:8080/customers" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: test-api-key" \
  -H "X-Request-Id: req-001" \
  -d '{"type":"INDIVIDUAL","name":"Maria da Silva","documentType":"CPF","documentNumber":"123.456.789-01"}'
```

---

## 2) Buscar Customer por id

### `GET /customers/{customerId}`

#### Response 200
```json
{
  "id": "6f1bf0b6-5c53-4f65-9e31-ff7c31d629c7",
  "type": "INDIVIDUAL",
  "name": "Maria da Silva",
  "documentType": "CPF",
  "documentNumber": "12345678901",
  "status": "ACTIVE"
}
```

#### Exemplo (curl)
```bash
curl -i "http://localhost:8080/customers/6f1bf0b6-5c53-4f65-9e31-ff7c31d629c7" \
  -H "X-API-Key: test-api-key"
```

---

## 3) Buscar por documento (evitar duplicidade)

### `GET /customers?documentType=CPF&documentNumber=12345678901`

Decisão do MVP:
- Não existe listagem geral de customers sem filtros.
- Quando não houver resultados, retornar `200` com `items=[]` (não usar `204`).

#### Response 200
```json
{
  "items": [
    {
      "id": "6f1bf0b6-5c53-4f65-9e31-ff7c31d629c7",
      "type": "INDIVIDUAL",
      "name": "Maria da Silva",
      "documentType": "CPF",
      "documentNumber": "12345678901",
      "status": "ACTIVE"
    }
  ],
  "page": 0,
  "size": 20,
  "total": 1
}
```

#### Exemplo (curl)
```bash
curl -i "http://localhost:8080/customers?documentType=CPF&documentNumber=123.456.789-01&page=0&size=20" \
  -H "X-API-Key: test-api-key"
```

---

## 4) (Opcional) Inativar Customer (sem delete)

### `PATCH /customers/{customerId}`

#### Request
```json
{ "status": "INACTIVE" }
```

#### Exemplo (curl)
```bash
curl -i -X PATCH "http://localhost:8080/customers/6f1bf0b6-5c53-4f65-9e31-ff7c31d629c7" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: test-api-key" \
  -d '{"status":"INACTIVE"}'
```

---

## Erros (Problem Details + extensões)

Todas as respostas de erro seguem o contrato (alinhado ao ADR de Wallet):
- `type`, `title`, `status`, `detail`, `instance`
- `errorCode`
- `violations[]` (somente validação)
- `traceId` (sempre presente)

### Exemplo 409 — Customer já existe
```json
{
  "type": "https://errors.yourdomain.com/customer/conflict",
  "title": "Conflict",
  "status": 409,
  "detail": "Customer already exists for this document.",
  "instance": "/customers",
  "errorCode": "CUSTOMER_ALREADY_EXISTS",
  "traceId": "req-001"
}
```
