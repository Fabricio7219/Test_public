package com.fabricioaraujo.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USUARIO = dataTest("username");
    private static final By SENHA = dataTest("password");
    private static final By BOTAO_LOGIN = dataTest("login-button");
    private static final By MENSAGEM_ERRO = dataTest("error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage abrir() {
        driver.get(BASE_URL);
        visivel(USUARIO);
        return this;
    }

    public void entrar(String usuario, String senha) {
        preencher(USUARIO, usuario);
        preencher(SENHA, senha);
        clicar(BOTAO_LOGIN);
    }

    public String mensagemErro() {
        return texto(MENSAGEM_ERRO);
    }
}
