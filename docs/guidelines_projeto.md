# Diretrizes e Caracteristicas do Projeto

Este documento consolida as principais diretrizes, padroes e aprendizados observados no repositorio.

## Visao geral
- Projeto em Java com Quarkus.
- Dominio principal: **Ledger Core** (contabil) e **Wallet API**.
- Modulo **Customer** em padrao ingles: API/Application/Domain/Infra.

Regras gerais do projeto estao na pasta `rules/`.

## Camadas e organizacao
- **API** (resources/controllers + DTOs)
- **Application** (use cases, orquestracao, transacoes)
- **Domain** (entidades e validacoes)
- **Infra** (persistencia, mappers, migrations)

Pacotes principais:
- `com.sistema.ledger.*`
- `com.sistema.wallet.*`
- `com.sistema.creditcard.*`
- `com.sistema.customer.*`

## Customer (Fase 1 aplicada)
- Endpoint principal: `POST /customers` e `GET /customers` (sem alias `/clientes`).
- Pacotes padronizados:
  - `com.sistema.customer.api` + `com.sistema.customer.api.dto`
  - `com.sistema.customer.application`
  - `com.sistema.customer.domain.model` + `com.sistema.customer.domain.repository`
  - `com.sistema.customer.infra.entity` + `com.sistema.customer.infra.mapper` + `com.sistema.customer.infra.repository`
- Use cases: `CreateCustomerUseCase` e `ListCustomersUseCase` (resources finos).
- DTO aceita aliases para compatibilidade de payload:
  - `name` aceita `nome`
  - `phoneNumber` aceita `telefone` e `foneNumber`

Especificação do módulo:
- Consulte `docs/especificacao_modulo_customer_mvp_wallet.md` (PF/PJ no MVP, obrigatórios mínimos, unicidade por documento por tenant).

## Observacoes recentes de testes
- `LedgerAccountResource` deve retornar **400** quando `X-Tenant-Id` esta ausente.
- Testes de wallet esperam excecoes tipadas:
  - `WalletAccountAlreadyExistsException`
  - `WalletAccountNotFoundException`
  - `WalletInsufficientBalanceException`

## Reuso entre modulos (Wallet/Ledger/CreditCard)
- Separar componentes genericos (ErrorResponse, traceId filter, mappers base) em um pacote comum.
- Cada modulo define:
  - catalogo de `errorCode`
  - excecoes tipadas do dominio
  - resolver de `type/title` se necessario
- Objetivo: padrao uniforme com minima duplicacao.

Pacote sugerido para o comum:
- `com.sistema.common.api.trace.*`
- `com.sistema.common.api.error.*`
- `com.sistema.common.api.validation.*`
- `com.sistema.common.tenant.*` (resolver de tenant por API key compartilhado)

Configuracao de API keys (dev/test):
- `app.api-keys=key-dev=00000000-0000-0000-0000-000000000000` (pode reaproveitar `wallet.api-keys` se preferir; `app.api-keys` tem precedencia no resolver comum).

## Testes
- JUnit 5 + QuarkusTest.
- H2 em memoria para testes.
- Limpeza de banco via `DbCleanIT` (truncates com nomes quoted).
- Testes de integracao e de dominio separados.

## Cobertura de testes
- Cobertura gerada pelo **Quarkus JaCoCo**.
- Relatorio: `target/site/jacoco/index.html`.
- Comando:
  - `./mvnw test`

Se houver limite de memoria local:
```
$env:MAVEN_OPTS='-Xmx512m -XX:MaxMetaspaceSize=256m'; ./mvnw test
```

## Ferramentas e dependencias
- Quarkus 3.3.1
- Flyway (migrations)
- H2 para testes (versao alinhada ao Flyway)
- RESTEasy Reactive + Jackson
- MapStruct para mapeamento

## Observacoes praticas
- Nomes de tabelas podem ser case-sensitive no H2: use aspas no truncate.
- DTOs simples nao armazenam saldo; saldo vem do ledger.
- O uso de `id` em mapeamentos precisa ser explicito para evitar warnings do MapStruct.

