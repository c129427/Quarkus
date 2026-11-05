# Arquitetura da aplicação

## Visão geral

O repositório contém a aplicação `quarkus-primeiro-projeto`, uma API REST para gestão de pessoas, endereços e rendas mensais. A implementação usa Java 17, Quarkus 3.40.1.1 e Maven. O código de produção fica em `quarkus-primeiro-projeto/src/main/java/com/giba`; a configuração e os recursos ficam em `src/main/resources`.

A arquitetura separa transporte HTTP, regras de negócio, acesso a dados e modelo persistente. O fluxo principal é:

```text
Cliente HTTP/JSON
       |
       v
Resources (JAX-RS, validação de entrada)
       |
       v
Services (regras de negócio, transações, logging)
       |
       v
Repositories (Panache)
       |
       v
Hibernate ORM / JPA
       |
       v
H2
```

Os DTOs definem os formatos de entrada e saída da API. Exceções específicas e mappers convertem alguns erros da aplicação em respostas HTTP.

## Organização do código

| Package | Responsabilidade |
|---|---|
| `com.giba.resource` | Endpoints REST, roteamento, status HTTP e coordenação entre requisições e serviços. |
| `com.giba.dto` | Records de requisição e resposta; não expõem diretamente as entidades JPA pela API. |
| `com.giba.service` | Casos de uso, regras de negócio, transações e logs das operações. |
| `com.giba.repository` | Consultas e operações de persistência com `PanacheRepository`. |
| `com.giba.model` | Entidades JPA e enumerações do domínio. |
| `com.giba.validation` | Anotação e validador de CPF. |
| `com.giba.exception` | Exceções de negócio e mappers de erro REST. |
| `com.giba` | Utilitários compartilhados para códigos e conteúdo de mensagens. |

`HelloResource` mantém o endpoint de demonstração `GET /hello`.

## API e fluxo das requisições

`PessoaResource` e `RendaResource` implementam os endpoints JAX-RS sob `/api`. Os recursos recebem DTOs JSON, aplicam validação declarativa quando definida e delegam as operações para os serviços. Os serviços retornam entidades, que são convertidas em DTOs de resposta antes da serialização JSON.

| Método | Caminho | Operação |
|---|---|---|
| `POST` | `/api/pessoas` | Cadastrar pessoa. |
| `GET` | `/api/pessoas` | Listar pessoas ordenadas por nome. |
| `GET` | `/api/pessoas/{id}` | Consultar pessoa por ID. |
| `PUT` | `/api/pessoas/{id}` | Atualizar pessoa. |
| `DELETE` | `/api/pessoas/{id}` | Excluir pessoa, desde que não tenha rendas associadas. |
| `PUT` | `/api/pessoas/{id}/endereco` | Criar ou atualizar endereço. |
| `DELETE` | `/api/pessoas/{id}/endereco` | Remover endereço. |
| `POST` | `/api/pessoas/{cpf}/rendas` | Cadastrar renda mensal. |
| `GET` | `/api/pessoas/{cpf}/rendas` | Listar rendas da pessoa. |
| `PUT` | `/api/pessoas/{cpf}/rendas/registro/{id}` | Atualizar renda. |
| `DELETE` | `/api/pessoas/{cpf}/rendas/registro/{id}` | Excluir renda. |

O contrato usa JSON; as datas são representadas como `yyyy-MM-dd`. CPFs recebidos com ou sem pontuação são normalizados para somente dígitos antes da persistência e nas respostas.

## Domínio e persistência

O modelo contém três entidades:

- **Pessoa**: CPF único, nome, data de nascimento, situação (`ATIVA` ou `INATIVA`) e endereço opcional.
- **Endereço**: dados postais associados a uma pessoa.
- **Renda**: valor decimal, mês, ano e pessoa proprietária.

Pessoa e endereço têm associação um-para-um. A associação usa cascata e remoção de órfão; assim, o endereço acompanha o ciclo de vida da pessoa. Renda tem associação muitos-para-um com pessoa. A persistência reforça as unicidades de CPF e da combinação pessoa/mês/ano.

Os repositórios estendem `PanacheRepository` e concentram consultas como busca por CPF, listagem de rendas por pessoa e verificação de duplicidade no período. Os serviços usam `@Transactional` nos casos de uso que leem ou alteram dados.

O banco configurado é H2:

- No perfil padrão, usa armazenamento em arquivo `./gestao-empresas`; o Hibernate está configurado com estratégia `update`.
- No perfil `%test`, usa H2 em memória e recria o esquema (`drop-and-create`).

Não há configuração de migrações de esquema no projeto. A estratégia atual de atualização automática é uma configuração do projeto e deve ser avaliada antes de uso em produção.

## Validação, regras e erros

Os DTOs usam Bean Validation para campos obrigatórios, tamanhos, datas, faixas numéricas, precisão decimal e formatos de estado e CEP. A anotação `@CpfValido` conecta a validação de CPF ao `CpfValidator`, que também fornece normalização e validação reutilizadas pelos serviços.

As regras de negócio incluem:

- impedir cadastro de CPF duplicado;
- impedir mais de uma renda para a mesma pessoa no mesmo mês e ano;
- rejeitar renda negativa;
- impedir exclusão de pessoa que ainda tenha rendas associadas;
- normalizar dados de CPF e endereço antes de persistir.

`ConflitoException` e `EntidadeNaoEncontradaException` são tratadas, respectivamente, por `ConflitoMapper` (HTTP 409) e `EntidadeNaoEncontradaMapper` (HTTP 404). `ValidacaoMapper` transforma violações de Bean Validation em HTTP 400, ordenando as mensagens no campo `mensagem` de `ErroResponse`.

Os parâmetros de caminho que representam IDs são recebidos como texto e convertidos por `IdPathParam`. Um ID que não possa ser convertido, como `:1`, produz HTTP 400 com JSON `ErroResponse` e a mensagem de campo inválido. Essa conversão explícita evita que o conversor padrão de parâmetros REST responda antes da lógica da aplicação. Os demais erros seguem os mappers específicos ou o tratamento padrão do Quarkus; não há mapper global para todas as exceções.

## Mensagens da aplicação

`src/main/resources/messages.properties` centraliza mensagens categorizadas por códigos. `CodigoMensagem` declara constantes para esses códigos, e `Mensagens` usa `ResourceBundle` e `MessageFormat` para recuperar e parametrizar o texto. Uma chave ausente é convertida em `IllegalArgumentException` com identificação clara do código.

O catálogo também reserva códigos de sucesso, autenticação/autorização e erros. A presença dessas mensagens não significa que autenticação, autorização ou um tratamento global de erros estejam implementados. Atualmente, mensagens centralizadas são usadas nos serviços, na validação de CPF e na validação explícita do ID de caminho.

## Logging

A aplicação usa `org.jboss.logging.Logger`, integrado ao logging do Quarkus; não usa Apache Log4j 2 diretamente nem adiciona uma dependência separada para logging. `application.properties` configura nível `INFO` para `com.giba` e define o formato do console.

`PessoaService` e `RendaService` registram operações de cadastro, atualização e exclusão com identificadores e, para rendas, período. Os logs não incluem CPF, nome nem valor monetário da renda.

## Configuração e documentação da API

`application.properties` configura datasource, Hibernate ORM, logging e OpenAPI. O OpenAPI informa o título e a versão da API; o Swagger UI é incluído também fora do perfil de desenvolvimento. Configurações específicas do perfil de teste e de desenvolvimento são prefixadas com `%test` e `%dev`.

## Testes

A estratégia usa JUnit 5, Mockito, `@QuarkusTest` e RestAssured. Testes unitários verificam classes de produção isoladamente; testes de recurso cobrem as rotas HTTP com a aplicação Quarkus.

As instruções do projeto também estabelecem uma classe de teste para cada classe de produção e não permitem mudar essa estratégia sem solicitação explícita.

`GestaoApiTestBase` contém cenários compartilhados da API. `GestaoApiTest`, anotada com `@QuarkusTest`, executa esses cenários no ambiente de teste do Quarkus. `GestaoApiIT`, anotada com `@QuarkusIntegrationTest`, executa-os contra o artefato empacotado; a classe-base não possui extensão de teste, evitando ativar simultaneamente os dois modos.

Comandos do Maven Wrapper no Windows:

```powershell
.\quarkus-primeiro-projeto\mvnw.cmd -f .\quarkus-primeiro-projeto\pom.xml test
.\quarkus-primeiro-projeto\mvnw.cmd -f .\quarkus-primeiro-projeto\pom.xml verify -DskipITs=false
```

O segundo comando inclui os testes de integração executados pelo Failsafe.

## Limites atuais

Não estão implementados autenticação ou autorização, paginação, migrações de banco, internacionalização, respostas REST globalmente padronizadas nem tratamento global de exceções. Essas capacidades não devem ser inferidas a partir das dependências presentes ou das chaves reservadas no catálogo de mensagens.
