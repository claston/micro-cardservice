# ADR — Erros Padronizados e Validação Declarativa na Wallet API

- **Status:** Accepted
- **Data:** 2026-01-25
- **Contexto:** Wallet API (Fase 1) sobre Ledger Core

---

## Contexto

Durante testes do MVP da Wallet, ocorreram respostas **400 genéricas** (ex.: “Solicitação Inválida”) quando o payload possuía valores inválidos (ex.: `ownerType` diferente de `CUSTOMER`). Isso dificulta o uso por clientes externos, aumenta tempo de integração e gera suporte desnecessário.

O comportamento atual usa `try/catch` de `IllegalArgumentException` no resource e retorna `WebApplicationException` sem um contrato de erro consistente.

A especificação do MVP introduziu o requisito **RF-06 — Modelo de Erro (Developer-friendly)**, recomendando um formato estruturado baseado em **Problem Details (RFC 7807)** com `errorCode`, `violations[]` e `traceId`.

---

## Decisão

A Wallet API adota validação declarativa e um contrato de erro padronizado. Os detalhes operacionais estao consolidados nas rules do projeto.

---

## Motivações

- Melhorar DX (Developer Experience) e reduzir fricção de integração
- Tornar o MVP vendável (API autoexplicativa)
- Evitar “debug lendo código” em situações comuns
- Padronizar o tratamento de erros desde o início

---

## Alternativas consideradas

### A) Manter `try/catch` no resource com mensagens simples
- **Prós:** rápido
- **Contras:** inconsistência, difícil padronizar, não escala

### B) Retornar apenas mensagens de texto
- **Prós:** simples
- **Contras:** não suporta erros por campo e automação do cliente

### C) Expor o ledger para debug
- **Prós:** facilita testes internos
- **Contras:** quebra encapsulamento e segurança do produto

---

## Consequências

### Positivas
- Contrato claro para clientes externos
- Redução de suporte e tempo de integração
- Melhor observabilidade com `traceId`

### Negativas / Custos
- Implementar DTO validations + exception mappers
- Manter catálogo de `errorCode`

---

## Notas

- O domínio do Ledger não deve ser exposto em erros (sem IDs/entidades internas além do necessário).
- Preferir mensagens úteis, mas não vazar detalhes sensíveis (ex.: stacktrace).




