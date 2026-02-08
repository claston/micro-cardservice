# Especificacao - Extensao Multi-Tenant para o Ledger Core

> Objetivo: evoluir o Ledger Core (single-tenant) para suportar multi-tenancy seguro, permitindo multiplas plataformas (tenants) usando o mesmo motor contabil com isolamento logico de dados.

Esta especificacao estende a especificacao original do Ledger.

---

## 1. Conceito de Tenant

Um Tenant representa uma organizacao cliente da plataforma (ex: marketplace, fintech, SaaS).

Cada tenant tem seus proprios usuarios, contas contabeis e transacoes. O ledger continua unico, mas particionado por tenantId.

---

## 2. Alteracoes no Modelo de Dados

### 2.1 Nova Entidade: Tenant
Tabela `tenants` com campos minimos:
- id (UUID)
- name
- status (ACTIVE/SUSPENDED)
- created_at

### 2.2 Accounts (alteracao)
Adicionar coluna obrigatoria:
- tenant_id (FK para tenants)

### 2.3 Ledger Transactions (alteracao)
Adicionar:
- tenant_id

### 2.4 Entries (alteracao)
Adicionar:
- tenant_id

---

## 3. Migracao de Banco (Estrategia)

Passos sugeridos:
1. Criar tabela `tenants`
2. Adicionar coluna `tenant_id` nas tabelas principais (nullable temporariamente)
3. Criar um tenant padrao DEFAULT
4. Atualizar todos os registros existentes para esse tenant
5. Tornar `tenant_id` NOT NULL
6. Criar novas constraints UNIQUE compostas

---

## 4. Resultado Esperado

Apos esta evolucao, o Ledger Core sera capaz de servir multiplas plataformas com isolamento logico por tenant.

---

**Fim da Especificacao Multi-Tenant para o Ledger**
