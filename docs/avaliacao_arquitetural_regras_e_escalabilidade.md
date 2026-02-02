# Avaliacao Arquitetural: Regras de Negocio e Escalabilidade

Objetivo: registrar pontos de melhoria para escalar regras de negocio (ex.: +100 regras)
sem perder clareza, consistencia e segurança operacional.

## 1) Diagnostico atual (resumo)

- Regras principais estao concentradas nos use cases (com validacoes imperativas).
- Parte das regras esta isolada em validadores estaticos no dominio, mas o centro
  de decisao ainda fica na camada Application.
- Padrao de erros/HTTP nao e totalmente uniforme entre modulos (Ledger vs Wallet).
- Regras criticas dependem de leituras antes de gravacao, sensiveis a concorrencia.
- Integridade de banco ainda nao cobre todas as invariantes (ex.: tenant em entries).

## 2) Riscos com +100 regras

- Explosao de complexidade nos use cases (cadeias de `if/throw`).
- Duplicacao de regras entre modulos e divergencia de mensagens/codigos.
- Testes grandes, acoplados e caros para isolar regras individuais.
- Inconsistencia de status HTTP e errorCode para regras semelhantes.
- Concorrencia pode gerar comportamentos inesperados (idempotencia, saldo).

## 3) Direcao recomendada (arquitetura de regras)

3.1 Pipeline de regras por caso de uso
- Criar uma lista ordenada de `Rule` (ou `Specification`) por caso de uso.
- Cada regra retorna um `RuleViolation` tipado (errorCode/status), em vez de
  lançar `IllegalArgumentException`.
- Beneficios: isolacao, reuso, testes unitarios por regra e clareza.

3.2 Padronizacao de erros
- Centralizar o mapeamento `RuleViolation` -> Problem Details.
- Manter catalogo de `errorCode` por modulo (Wallet/Ledger/Customer).

3.3 Separacao entre validacao estrutural e regra de negocio
- DTO/Bean Validation: formato, obrigatoriedade, ranges.
- Regras de negocio: dependem de estado (saldo, status, tenant).

3.4 Concorrencia e consistencia
- Idempotencia: ao violar unique index, recarregar transacao e devolver replay.
- Saldo negativo: considerar lock por conta ou read-model de saldo com check atomico.

3.5 Defesa no banco
- Triggers para imutabilidade em `ledger_transactions` e `entries`.
- Constraint/validacao para garantir `entries.tenant_id` = `ledger_transactions.tenant_id`.

## 4) Prioridades sugeridas (do mais critico ao evolutivo)

P1 - Consistencia e seguranca
1) Tratamento robusto de idempotencia em concorrencia.
2) Reforcos de integridade no banco (imutabilidade + tenant).
3) Padronizacao de erros (errorCode/status/Problem Details).

P2 - Escalabilidade de regras
4) Introduzir pipeline de regras por use case.
5) Migrar regras atuais para classes isoladas (1 regra = 1 classe).

P3 - Observabilidade e manutencao
6) Logs estruturados com `tenantId` e `idempotencyKey`.
7) Catalogo de regras e testes unitarios por regra.

## 5) Proximo passo de implementacao (sugestao)

- Definir interfaces base:
  - `RuleContext` (dados do comando + estado carregado)
  - `RuleViolation` (code, status, message, meta)
  - `Rule` (validate(context) -> Optional<RuleViolation>)
- Criar `PostingRulesPipeline` e `WalletTransferRulesPipeline`.
- Migrar 2-3 regras iniciais como exemplo (ex.: double-entry, currency mismatch, cross-tenant).
- Ajustar mapeador de erros para usar `RuleViolation`.

## 6) Definicao de sucesso

- Toda regra nova possui:
  - classe isolada
  - teste unitario proprio
  - `errorCode` padronizado
- Use cases viram orquestradores, nao conteudo de regra.
- Logs e respostas de erro ficam consistentes entre modulos.

