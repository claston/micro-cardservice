# Plano de Refatoracao do Modulo Customer (Fase 1..3)

Este documento descreve um plano incremental para padronizar o modulo `customer` com a arquitetura usada em `ledger` e `wallet`.

## Objetivos
- Padronizar nomenclatura, estrutura de pacotes e estilo.
- Reduzir acoplamento e deixar resources finos.
- Preparar o modulo para multi-tenancy e padrao de erros/validacao.

---

## Fase 1 - Padronizar casca (sem quebrar consumers)

### Escopo
- Criar API em ingles (/customers).
- Organizar pacotes por camadas (API/Application/Domain/Infra).
- Remover logging improprio nos resources.
- Consolidar repositorios duplicados.

### Mudancas recomendadas
1) Novos pacotes paralelos aos atuais.
2) Endpoints alinhados ao padrao do projeto.
3) Use cases dedicados para criar e listar customers.
4) Repositorio unico para leitura/escrita.
5) DTOs padronizados com aliases quando necessario.

### Definition of Done
- /customers funciona e esta padronizado.
- Resources sem serializacao manual.
- Um unico repositorio efetivo.

---

## Fase 2 - Multi-tenancy e modelo mais completo

### Escopo
- Introduzir tenantId no modelo e persistencia.
- Ajustar repositorios e use cases.
- Adicionar migracoes Flyway para customer.

---

## Fase 3 - Validacao declarativa e erros padronizados

### Escopo
- Adotar Bean Validation nos DTOs.
- Padronizar erros conforme contrato do projeto.
- Introduzir excecoes tipadas no Application.

---

## Notas e decisoes em aberto
- Campos obrigatorios no cadastro de customer.
- Chaves unicas por tenant.
- Mecanismo de autenticacao/tenant nas rotas publicas.

Consulte `docs/especificacao_modulo_customer_mvp_wallet.md` para detalhes do modulo.
