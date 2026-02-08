# Error Contract Rule

## Escopo
- Aplica-se a todas as APIs publicas e internas expostas.

## Contrato
- Problem Details com extensoes:
  - type, title, status, detail, instance
  - errorCode, traceId
  - violations[] para validacao

## Status padrao
- 400 validacao
- 401 nao autorizado
- 404 nao encontrado
- 409 conflito
- 500 inesperado

## Implementacao
- Bean Validation nos DTOs.
- Validar enums com mensagens amigaveis.
- Exceptions tipadas por modulo.
- Sempre retornar traceId.
