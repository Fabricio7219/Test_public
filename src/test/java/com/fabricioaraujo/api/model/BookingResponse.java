package com.fabricioaraujo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Resposta do POST /booking: id gerado + reserva criada. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BookingResponse(int bookingid, Booking booking) {
}
