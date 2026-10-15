package com.fabricioaraujo.web.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.Map;

/**
 * Cria e guarda um WebDriver por thread (permite rodar cenários em paralelo no futuro).
 * O Selenium Manager (embutido no Selenium 4) baixa o driver compatível com o Chrome instalado.
 *
 * Rodar com navegador visível: mvn test -Dheadless=false
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver iniciar() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "true"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1366,768", "--no-sandbox", "--disable-dev-shm-usage");
        // Evita o pop-up de "senha vazada" do Chrome, que trava o fluxo do SauceDemo
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver get() {
        return DRIVER.get();
    }

    public static void encerrar() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
