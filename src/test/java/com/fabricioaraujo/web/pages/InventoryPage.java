package com.fabricioaraujo.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class InventoryPage extends BasePage {

    private static final By TITULO = dataTest("title");
    private static final By BADGE_CARRINHO = dataTest("shopping-cart-badge");
    private static final By LINK_CARRINHO = dataTest("shopping-cart-link");
    private static final By ORDENACAO = dataTest("product-sort-container");
    private static final By PRECOS = dataTest("inventory-item-price");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String titulo() {
        return texto(TITULO);
    }

    /** Converte "Sauce Labs Backpack" no id usado pelo site: add-to-cart-sauce-labs-backpack. */
    private static String slug(String produto) {
        return produto.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    public void adicionar(String produto) {
        clicar(dataTest("add-to-cart-" + slug(produto)));
    }

    public void remover(String produto) {
        clicar(dataTest("remove-" + slug(produto)));
    }

    public int quantidadeNoCarrinho() {
        List<WebElement> badge = driver.findElements(BADGE_CARRINHO);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText());
    }

    public void ordenarPor(String opcaoVisivel) {
        new Select(visivel(ORDENACAO)).selectByVisibleText(opcaoVisivel);
    }

    public List<Double> precos() {
        visivel(PRECOS);
        return driver.findElements(PRECOS).stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "")))
                .toList();
    }

    public void abrirCarrinho() {
        clicar(LINK_CARRINHO);
    }
}
