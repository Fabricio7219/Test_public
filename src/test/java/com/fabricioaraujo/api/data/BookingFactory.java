package com.fabricioaraujo.api.data;

import com.fabricioaraujo.api.model.Booking;
import com.fabricioaraujo.api.model.Booking.BookingDates;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gera massa de dados para os testes.
 * Cada reserva tem dados aleatórios para não depender de registros existentes na API pública.
 */
public final class BookingFactory {

    private static final List<String> NOMES = List.of("Ana", "Bruno", "Carla", "Diego", "Elisa", "Fabio");
    private static final List<String> SOBRENOMES = List.of("Silva", "Souza", "Oliveira", "Lima", "Costa");

    private BookingFactory() {
    }

    public static Booking valida() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        LocalDate checkin = LocalDate.now().plusDays(r.nextInt(1, 60));
        return new Booking(
                NOMES.get(r.nextInt(NOMES.size())),
                SOBRENOMES.get(r.nextInt(SOBRENOMES.size())) + "-QA" + r.nextInt(1000, 9999),
                r.nextInt(100, 2000),
                r.nextBoolean(),
                new BookingDates(checkin.toString(), checkin.plusDays(r.nextInt(1, 10)).toString()),
                "Cafe da manha");
    }

    public static Booking comNovoPreco(Booking original, int novoPreco) {
        return new Booking(original.firstname(), original.lastname(), novoPreco,
                original.depositpaid(), original.bookingdates(), original.additionalneeds());
    }
}
