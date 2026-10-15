package com.fabricioaraujo.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Esperas explícitas centralizadas: nenhum Thread.sleep no projeto. */
public abstract class BasePage {

    protected static final String BASE_URL =
            System.getProperty("web.baseUrl", "https://www.saucedemo.com");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected WebElement visivel(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void clicar(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void preencher(By locator, String texto) {
        WebElement campo = visivel(locator);
        campo.clear();
        campo.sendKeys(texto);
    }

    protected String texto(By locator) {
        return visivel(locator).getText();
    }

    protected static By dataTest(String valor) {
        return By.cssSelector("[data-test='" + valor + "']");
    }
}
