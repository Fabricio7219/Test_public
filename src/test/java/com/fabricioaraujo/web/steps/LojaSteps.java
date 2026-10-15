package com.fabricioaraujo.web.steps;

import com.fabricioaraujo.web.driver.DriverFactory;
import com.fabricioaraujo.web.pages.CartPage;
import com.fabricioaraujo.web.pages.CheckoutPage;
import com.fabricioaraujo.web.pages.InventoryPage;
import com.fabricioaraujo.web.pages.LoginPage;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

public class LojaSteps {

    private static final String SENHA_PADRAO = "secret_sauce";

    private LoginPage login() {
        return new LoginPage(DriverFactory.get());
    }

    private InventoryPage produtos() {
        return new InventoryPage(DriverFactory.get());
    }

    private CartPage carrinho() {
        return new CartPage(DriverFactory.get());
    }

    private CheckoutPage checkout() {
        return new CheckoutPage(DriverFactory.get());
    }

    // ---------- Login ----------

    @Dado("que estou na página de login")
    public void queEstouNaPaginaDeLogin() {
        login().abrir();
    }

    @Dado("que estou logado como {string}")
    public void queEstouLogadoComo(String usuario) {
        login().abrir().entrar(usuario, SENHA_PADRAO);
        assertThat(produtos().titulo()).isEqualTo("Products");
    }

    @Quando("faço login com o usuário {string} e a senha {string}")
    public void facoLogin(String usuario, String senha) {
        login().entrar(usuario, senha);
    }

    @Então("vejo a página de produtos")
    public void vejoAPaginaDeProdutos() {
        assertThat(produtos().titulo()).isEqualTo("Products");
    }

    @Então("vejo a mensagem de erro {string}")
    public void vejoAMensagemDeErro(String mensagem) {
        assertThat(login().mensagemErro()).isEqualTo(mensagem);
    }

    // ---------- Carrinho ----------

    @Quando("adiciono o produto {string}")
    public void adicionoOProduto(String produto) {
        produtos().adicionar(produto);
    }

    @Quando("removo o produto {string}")
    public void removoOProduto(String produto) {
        produtos().remover(produto);
    }

    @Então("o carrinho mostra {int} itens")
    public void oCarrinhoMostraItens(int quantidade) {
        assertThat(produtos().quantidadeNoCarrinho()).isEqualTo(quantidade);
    }

    @Então("o carrinho contém {string} e {string}")
    public void oCarrinhoContem(String produto1, String produto2) {
        produtos().abrirCarrinho();
        assertThat(carrinho().itens()).containsExactlyInAnyOrder(produto1, produto2);
    }

    @Quando("ordeno os produtos por {string}")
    public void ordenoOsProdutosPor(String opcao) {
        produtos().ordenarPor(opcao);
    }

    @Então("os preços aparecem em ordem crescente")
    public void osPrecosAparecemEmOrdemCrescente() {
        assertThat(produtos().precos()).isSortedAccordingTo(Comparator.naturalOrder());
    }

    // ---------- Checkout ----------

    @Quando("vou para o checkout")
    public void vouParaOCheckout() {
        produtos().abrirCarrinho();
        carrinho().irParaCheckout();
    }

    @Quando("informo nome {string}, sobrenome {string} e CEP {string}")
    public void informoDados(String nome, String sobrenome, String cep) {
        checkout().preencherDados(nome, sobrenome, cep);
    }

    @Quando("finalizo o pedido")
    public void finalizoOPedido() {
        checkout().finalizar();
    }

    @Então("vejo a confirmação {string}")
    public void vejoAConfirmacao(String mensagem) {
        assertThat(checkout().mensagemConfirmacao()).isEqualTo(mensagem);
    }

    @Então("vejo o erro de checkout {string}")
    public void vejoOErroDeCheckout(String mensagem) {
        assertThat(checkout().mensagemErro()).isEqualTo(mensagem);
    }
}
