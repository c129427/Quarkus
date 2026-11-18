# Documento Mestre do Projeto

## Propósito e continuidade

Este documento mantém o contexto técnico e pedagógico da apostila da aplicação `quarkus-primeiro-projeto`. A entrega atual foi reestruturada como tutorial passo a passo para recriar a aplicação a partir de uma pasta limpa: `Guia-Passo-a-Passo-Quarkus.docx`. O tutorial contém o código de produção, os testes e uma seção de auditoria resumida; o relatório detalhado original está em `Auditoria-Tecnica-Detalhada.docx`. Não avançar para capítulos adicionais sem autorização do usuário.

## Identificação e objetivo do sistema

O repositório contém uma aplicação Quarkus que expõe uma API REST para cadastro e consulta de pessoas, endereços opcionais e rendas mensais. O contrato usa JSON; CPF é normalizado para dígitos, e datas são representadas por `LocalDate`.

## Ambiente e tecnologias observadas

| Item | Valor |
|---|---|
| JDK usado na validação | Oracle JDK 17.0.12 |
| Maven Wrapper | Apache Maven 3.9.16 |
| Versão do wrapper | 3.3.4 |
| Quarkus | 3.40.1.1 |
| Java release no POM | 17 |
| Persistência | Hibernate ORM/Panache e H2 |
| Testes | JUnit 5, Mockito, `@QuarkusTest`, RestAssured e Failsafe |

O build foi executado com o JDK 17.0.12 informado e confirmou a resolução do Maven 3.9.16 pelo Wrapper.

## Arquitetura e fluxo

```text
Cliente HTTP/JSON
       -> resource REST
       -> service (casos de uso e transações)
       -> repository Panache
       -> Hibernate ORM/JPA
       -> H2
```

Resources coordenam o transporte HTTP e convertem DTOs. Services aplicam regras de negócio e transações. Repositories concentram consultas Panache. Models são entidades JPA. Mappers convertem exceções selecionadas em respostas HTTP.

## Inventário

- Um projeto Maven de aplicação; não há agregador ou módulos Maven filhos.
- 28 classes Java de produção e 31 arquivos Java de teste.
- O guia reproduz os arquivos de produção, configuração Maven e mensagens, além das 31 classes de teste, nos passos e apêndices correspondentes.
- Pacotes de produção: `com.giba` (2), `dto` (7), `exception` (5), `model` (4), `repository` (2), `resource` (4), `service` (2), `validation` (2).
- Nove dependências diretas: REST/Jackson, Arc, Hibernate ORM Panache, JDBC H2, Hibernate Validator, SmallRye OpenAPI, Quarkus JUnit, Quarkus JUnit Mockito e RestAssured.
- Quatro plugins no build: Quarkus, Compiler, Surefire e Failsafe. Um perfil Maven (`native`).
- Documentação consultada: `README.md`, `Arquitetura.md` e `.github/copilot-instructions.md`.
- Recursos: `application.properties` e `messages.properties`; quatro Dockerfiles e `.dockerignore`.
- Não existem no projeto Docker Compose, Jenkinsfile, workflow do GitHub Actions, manifestos Kubernetes nem scripts SQL.
- `target/` e arquivos locais de H2 são artefatos/estado local, não fontes de referência da apostila.

## Organização e responsabilidade dos pacotes

| Pacote | Responsabilidade |
|---|---|
| `com.giba` | Catálogo de códigos de mensagens e resolução de mensagens. |
| `com.giba.dto` | Records para contratos de request/response e erros. |
| `com.giba.exception` | Exceções de negócio e `ExceptionMapper`s. |
| `com.giba.model` | Entidades persistentes e enum de situação. |
| `com.giba.repository` | Consultas por CPF, período de renda e pessoa. |
| `com.giba.resource` | Endpoints REST, conversão de parâmetros e respostas HTTP. |
| `com.giba.service` | Regras de negócio, transações, normalização e logging. |
| `com.giba.validation` | Constraint e implementação de validação/normalização de CPF. |

## Regras de negócio observadas

- CPF deve ser válido e único; é persistido sem pontuação.
- Pessoa tem situação `ATIVA` ou `INATIVA` e pode ter um endereço.
- Endereço é associado em relação um-para-um e removido como órfão.
- Renda pertence a uma pessoa e é única por pessoa, mês e ano.
- Renda não pode ser negativa; pessoa com rendas associadas não pode ser excluída.
- O código usa DTOs distintos das entidades e mapeia validação para HTTP 400, conflito de negócio para 409 e ausência de entidade para 404.

## Configuração persistente observada

O perfil padrão configura H2 em arquivo e `quarkus.hibernate-orm.schema-management.strategy=update`. `%test` usa H2 em memória com `drop-and-create`; `%dev` habilita HQL no Dev UI. Não há migrações de esquema no projeto nem uma configuração explícita de datasource de produção. O README adverte que persistência e estratégia de esquema devem ser revistas antes de produção.

## Estratégia e resultado dos testes

Preservar JUnit 5, Mockito, `@QuarkusTest`, RestAssured e a organização de testes existente. Na validação, `mvnw.cmd verify -DskipITs=false` terminou com `BUILD SUCCESS`. O Surefire executou 38 testes e o Failsafe, 4 testes de integração; ambos sem falhas nem erros.

## Achados da auditoria — Capítulo 1

| ID | Severidade | Tema | Referências principais |
|---|---|---|---|
| A-01 | Alta | Endpoints de dados pessoais sem autenticação/autorização; risco condicionado à exposição em rede. | `PessoaResource.java`, `RendaResource.java`, `README.md` |
| A-02 | Média | Configuração padrão de H2 em arquivo e atualização automática de esquema também usada fora de `%test`. | `application.properties`, `README.md` |
| A-03 | Média | Listagens sem paginação/limite de resultados. | `PessoaService.java`, `RendaService.java` |
| A-04 | Baixa | Verificação de duplicidade sofre condição de corrida; falha da constraint concorrente não é convertida no conflito de domínio. | Services, models e mappers |
| A-05 | Baixa | Guava 33.6.0-jre transitivo de escopo `test` aparece afetado pelo GHSA-xxph-c9ww-hj94/CVE-2026-102554; sem uso de desserialização Java no código auditado. | `pom.xml`, árvore Maven, advisory OSV/GitHub |

Não houve achado classificado como crítico. O detalhamento, impacto, confiança, recomendações, limitações de escopo e referências do relatório estão no DOCX do Capítulo 1. Os avisos do `dependency:analyze` sobre extensões Quarkus não foram considerados prova de dependência sem uso: o analisador não detecta usos determinados por configuração e metadados de extensão. A árvore Maven não mostrou versões omitidas por conflito.

## Avaliação de dependências e limites

A árvore resolveu 293 coordenadas Maven distintas, consultadas em lote na base OSV. Foi encontrado um advisory para Guava, dependência transitiva apenas de testes; a aplicação não contém chamadas a `ObjectInputStream` no código-fonte consultado. O advisory indica correção em Guava 33.7.2. A severidade do relatório considera o contexto e a alcançabilidade observados, além da classificação publicada pelo advisory.

A análise de dependências não equivale a prova de ausência de vulnerabilidades. A consulta depende da cobertura e atualização do OSV e não avaliou imagem de container, biblioteca nativa ou vulnerabilidade que não esteja indexada para coordenadas Maven.

## Mapeamento pedagógico para certificações

- **1Z0-808**: relacionar somente fundamentos de Java SE 8 que também aparecem no projeto, como classes, interfaces, exceções, collections, lambdas e streams. Não atribuir records ou recursos posteriores ao escopo desse exame.
- **1Z0-829**: prioridade de alinhamento para a linguagem configurada no projeto, Java SE 17; abordar records, APIs de data/hora, collections, streams, generics e exceções quando encontrados.
- **1Z0-830**: distinguir conteúdo de Java SE 21 do código compilado para release 17. Não afirmar que o projeto usa recursos exclusivos do Java 21 apenas por uma dependência transitiva.

Questões de prática devem ser originais e identificadas como “estilo certificação”, nunca apresentadas como questões oficiais Oracle. Não foi possível confirmar os objetivos oficiais porque as páginas da Oracle não responderam durante a auditoria; por isso, o mapeamento é apenas didático e deve ser conferido nas fontes oficiais.

## Capítulos futuros

1. Auditoria Técnica — concluída; síntese no Apêndice A do guia e relatório detalhado em `Auditoria-Tecnica-Detalhada.docx`.
2. Visão Geral do Projeto.
3. Estrutura de Diretórios.
4. Maven.
5. Quarkus.
6. Classes de Domínio.
7. Classes de Serviço.
8. Endpoints REST.
9. Persistência.
10. Fluxo de Execução.
11. Testes.
12. Certificações Oracle.
13. Boas Práticas.
14. Plano de Estudos.
15. Glossário.
16. Índice Remissivo.

Antes de cada capítulo futuro, consultar este documento e o material já produzido, manter a continuidade e atualizar o resumo e o status ao concluir. A produção está pausada após a entrega do tutorial; só continuar com autorização do usuário.
