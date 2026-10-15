package com.fabricioaraujo.api.tests;

import com.fabricioaraujo.api.client.BookingClient;
import org.junit.jupiter.api.Tag;

@Tag("api")
public abstract class BaseApiTest {

    protected final BookingClient client = new BookingClient();
}
