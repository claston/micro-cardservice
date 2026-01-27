# Plano de Refatoracao do Modulo Customer (Fase 1..3)

Este documento descreve um plano incremental para padronizar o modulo `customer` com a arquitetura e convencoes usadas em `ledger` e `wallet`, reduzindo risco de regressao e mantendo compatibilidade durante a transicao.

## Objetivos
- Padronizar nomenclatura, estrutura de pacotes e estilo (PT/EN consistente).
- Reduzir acoplamento e deixar resources "finos" (API -> Application -> Domain -> Infra).
- Preparar o modulo para multi-tenancy e para padrao de erros/validacao (Problem Details + Bean Validation).

## Principios (nao negociaveis)
- Nao quebrar clientes existentes durante a Fase 1 (manter `/clientes` funcionando).
- Evitar dependencias diretas entre modulos (ex.: wallet nao deve depender de classes do customer).
- Padronizar gradualmente com migracoes pequenas e revisaveis.

---

## Fase 1 — Padronizar "casca" (sem quebrar consumers)

### Escopo
- Criar uma nova API em ingles (`/customers`).
- Como a aplicacao nao esta em PRD, a recomendacao padrao e **renomear** `/clientes` -> `/customers` (sem manter alias).
- Organizar pacotes para refletir as camadas do projeto (API/Application/Domain/Infra).
- Remover codigo de debug/logging improprio (`System.out`, `ObjectMapper` manual) dos resources.
- Consolidar reposititorios duplicados e corrigir inconsistencias obvias.

### Mudancas recomendadas
1) **Novos pacotes (paralelos aos atuais)**
   - `com.sistema.customer.api` e `com.sistema.customer.api.dto`
   - `com.sistema.customer.application`
   - `com.sistema.customer.domain.model` e `com.sistema.customer.domain.repository`
   - `com.sistema.customer.infra.entity`, `com.sistema.customer.infra.mapper`, `com.sistema.customer.infra.repository`

2) **Endpoints**
   - Criar `CustomerResource` com `@Path("/customers")`.
   - Remover/renomear `CriarClienteResource` (evitar manter dois endpoints).
   - Opcional: manter `/clientes` como wrapper apenas se houver consumidores legados.

3) **Use cases**
   - Introduzir `CreateCustomerUseCase` e `ListCustomersUseCase`.
   - Deprecar `ClienteService` (ou remover se nao for usado) para evitar duplicacao com use case.

4) **Repositorio**
   - Escolher um unico adapter (Panache) como implementacao do `CustomerRepository`.
   - Remover ou corrigir `ClienteRepository` (hoje usa `ORDER BY nome`, mas a entidade tem `name`).

5) **DTOs e retorno**
   - Padronizar payload e nomenclatura de campos (`phoneNumber` em vez de `foneNumber`, etc.).
   - Definir contrato minimo da API (campos de entrada/saida) e manter compatibilidade via alias quando necessario.

### Definition of Done
- `/customers` funciona e esta padronizado.
- Se `/clientes` existir, deve ser apenas por compatibilidade e delegar para o fluxo novo.
- Resources sem `System.out` e sem serializacao manual.
- Um unico repositorio efetivo para leitura/escrita (sem duplicidade/confusao).
- Testes ajustados para cobrir `/customers` (e opcionalmente manter cobertura de `/clientes`).

---

## Fase 2 — Multi-tenancy + modelo mais completo

### Escopo
- Introduzir `tenantId` no modelo e persistencia de Customer.
- Ajustar repositorios e use cases para sempre exigir `tenantId`.
- Adicionar migracoes Flyway para criar/alterar tabelas de customer com constraints por tenant.

### Sugestoes de modelagem
- `Customer` com `id`, `tenantId`, `status`, `createdAt`, `updatedAt` (ou ao menos `createdAt`).
- Documento: substituir `cpf`/`cnpj` separados por `documentType` + `documentNumber` (opcional).
- Constraints: `UNIQUE(tenant_id, document_number)` e/ou `UNIQUE(tenant_id, email)` (decidir regra de negocio).

### Definition of Done
- Nenhum Customer existe sem `tenantId`.
- Queries sempre filtram por `tenantId`.
- Migracoes aplicadas e testes de coexistencia multi-tenant adicionados.

---

## Fase 3 — Validacao declarativa + erros padronizados

### Escopo
- Adotar Bean Validation nos DTOs do Customer.
- Padronizar erros no formato Problem Details (RFC 7807 + extensoes) com `errorCode`, `violations[]`, `traceId`.
- Introduzir excecoes tipadas no Application (ex.: `CustomerNotFound`, `CustomerAlreadyExists`).

### Definition of Done
- Erros de validacao retornam 400 com `violations[]`.
- Conflitos retornam 409 com `errorCode` estavel.
- `traceId` sempre presente e reutiliza `X-Request-Id`.

---

## Notas e decisoes em aberto
- Quais campos sao obrigatorios no cadastro de customer (nome, email, documento, data nascimento, etc.)?
- Quais chaves sao unicas por tenant (CPF/CNPJ? email? ambos?)?
- O endpoint de customer deve exigir autenticacao/tenant da mesma forma que wallet (ex.: `X-API-Key`) ou outro mecanismo?

## Esclarecimento de dominio (Tenant vs Customer)
- `tenant` representa o cliente pagante do sistema (B2B / organizacao).
- `customer` representa o cliente final dentro de um tenant (B2C/B2B2C), ou seja, o "cliente do tenant".
- Consequencia: o mesmo CPF/CNPJ pode existir em tenants diferentes, mas deve ser unico dentro do mesmo tenant.

## Regras propostas (para Fase 2)
- Campos obrigatorios (minimo): `document` (CPF/CNPJ), `name`, `birthDate` (quando `documentType=CPF`).
- Unicidade: `UNIQUE(tenant_id, document_type, document_number)`.
