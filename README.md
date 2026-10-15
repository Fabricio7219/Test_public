# QA Automation — Java

[![Testes automatizados](https://github.com/Fabricio7219/Test_public/actions/workflows/testes.yml/badge.svg)](https://github.com/Fabricio7219/Test_public/actions/workflows/testes.yml)

Projeto de automação de testes de **API**, **aplicação web** e **performance**, com execução contínua em **pipeline de CI/CD** no GitHub Actions.

| Camada | Sistema testado | Ferramentas |
| --- | --- | --- |
| API REST | [Restful-Booker](https://restful-booker.herokuapp.com) | Java 17, REST Assured, JUnit 5, JSON Schema |
| Web | [SauceDemo](https://www.saucedemo.com) | Selenium WebDriver, Cucumber (BDD em português), Page Objects |
| Performance | Restful-Booker | k6 (teste de carga com critérios de aceite) |
| CI/CD | GitHub Actions | Execução em push, PR, agendada diária e manual por tag |

## O que é testado

**API — Restful-Booker**
- Autenticação: geração de token válido e recusa de credenciais inválidas
- CRUD completo de reservas: criar, buscar, filtrar, atualizar (PUT e PATCH) e excluir
- Segurança: alteração e exclusão bloqueadas sem token
- Contrato: estrutura das respostas validada com JSON Schema

**Web — SauceDemo**
- Login com usuário válido, bloqueado, senha errada e campos vazios
- Carrinho: adicionar, remover e ordenar produtos por preço
- Checkout: compra concluída e validação dos campos obrigatórios

**Performance — Restful-Booker (k6)**
- Carga de até 5 usuários simultâneos por 50 segundos, cada um criando, buscando e excluindo uma reserva
- Critérios de aceite: taxa de erro abaixo de 1% e p95 do tempo de resposta abaixo de 2 s no geral, com limites próprios por endpoint
- Se algum critério falhar, o pipeline fica vermelho; o resumo com média, mediana, p95 e máximo por endpoint aparece na página do run
- A carga é baixa de propósito, porque a API é pública: o objetivo é medir, não derrubar o serviço

## Estratégia de testes

Cada cenário é marcado com tags, que definem quando ele roda:

| Tag | Uso |
| --- | --- |
| `smoke` | Caminho crítico, roda rápido para validar que o sistema está de pé |
| `regressao` | Suíte completa, roda a cada alteração e diariamente |
| `contrato` | Valida a estrutura do JSON da API |
| `api` / `web` | Separa as camadas |

Os cenários foram priorizados por **risco**: login, criação de reserva e finalização da compra são cobertos primeiro e entram no smoke, porque uma falha neles impede o uso do sistema.

Boas práticas aplicadas:
- **Page Objects** e **camada de client** para a API: uma mudança na tela ou no endpoint é corrigida em um lugar só
- **Esperas explícitas**, sem `Thread.sleep`
- **Massa de dados gerada a cada teste** e removida no final, sem depender de registros existentes
- **Print da tela anexado** ao relatório quando um cenário web falha
- Defeitos encontrados registrados em [`docs/BUGS.md`](docs/BUGS.md)

## Como executar

Pré-requisitos: Java 17+, Maven 3.9+ e Google Chrome.

```bash
# Tudo
mvn test

# Só o smoke (API + web)
mvn test -Dgroups=smoke

# Só a API
mvn test -Dgroups=api

# Web com o navegador visível
mvn test -Dtest=RunCucumberTest -Dheadless=false

# Performance (requer k6 instalado: https://grafana.com/docs/k6/latest/set-up/install-k6/)
k6 run perf/booking-load.js
```

Relatórios gerados em `target/`:
- `cucumber-report.html` — cenários BDD
- `surefire-reports/` — resultado JUnit
- `allure-results/` — testes de API, gerar com `allure serve target/allure-results`

## Pipeline

O workflow [`testes.yml`](.github/workflows/testes.yml) roda API, web e performance em jobs paralelos:
- a cada **push** e **pull request** na `main`
- **diariamente às 9h** (regressão agendada)
- **manualmente**, escolhendo a tag em *Actions → Testes automatizados → Run workflow*

Os relatórios de cada execução ficam disponíveis como artefatos do workflow.

## Estrutura

```
src/test/java/com/fabricioaraujo
├── api
│   ├── client/      # chamadas aos endpoints
│   ├── config/      # URL base e especificação das requisições
│   ├── data/        # geração de massa de dados
│   ├── model/       # objetos de requisição e resposta
│   └── tests/       # testes JUnit
└── web
    ├── driver/      # criação do WebDriver
    ├── pages/       # Page Objects
    ├── steps/       # passos do Cucumber e hooks
    └── RunCucumberTest.java
src/test/resources
├── features/        # cenários em Gherkin (português)
└── schemas/         # JSON Schemas de contrato
perf/
└── booking-load.js  # teste de carga k6
```

## Autor

**Fabrício Araújo** — [LinkedIn](https://www.linkedin.com/in/fabricioaraujo-qa) · [GitHub](https://github.com/Fabricio7219)
