package com.fabricioaraujo.api.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Configuração base das requisições à API Restful-Booker.
 * A URL pode ser trocada com -Dapi.baseUri=... (ex.: um ambiente local em Docker).
 */
public final class ApiConfig {

    public static final String BASE_URI =
            System.getProperty("api.baseUri", "https://restful-booker.herokuapp.com");

    public static final String USERNAME = System.getProperty("api.user", "admin");
    public static final String PASSWORD = System.getProperty("api.password", "password123");

    private ApiConfig() {
    }

    public static RequestSpecification spec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                // O REST Assured envia "application/json; charset=UTF-8" por padrão,
                // e o POST /booking recusa com 418 (ver docs/BUGS.md, BUG-003)
                .setConfig(RestAssuredConfig.config().encoderConfig(
                        EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();
    }
}
