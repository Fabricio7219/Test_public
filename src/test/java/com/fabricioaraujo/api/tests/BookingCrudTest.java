package com.fabricioaraujo.api.tests;

import com.fabricioaraujo.api.data.BookingFactory;
import com.fabricioaraujo.api.model.Booking;
import com.fabricioaraujo.api.model.BookingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("API - CRUD de reservas")
class BookingCrudTest extends BaseApiTest {

    private String token;
    private Integer idCriado;

    @BeforeEach
    void setUp() {
        token = client.gerarToken();
    }

    /** Limpa a massa criada para não acumular lixo na API pública. */
    @AfterEach
    void limpar() {
        if (idCriado != null) {
            client.excluir(idCriado, token);
        }
    }

    private BookingResponse criarReserva(Booking booking) {
        BookingResponse resp = client.criar(booking)
                .then().statusCode(200)
                .extract().as(BookingResponse.class);
        idCriado = resp.bookingid();
        return resp;
    }

    @Test
    @Tag("smoke")
    @Tag("regressao")
    @DisplayName("Cria reserva e retorna os mesmos dados enviados")
    void criaReserva() {
        Booking enviada = BookingFactory.valida();

        BookingResponse resp = criarReserva(enviada);

        assertThat(resp.bookingid()).isPositive();
        assertThat(resp.booking()).isEqualTo(enviada);
    }

    @Test
    @Tag("regressao")
    @DisplayName("Busca por id retorna a reserva criada")
    void buscaReservaPorId() {
        Booking enviada = BookingFactory.valida();
        int id = criarReserva(enviada).bookingid();

        Booking retornada = client.buscar(id)
                .then().statusCode(200)
                .extract().as(Booking.class);

        assertThat(retornada).isEqualTo(enviada);
    }

    @Test
    @Tag("regressao")
    @DisplayName("Filtro por nome encontra a reserva criada")
    void filtraReservaPorNome() {
        Booking enviada = BookingFactory.valida();
        int id = criarReserva(enviada).bookingid();

        client.listarPorNome(enviada.firstname(), enviada.lastname())
                .then()
                .statusCode(200)
                .body("bookingid", hasItem(id));
    }

    @Test
    @Tag("regressao")
    @DisplayName("Atualiza reserva inteira (PUT) com token")
    void atualizaReserva() {
        Booking original = BookingFactory.valida();
        int id = criarReserva(original).bookingid();
        Booking alterada = BookingFactory.comNovoPreco(original, 999);

        client.atualizar(id, alterada, token)
                .then()
                .statusCode(200)
                .body("totalprice", equalTo(999))
                .body("firstname", equalTo(original.firstname()));
    }

    @Test
    @Tag("regressao")
    @DisplayName("Atualiza só um campo (PATCH) sem alterar os demais")
    void atualizaParcial() {
        Booking original = BookingFactory.valida();
        int id = criarReserva(original).bookingid();

        client.atualizarParcial(id, Map.of("additionalneeds", "Late checkout"), token)
                .then()
                .statusCode(200)
                .body("additionalneeds", equalTo("Late checkout"))
                .body("lastname", equalTo(original.lastname()))
                .body("totalprice", equalTo(original.totalprice()));
    }

    @Test
    @Tag("regressao")
    @DisplayName("Não permite atualizar reserva sem token")
    void naoAtualizaSemToken() {
        Booking original = BookingFactory.valida();
        int id = criarReserva(original).bookingid();

        client.atualizar(id, BookingFactory.comNovoPreco(original, 1), "token-invalido")
                .then()
                .statusCode(403);
    }

    @Test
    @Tag("regressao")
    @DisplayName("Exclui reserva e ela deixa de existir")
    void excluiReserva() {
        int id = criarReserva(BookingFactory.valida()).bookingid();

        // A API responde 201 no DELETE em vez de 200/204 (ver docs/BUGS.md, BUG-002)
        client.excluir(id, token).then().statusCode(anyOf(is(200), is(201), is(204)));
        client.buscar(id).then().statusCode(404);

        idCriado = null; // já excluída
    }

    @Test
    @Tag("regressao")
    @DisplayName("Não permite excluir reserva sem token")
    void naoExcluiSemToken() {
        int id = criarReserva(BookingFactory.valida()).bookingid();

        client.excluir(id, null).then().statusCode(403);
        client.buscar(id).then().statusCode(200);
    }
}
