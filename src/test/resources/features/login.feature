# language: pt
@web
Funcionalidade: Login na loja
  Como cliente da loja
  Quero acessar minha conta
  Para ver os produtos e fazer compras

  Contexto:
    Dado que estou na página de login

  @smoke @regressao
  Cenário: Login com usuário válido
    Quando faço login com o usuário "standard_user" e a senha "secret_sauce"
    Então vejo a página de produtos

  @regressao
  Esquema do Cenário: Login recusado
    Quando faço login com o usuário "<usuario>" e a senha "<senha>"
    Então vejo a mensagem de erro "<mensagem>"

    Exemplos:
      | usuario         | senha        | mensagem                                                                  |
      | locked_out_user | secret_sauce | Epic sadface: Sorry, this user has been locked out.                       |
      | standard_user   | senha_errada | Epic sadface: Username and password do not match any user in this service |
      |                 | secret_sauce | Epic sadface: Username is required                                        |
      | standard_user   |              | Epic sadface: Password is required                                        |
