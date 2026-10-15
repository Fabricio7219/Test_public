package com.fabricioaraujo.api.client;

import com.fabricioaraujo.api.config.ApiConfig;
import com.fabricioaraujo.api.model.Booking;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Camada de acesso aos endpoints. Os testes só chamam métodos daqui,
 * então uma mudança de rota ou de header é corrigida em um lugar só.
 */
public class BookingClient {

    public Response ping() {
        return given().spec(ApiConfig.spec()).get("/ping");
    }

    public Response autenticar(String usuario, String senha) {
        return given().spec(ApiConfig.spec())
                .body(Map.of("username", usuario, "password", senha))
                .post("/auth");
    }

    public String gerarToken() {
        return autenticar(ApiConfig.USERNAME, ApiConfig.PASSWORD)
                .then().statusCode(200)
                .extract().path("token");
    }

    public Response listar() {
        return given().spec(ApiConfig.spec()).get("/booking");
    }

    public Response listarPorNome(String firstname, String lastname) {
        return given().spec(ApiConfig.spec())
                .queryParam("firstname", firstname)
                .queryParam("lastname", lastname)
                .get("/booking");
    }

    public Response buscar(int id) {
        return given().spec(ApiConfig.spec()).get("/booking/{id}", id);
    }

    public Response criar(Object body) {
        return given().spec(ApiConfig.spec()).body(body).post("/booking");
    }

    public Response atualizar(int id, Booking booking, String token) {
        return given().spec(ApiConfig.spec())
                .cookie("token", token)
                .body(booking)
                .put("/booking/{id}", id);
    }

    public Response atualizarParcial(int id, Map<String, Object> campos, String token) {
        return given().spec(ApiConfig.spec())
                .cookie("token", token)
                .body(campos)
                .patch("/booking/{id}", id);
    }

    public Response excluir(int id, String token) {
        var req = given().spec(ApiConfig.spec());
        if (token != null) {
            req.cookie("token", token);
        }
        return req.delete("/booking/{id}", id);
    }
}
