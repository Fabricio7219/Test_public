# language: pt
@web
Funcionalidade: Finalização da compra
  Como cliente com produtos no carrinho
  Quero informar meus dados e finalizar o pedido
  Para concluir a compra

  Contexto:
    Dado que estou logado como "standard_user"
    E adiciono o produto "Sauce Labs Backpack"
    E vou para o checkout

  @smoke @regressao
  Cenário: Compra concluída com dados válidos
    Quando informo nome "Fabricio", sobrenome "Araujo" e CEP "06000-000"
    E finalizo o pedido
    Então vejo a confirmação "Thank you for your order!"

  @regressao
  Esquema do Cenário: Checkout bloqueado com dado obrigatório faltando
    Quando informo nome "<nome>", sobrenome "<sobrenome>" e CEP "<cep>"
    Então vejo o erro de checkout "<mensagem>"

    Exemplos:
      | nome     | sobrenome | cep       | mensagem                        |
      |          | Araujo    | 06000-000 | Error: First Name is required   |
      | Fabricio |           | 06000-000 | Error: Last Name is required    |
      | Fabricio | Araujo    |           | Error: Postal Code is required  |
