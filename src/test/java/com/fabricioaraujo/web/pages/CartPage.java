package com.fabricioaraujo.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    private static final By NOMES_ITENS = dataTest("inventory-item-name");
    private static final By BOTAO_CHECKOUT = dataTest("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<String> itens() {
        visivel(BOTAO_CHECKOUT);
        return driver.findElements(NOMES_ITENS).stream().map(WebElement::getText).toList();
    }

    public void irParaCheckout() {
        clicar(BOTAO_CHECKOUT);
    }
}
