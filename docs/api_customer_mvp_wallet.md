# API - Customer (MVP para Wallet)

> Ambiente dev: `http://localhost:8080`
>
> Escopo: endpoints minimos para criar/consultar Customer e suportar Wallet (ownerType=CUSTOMER, ownerId=<customerId>).

---

## Modelo (MVP)

- type: INDIVIDUAL | BUSINESS
- documentType: CPF | CNPJ
- documentNumber: string (a API normaliza e retorna somente digitos)
- status: ACTIVE | INACTIVE

---

## 1) Criar Customer

### POST /customers

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

### GET /customers/{customerId}

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

### GET /customers?documentType=CPF&documentNumber=12345678901

Decisao do MVP:
- Nao existe listagem geral de customers sem filtros.
- Quando nao houver resultados, retornar 200 com items=[], nao usar 204.

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

### PATCH /customers/{customerId}

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
