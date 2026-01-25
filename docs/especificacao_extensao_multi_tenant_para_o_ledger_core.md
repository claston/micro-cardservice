# Especificação — Extensão Multi-Tenant para o Ledger Core

> Objetivo: evoluir o **Ledger Core** existente (single-tenant) para suportar **multi-tenancy seguro**, permitindo que múltiplas plataformas (tenants) utilizem o mesmo motor contábil com isolamento lógico de dados.

Esta especificação **estende a especificação original do Ledger**, sem alterar seus princípios de imutabilidade e double-entry.

---

## 1. Conceito de Tenant

Um **Tenant** representa uma organização cliente da sua plataforma (ex: um marketplace, fintech, SaaS).

Cada tenant deve ter:
- Seus próprios usuários (owners das contas)
- Suas próprias contas contábeis
- Suas próprias transações
- Isolamento total de dados em relação a outros tenants

O ledger continua único, mas logicamente particionado por `tenantId`.

---

## 2. Princípio de Isolamento

**Regra fundamental:**
> Nenhuma Account, Entry ou LedgerTransaction pode existir sem um `tenantId`.

E:
> Uma transação nunca pode envolver contas de tenants diferentes.

---

## 3. Alterações no Modelo de Dados

### 3.1 Nova Entidade: Tenant
Tabela `tenants`

Campos mínimos:
- `id` (UUID)
- `name`
- `status` (ACTIVE / SUSPENDED)
- `created_at`

---

### 3.2 Accounts (alteração)
Adicionar coluna obrigatória:
- `tenant_id` (FK para tenants)

Novas restrições:
- UNIQUE (`tenant_id`, `owner_type`, `owner_id`, `currency`, `account_type`)
- Todas as consultas devem filtrar por `tenant_id`

---

### 3.3 Ledger Transactions (alteração)
Adicionar:
- `tenant_id`

Restrição:
- UNIQUE (`tenant_id`, `idempotency_key`)

Isso permite que diferentes tenants reutilizem a mesma chave de idempotência sem conflito global.

---

### 3.4 Entries (alteração)
Adicionar:
- `tenant_id`

Regra:
- `entries.tenant_id` deve ser igual ao `tenant_id` da transaction

---

## 4. Regras de Negócio Adicionais

### RN-01 — Isolamento de Transações
Ao criar uma LedgerTransaction:
- Todas as accounts envolvidas devem pertencer ao mesmo `tenantId`
- Caso contrário, lançar erro `CrossTenantOperationException`

---

### RN-02 — Idempotência por Tenant
A chave de idempotência passa a ser única **dentro do tenant**, não global.

---

### RN-03 — Proibição de Transferência entre Tenants
Não é permitido movimentar saldo entre contas de tenants diferentes.

---

## 5. Alterações na Camada de Aplicação

Todos os use cases do ledger passam a exigir `tenantId` como parâmetro obrigatório.

Exemplo:
```
postTransaction(tenantId, idempotencyKey, entries, description)
```

O `tenantId` não deve ser inferido do banco — deve vir da camada de autenticação.

---

## 6. Segurança e Autenticação

### API Key → Tenant
Cada chamada à Wallet API deve resolver um `tenantId` a partir da API Key.

Fluxo:
1. Request chega com `X-API-Key`
2. Sistema identifica o `tenantId`
3. `tenantId` é propagado para o Ledger Use Cases

---

## 7. Consultas e Índices

Todos os índices críticos passam a incluir `tenant_id` como primeira coluna:

- `entries(tenant_id, account_id, occurred_at)`
- `ledger_transactions(tenant_id, idempotency_key)`
- `accounts(tenant_id, owner_id)`

Isso garante boa performance e isolamento lógico.

---

## 8. Migração de Banco (Estratégia)

Passos sugeridos:
1. Criar tabela `tenants`
2. Adicionar coluna `tenant_id` nas tabelas principais (nullable temporariamente)
3. Criar um tenant padrão `DEFAULT`
4. Atualizar todos os registros existentes para esse tenant
5. Tornar `tenant_id` NOT NULL
6. Criar novas constraints UNIQUE compostas

---

## 9. Testes

Adicionar cenários de teste:
- Criar contas iguais em tenants diferentes (deve permitir)
- IdempotencyKey repetida em tenants diferentes (deve permitir)
- Tentativa de transferir entre tenants (deve falhar)

---

## 10. Resultado Esperado

Após esta evolução, o Ledger Core será capaz de:

- Servir múltiplas plataformas
- Garantir isolamento total de dados
- Manter consistência contábil por tenant

Isso habilita a Wallet API a ser oferecida como **produto multi-cliente** com segurança.

---

**Fim da Especificação Multi-Tenant para o Ledger**

