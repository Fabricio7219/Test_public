# language: pt
@web
Funcionalidade: Carrinho de compras
  Como cliente logado
  Quero gerenciar os produtos do meu carrinho
  Para comprar só o que eu escolher

  Contexto:
    Dado que estou logado como "standard_user"

  @smoke @regressao
  Cenário: Adicionar produtos ao carrinho
    Quando adiciono o produto "Sauce Labs Backpack"
    E adiciono o produto "Sauce Labs Bike Light"
    Então o carrinho mostra 2 itens
    E o carrinho contém "Sauce Labs Backpack" e "Sauce Labs Bike Light"

  @regressao
  Cenário: Remover produto do carrinho
    Quando adiciono o produto "Sauce Labs Backpack"
    E removo o produto "Sauce Labs Backpack"
    Então o carrinho mostra 0 itens

  @regressao
  Cenário: Ordenar produtos pelo menor preço
    Quando ordeno os produtos por "Price (low to high)"
    Então os preços aparecem em ordem crescente
