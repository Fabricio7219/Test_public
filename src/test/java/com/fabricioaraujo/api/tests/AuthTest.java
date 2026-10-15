package com.fabricioaraujo.api.tests;

import com.fabricioaraujo.api.config.ApiConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@DisplayName("API - Autenticação")
class AuthTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @Tag("regressao")
    @DisplayName("Gera token com credenciais válidas")
    void geraTokenComCredenciaisValidas() {
        client.autenticar(ApiConfig.USERNAME, ApiConfig.PASSWORD)
                .then()
                .statusCode(200)
                .body("token", not(emptyOrNullString()));
    }

    @Test
    @Tag("regressao")
    @DisplayName("Não gera token com senha inválida")
    void naoGeraTokenComSenhaInvalida() {
        // Observação: a API responde 200 em vez de 401 (ver docs/BUGS.md, BUG-001).
        // Aqui validamos a regra de negócio: nenhum token pode ser emitido.
        client.autenticar(ApiConfig.USERNAME, "senha-errada")
                .then()
                .body("token", nullValue())
                .body("reason", equalTo("Bad credentials"));
    }
}
