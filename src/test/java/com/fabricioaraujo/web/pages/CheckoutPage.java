package com.fabricioaraujo.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    private static final By NOME = dataTest("firstName");
    private static final By SOBRENOME = dataTest("lastName");
    private static final By CEP = dataTest("postalCode");
    private static final By CONTINUAR = dataTest("continue");
    private static final By FINALIZAR = dataTest("finish");
    private static final By MENSAGEM_ERRO = dataTest("error");
    private static final By CONFIRMACAO = dataTest("complete-header");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void preencherDados(String nome, String sobrenome, String cep) {
        if (!nome.isEmpty()) preencher(NOME, nome);
        if (!sobrenome.isEmpty()) preencher(SOBRENOME, sobrenome);
        if (!cep.isEmpty()) preencher(CEP, cep);
        clicar(CONTINUAR);
    }

    public void finalizar() {
        clicar(FINALIZAR);
    }

    public String mensagemErro() {
        return texto(MENSAGEM_ERRO);
    }

    public String mensagemConfirmacao() {
        return texto(CONFIRMACAO);
    }
}
