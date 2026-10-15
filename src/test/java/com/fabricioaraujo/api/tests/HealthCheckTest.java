package com.fabricioaraujo.api.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("API - Health check")
class HealthCheckTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @DisplayName("API responde no endpoint /ping")
    void apiDisponivel() {
        // A API retorna 201 no /ping (comportamento documentado do Restful-Booker)
        client.ping().then().statusCode(201);
    }
}
