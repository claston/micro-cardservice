# Especificação — Núcleo Ledger (separado do domínio de Cartão)

> Objetivo: criar um **núcleo de Ledger** (razão contábil) independente do domínio de cartão de crédito, com APIs e modelo de dados próprios, permitindo que o domínio “Cartão/Cliente/Transação de Cartão” poste efeitos contábeis no ledger sem acoplamento.

## 1. Contexto e Motivação
Hoje o sistema possui entidades como **Customer**, **CreditCard** e **Transação (cartão)**. O objetivo é evoluir para um **Ledger** que seja a **fonte de verdade de saldos** via lançamentos **imutáveis** (append-only).

### Definições
- **Transação de Cartão**: evento de negócio (compra, estorno, pagamento etc.). Pode ter status e ciclo de vida.
- **Transação de Ledger**: agrupador imutável de lançamentos (entries) que precisam “fechar” (double-entry).
- **Entry**: lançamento contábil em uma conta (debit/credit).

### Princípios
- Imutabilidade (sem UPDATE de entries/transações — ajustes são novas transações).
- Double-entry (a soma deve fechar por moeda).
- Idempotência por chave (evitar duplicidade de postagens).
- Auditabilidade e rastreabilidade.

---

## 2. Escopo do MVP
### Em escopo
1) Criar e gerenciar **Accounts** (contas do ledger)
2) Postar **Ledger Transactions** com **Entries** (double-entry)
3) Consultar **Saldo** por conta
4) Consultar **Extrato** (statement) por conta
5) Suporte a **idempotencyKey** por transação
6) Integração com H2 para testes e PostgreSQL para produção (compatível)

### Fora de escopo (por enquanto)
- Multi-moeda avançada (FX) — apenas validação por moeda
- Conciliação bancária (Pix/boletos) completa
- Event streaming / outbox (pode entrar depois)
- Regras complexas de limite/crédito
- Split payments avançado

---

## 3. Requisitos Funcionais (RF)

### RF-01 — Criar Conta (Account)
- O sistema deve permitir criar uma conta com:
  - `name` (obrigatório)
  - `type` (ex.: ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE) — MVP pode usar enum simples
  - `currency` (ex.: BRL, USD) — string/enum
  - `allowNegative` (boolean)
  - `status` (ACTIVE/INACTIVE)
- Deve retornar `accountId`.

### RF-02 — Consultar Conta
- Deve permitir buscar detalhes de uma conta por `accountId`.

### RF-03 — Postar Transação no Ledger (LedgerTransaction)
- Deve receber um comando contendo:
  - `idempotencyKey` (obrigatório)
  - `externalReference` (opcional; ex.: id da transação do cartão)
  - `description` (opcional)
  - `occurredAt` (opcional; default = now)
  - `entries[]` (obrigatório; >=2)
    - `accountId`
    - `direction` (DEBIT|CREDIT)
    - `amount` (valor)
    - `currency` (opcional; se não vier, herda da conta)
- Deve validar:
  - RF-03.1 Double-entry: **soma(DEBIT) == soma(CREDIT)** por moeda
  - RF-03.2 Todas as contas existem e estão ACTIVE
  - RF-03.3 Moeda consistente (entries e contas)
  - RF-03.4 Se `allowNegative=false`, não permitir que a transação resulte em saldo negativo (ver RF-05)
- Persistir a transação e seus entries de forma atômica.

### RF-04 — Idempotência
- Se uma requisição com o mesmo `idempotencyKey` (no mesmo tenant, se houver) for enviada novamente:
  - deve retornar a **mesma transação** previamente criada (sem duplicar entries).

### RF-05 — Saldo por Conta
- Deve permitir obter o saldo atual de uma conta.
- Cálculo MVP:
  - saldo = soma(credits) - soma(debits) **ou** vice-versa, desde que consistente (definir convenção)
  - Recomendação: armazenar `direction` e derivar `signedAmount`.

### RF-06 — Extrato (Statement)
- Deve permitir listar entries por `accountId` com filtros:
  - `from` / `to` (datas)
  - paginação (`page`, `size`) e ordenação por `occurredAt` desc/asc.
- Deve retornar:
  - data, descrição, transactionId, amount, direction, saldo parcial (opcional)

### RF-07 — Estorno/Ajuste
- O estorno é uma nova LedgerTransaction que inverte os lançamentos (não altera histórico).
- Deve permitir `POST /ledger/transactions/{id}/reverse` (opcional no MVP; pode ficar para fase 2)

### RF-08 — Integração com domínio de Cartão (adaptação)
- O domínio de cartão deve conseguir “postar” efeitos contábeis sem conhecer detalhes internos:
  - via `LedgerPostingService` (porta na aplicação) ou chamando endpoint.
- Deve suportar `externalReference = cardTransactionId`.

---

## 4. Requisitos Não Funcionais (RNF)

### RNF-01 — Consistência
- Postagem de transação e entries deve ser **atômica** (transação de DB).

### RNF-02 — Imutabilidade
- Não permitir UPDATE/DELETE de entries e ledger_transactions via API.
- **Garantias no PostgreSQL (sem Datomic):**
  1) **Append-only por design**: a aplicação só faz `INSERT` em `ledger_transactions` e `entries`.
  2) **Privilégios mínimos (recomendado):** usar um usuário/role do app com permissões apenas de `SELECT/INSERT` nas tabelas do ledger (sem `UPDATE/DELETE`).
  3) **Triggers de proteção (defesa em profundidade):** triggers `BEFORE UPDATE OR DELETE` nas tabelas `entries` e `ledger_transactions` que lançam exceção.
  4) **Correções via compensação:** qualquer correção deve ser uma nova `ledger_transaction` (ex.: estorno/ajuste), nunca alteração do histórico.
  5) **Auditoria:** `created_at`, `created_by` (opcional) e `external_reference` para rastreio.

### RNF-03 — Performance (MVP)
- Consultas de saldo e extrato devem funcionar com índices adequados.
- Preparar para futura projeção de saldos (read model) sem quebrar contrato.

### RNF-04 — Observabilidade
- Logs estruturados para:
  - criação de transação
  - erro de validação
  - violação de idempotência

### RNF-05 — Testabilidade
- Testes unitários para regras (double-entry, moeda, saldo negativo).
- Testes de integração com H2 com **limpeza automática** entre testes.

### RNF-06 — Portabilidade
- SQL e migrations compatíveis com PostgreSQL (H2 em MODE=PostgreSQL para testes).

### RNF-07 — Segurança (mínimo)
- Preparar endpoints para autenticação (pode ser stub no MVP).
- Validar inputs e retornar erros claros (400/409).

---

## 5. Arquitetura Proposta

### Camadas
- **API (Resource/Controller)**: validação básica, DTOs
- **Application (Use Cases)**: orquestração + transação + idempotência
- **Domain (Core)**: entidades/VOs + validações
- **Infrastructure**: persistence (JPA/JDBC), migrations, config
- **Read Model (opcional)**: projeções de saldo

### Estrutura sugerida (monólito modular)
```
/ledger
  /api
  /application
  /domain
  /infra
```

---

## 6. Modelo de Domínio (sugestão)

### Value Objects
- `Money { long amountMinor; String currency; }`  (recomendado)
- `IdempotencyKey { String value; }`

### Entities
- `Account { AccountId id; String name; AccountType type; Currency currency; boolean allowNegative; Status status; }`
- `LedgerTransaction { TxId id; IdempotencyKey key; String externalReference; String description; Instant occurredAt; List<Entry> entries; }`
- `Entry { EntryId id; TxId transactionId; AccountId accountId; Direction direction; Money money; Instant occurredAt; }`

### Regras
- `DoubleEntryValidator.validate(entries)`
- `NegativeBalancePolicy.check(account, resultingBalance)`

---

## 7. Persistência e Migrations

### Tabelas
- `accounts`
- `ledger_transactions`
- `entries`

### Campos mínimos (proposta)
**accounts**
- id (UUID)
- name
- type
- currency
- allow_negative
- status
- created_at

**ledger_transactions**
- id (UUID)
- idempotency_key (UNIQUE)
- external_reference (nullable)
- description (nullable)
- occurred_at
- created_at

**entries**
- id (UUID)
- transaction_id (FK)
- account_id (FK)
- direction (DEBIT/CREDIT)
- amount_minor (BIGINT)
- currency
- occurred_at
- created_at

### Índices
- unique(idempotency_key)
- idx_entries_account_date(account_id, occurred_at)
- idx_entries_tx(transaction_id)

---

## 8. Contratos de API (proposta)

### POST /ledger/accounts
Request:
```json
{ "name": "Customer Wallet", "type": "ASSET", "currency": "BRL", "allowNegative": false }
```
Response:
```json
{ "accountId": "..." }
```

### POST /ledger/transactions
Request:
```json
{
  "idempotencyKey": "card-txn-123",
  "externalReference": "cardTxnId-123",
  "description": "Compra no merchant X",
  "occurredAt": "2026-01-24T10:00:00Z",
  "entries": [
    { "accountId": "A1", "direction": "DEBIT",  "amountMinor": 10000, "currency": "BRL" },
    { "accountId": "A2", "direction": "CREDIT", "amountMinor": 10000, "currency": "BRL" }
  ]
}
```
Responses:
- 201 Created + payload com `transactionId`
- 200 OK se idempotente (mesmo `idempotencyKey`) retornando a transação existente
- 400 Bad Request (violação double-entry, conta inexistente, moeda inválida)
- 409 Conflict (casos de chave única conflitante com payload diferente — opcional)

### GET /ledger/accounts/{id}/balance
Response:
```json
{ "accountId": "A1", "balanceMinor": 25000, "currency": "BRL" }
```

### GET /ledger/accounts/{id}/statement?from=&to=&page=&size=
Response:
```json
{
  "accountId": "A1",
  "items": [
    { "occurredAt": "...", "transactionId": "...", "description": "...", "direction": "DEBIT", "amountMinor": 10000, "currency": "BRL" }
  ],
  "page": 0,
  "size": 20,
  "total": 123
}
```

---

## 9. Estratégia de Testes

### Unit Tests (domínio)
- Double-entry fecha
- Falha quando não fecha
- Falha quando conta INACTIVE
- Falha quando moeda diverge
- Falha quando saldo negativo não permitido

### Integration Tests (repositório/API)
- H2 em `MODE=PostgreSQL`
- `DbCleanIT` para truncar tabelas entre testes
- Separar IT por tag `@Tag("it")`

---

## 10. Plano de Implementação (incremental)

### Fase 1 — Núcleo
1. Criar entidades/VOs e validações
2. Migrations + repositórios
3. Endpoint POST /ledger/transactions com idempotência
4. GET balance (derivado)

### Fase 2 — APIs completas
5. Criar accounts
6. Statement paginado

### Fase 3 — Integração com cartão
7. Adapter/Service no domínio de cartão que chama Ledger
8. Mapear evento do cartão → posting no ledger

---

## 11. Entregáveis para o Codex (geração de estrutura)

### Deve gerar
- Pacotes `/ledger/api`, `/ledger/application`, `/ledger/domain`, `/ledger/infra`
- DTOs e Resources para accounts/transactions
- Use cases (`CreateAccount`, `PostTransaction`, `GetBalance`, `GetStatement`)
- Entidades de persistência + repositories
- Migrations Flyway
- Testes unitários e de integração + `DbCleanIT`

### Padrões
- Java + Quarkus
- JUnit 5
- Flyway
- H2 para testes
- PostgreSQL compat

---

## 12. Perguntas em aberto (para próxima iteração)
1) Vai ser **multi-tenant** desde o início? (se sim, adicionar `tenant_id` em tudo e compor idempotency por tenant)
2) Convenção de saldo: `CREDIT - DEBIT` ou o inverso? (vamos padronizar)
3) Vamos armazenar dinheiro em `long minor units` (recomendado) ou `BigDecimal`?
4) Precisamos de endpoint de reversão já no MVP?

---

## 13. Imutabilidade usando apenas PostgreSQL (sem Datomic)

Embora bancos como Datomic tenham imutabilidade nativa, é totalmente viável implementar um ledger **append-only** seguro usando PostgreSQL, combinando modelagem, constraints e triggers.

### 13.1 Estratégia geral
- Tabelas de ledger (`ledger_transactions`, `entries`) são **somente inserção (INSERT-only)**.
- Não permitir UPDATE ou DELETE nessas tabelas.
- Correções e estornos são feitos por **novas transações**, nunca por alteração de dados antigos.

### 13.2 Bloqueando UPDATE e DELETE no nível do banco
Criar triggers que impedem qualquer modificação após inserção.

```sql
CREATE OR REPLACE FUNCTION prevent_mutation()
RETURNS trigger AS $$
BEGIN
  RAISE EXCEPTION 'Ledger data is immutable. Use reversal transactions.';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER no_update_ledger_transactions
BEFORE UPDATE OR DELETE ON ledger_transactions
FOR EACH ROW EXECUTE FUNCTION prevent_mutation();

CREATE TRIGGER no_update_entries
BEFORE UPDATE OR DELETE ON entries
FOR EACH ROW EXECUTE FUNCTION prevent_mutation();
```

Isso garante que nem um erro de código ou acesso direto consiga alterar o histórico.

### 13.3 Controle de acesso (opcional, reforço)
Criar um usuário de aplicação com permissões apenas de `INSERT` e `SELECT` nessas tabelas.

```sql
REVOKE UPDATE, DELETE ON ledger_transactions FROM app_user;
REVOKE UPDATE, DELETE ON entries FROM app_user;
```

### 13.4 Idempotência forte
A imutabilidade depende de evitar duplicações:

```sql
ALTER TABLE ledger_transactions
ADD CONSTRAINT uq_idempotency UNIQUE (idempotency_key);
```

Se for multi-tenant futuramente:
```
UNIQUE (tenant_id, idempotency_key)
```

### 13.5 Auditoria automática
Adicionar colunas automáticas para rastreabilidade.

```sql
ALTER TABLE ledger_transactions ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now();
ALTER TABLE entries ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now();
```

### 13.6 Garantindo integridade double-entry no banco (opcional avançado)
A validação principal fica na aplicação, mas pode haver verificação adicional via trigger DEFERRABLE.

```sql
-- Exemplo conceitual (validação final antes do commit)
-- Pode ser implementado com constraint deferrable ou trigger AFTER INSERT
```

### 13.7 Reversão em vez de edição
Se uma transação precisar ser "desfeita":
- Criar nova ledger_transaction
- Inserir entries com **direções invertidas**
- Referenciar a transação original em `external_reference`

### 13.8 Resultado prático
Com essas regras, o PostgreSQL passa a funcionar como um **event store contábil**:
- Histórico completo
- Nenhuma mutação destrutiva
- Total auditabilidade
- Compatível com projeções e reconstrução de estado

Essa abordagem é amplamente utilizada em sistemas financeiros que não usam bancos imutáveis nativos.

---

## 14. Quando as tabelas ficarem grandes (estratégia de escala)

A tabela `entries` tende a crescer continuamente (append-only). Isso é esperado em um ledger. A arquitetura deve prever **boas leituras** sem comprometer a integridade.

### 14.1 MVP (até ~milhões de entries)
- Índices certos:
  - `entries(account_id, occurred_at)`
  - `entries(transaction_id)`
  - `ledger_transactions(idempotency_key)`
- Consultas paginadas no extrato (sempre com `LIMIT/OFFSET` ou keyset pagination).
- Saldo derivado por soma (ok no início).

### 14.2 Projeções de leitura (recomendado para crescer)
**Introduzir um Read Model** sem mudar o ledger (continua append-only):
- `account_balances(account_id, balance_minor, currency, updated_at)`
- Atualização:
  - **sincrona** (no mesmo commit) para consistência forte, ou
  - **assíncrona** via outbox/consumer para escala (eventual consistency).

Isso deixa `GET /balance` O(1) e elimina somas gigantes.

### 14.3 Particionamento no PostgreSQL (quando for grande mesmo)
Quando o volume subir muito (dezenas/centenas de milhões):
- Particionar `entries` por **data** (`occurred_at`) ou por **hash(account_id)**.
  - Por data: ótimo para retenção e extrato por período.
  - Por hash de account: ótimo para distribuir escrita/leitura por conta.
- Com partições, o Postgres “pula” partições fora do filtro (partition pruning).

### 14.4 Retenção e arquivamento (sem perder auditoria)
Ledger costuma exigir retenção longa, mas você pode:
- manter partições antigas em storage mais barato (cold)
- exportar partições fechadas para data lake/backup imutável
- manter o “hot set” (últimos N meses) mais performático

### 14.5 Paginação correta para extrato
Evitar `OFFSET` alto quando crescer:
- usar **keyset pagination**: `WHERE occurred_at < :cursor` ORDER BY occurred_at DESC LIMIT :size
- cursor pode ser `(occurred_at, entry_id)` para desempate.

### 14.6 Escala horizontal
- Read replicas para consultas (extrato/relatórios)
- Caching de consultas frequentes (balance)
- Sharding é possível no futuro, mas geralmente **partições + read model** resolvem por muito tempo.

### 14.7 Resumo de evolução sugerida
1) MVP: índices + paginação
2) Crescimento: `account_balances` (read model)
3) Muito grande: partições + keyset pagination + replicas


