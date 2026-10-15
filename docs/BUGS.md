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
