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

## BUG-003 — Criação de reserva recusa Content-Type com charset

| Campo | Detalhe |
| --- | --- |
| Severidade | Média |
| Endpoint | `POST /booking` |
| Passos | Enviar uma reserva válida com header `Content-Type: application/json; charset=UTF-8` |
| Resultado esperado | `200 OK` com a reserva criada (o parâmetro charset é válido pela RFC 8259) |
| Resultado obtido | `418 I'm a Teapot` |
| Impacto | Clientes HTTP que incluem charset por padrão (como o REST Assured) não conseguem criar reservas |
| Contorno no projeto | `ApiConfig` desliga o charset automático do REST Assured |
| Como foi encontrado | Primeira execução do pipeline: todos os testes que criam reserva falharam com 418, enquanto `/auth` e `/ping` passaram |
