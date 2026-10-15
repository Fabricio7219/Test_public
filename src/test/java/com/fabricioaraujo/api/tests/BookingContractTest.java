package com.fabricioaraujo.api.tests;

import com.fabricioaraujo.api.data.BookingFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Testes de contrato: garantem que a estrutura do JSON (campos, tipos, obrigatoriedade)
 * não mudou, mesmo que os valores mudem. Pegam quebras que um teste de valor não pega.
 */
@Tag("contrato")
@Tag("regressao")
@DisplayName("API - Contrato (JSON Schema)")
class BookingContractTest extends BaseApiTest {

    @Test
    @DisplayName("POST /booking respeita o schema")
    void contratoCriacao() {
        var resp = client.criar(BookingFactory.valida());
        resp.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-schema.json"));

        client.excluir(resp.jsonPath().getInt("bookingid"), client.gerarToken());
    }

    @Test
    @DisplayName("GET /booking/{id} respeita o schema")
    void contratoBusca() {
        int id = client.criar(BookingFactory.valida()).then().statusCode(200)
                .extract().jsonPath().getInt("bookingid");

        client.buscar(id).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));

        client.excluir(id, client.gerarToken());
    }

    @Test
    @DisplayName("GET /booking respeita o schema da lista")
    void contratoListagem() {
        client.listar().then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-list-schema.json"));
    }
}
