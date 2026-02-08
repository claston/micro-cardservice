# Ledger Immutability Rule

## Escopo
- Aplica-se a ledger_transactions e entries.

## Regras obrigatorias
- Append-only: sem UPDATE ou DELETE via aplicacao.
- Correcao por compensacao/reversao (nova transacao).

## Banco
- Triggers bloqueiam UPDATE/DELETE nas tabelas de ledger.
- Usuario da aplicacao com apenas SELECT/INSERT nas tabelas de ledger.
- created_at e external_reference para auditoria.

## API
- Nenhum endpoint deve atualizar ou deletar rows do ledger.

## Testes
- Tentativas de UPDATE/DELETE devem falhar.
