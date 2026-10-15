# Registro de defeitos

Defeitos encontrados durante a automação da API Restful-Booker.
Os testes validam a regra de negócio e deixam o desvio documentado aqui, em vez de mascarar o problema.

## BUG-001 — Login inválido retorna 200 em vez de 401

| Campo | Detalhe |
| --- | --- |
| Severidade | Média |
| Endpoint | `POST /auth` |
| Passos | Enviar `{"username": "admin", "password": "senha-errada"}` |
| Resultado esperado | `401 Unauthorized` |
| Resultado obtido | `200 OK` com corpo `{"reason": "Bad credentials"}` |
| Impacto | Clientes que confiam só no status HTTP tratam a falha de login como sucesso |
| Teste relacionado | `AuthTest.naoGeraTokenComSenhaInvalida` |

## BUG-002 — Exclusão retorna 201 Created

| Campo | Detalhe |
| --- | --- |
| Severidade | Baixa |
| Endpoint | `DELETE /booking/{id}` |
| Passos | Excluir uma reserva existente com token válido |
| Resultado esperado | `200 OK` ou `204 No Content` |
| Resultado obtido | `201 Created` |
| Impacto | Status semanticamente incorreto, pode confundir integrações e monitoramento |
| Teste relacionado | `BookingCrudTest.excluiReserva` |

## BUG-003 — Criação de reserva recusa header Accept com múltiplos tipos

| Campo | Detalhe |
| --- | --- |
| Severidade | Média |
| Endpoint | `POST /booking` |
| Passos | Enviar uma reserva válida com `Accept: application/json, application/javascript, text/javascript, text/json` |
| Resultado esperado | `200 OK` com a reserva criada, já que `application/json` está entre os tipos aceitos |
| Resultado obtido | `418 I'm a Teapot` |
| Impacto | Clientes que enviam vários tipos no Accept (o padrão do REST Assured com `ContentType.JSON`) não conseguem criar reservas |
| Contorno no projeto | `ApiConfig` envia `Accept: application/json` explicitamente |

**Como foi investigado:** na primeira execução do pipeline, todos os testes que criam reserva falharam com 418, enquanto `/auth` e `/ping` passaram. A primeira hipótese (charset no Content-Type) foi descartada. Um diagnóstico com curl no próprio runner testou as variações de header isoladamente:

| Content-Type | Accept | Status |
| --- | --- | --- |
| `application/json` | `application/json` | 200 |
| `application/json; charset=UTF-8` | `application/json` | 200 |
| `application/json` | (ausente) | 200 |
| `application/json` | `application/json, application/javascript, text/javascript, text/json` | **418** |
