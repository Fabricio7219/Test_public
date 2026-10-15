package com.fabricioaraujo.web.steps;

import com.fabricioaraujo.web.driver.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    @Before
    public void abrirNavegador() {
        DriverFactory.iniciar();
    }

    /** Em caso de falha, anexa um print da tela ao relatório: evidência para o registro do defeito. */
    @After
    public void fecharNavegador(Scenario cenario) {
        try {
            if (cenario.isFailed() && DriverFactory.get() != null) {
                byte[] print = ((TakesScreenshot) DriverFactory.get()).getScreenshotAs(OutputType.BYTES);
                cenario.attach(print, "image/png", "evidencia-falha");
            }
        } finally {
            DriverFactory.encerrar();
        }
    }
}
