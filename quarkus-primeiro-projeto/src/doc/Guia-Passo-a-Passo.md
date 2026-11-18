# Do zero à API REST com Quarkus

## Guia prático do projeto Gestao de Empresas e Trabalhadores

**Ambiente:** Oracle JDK 17.0.12, Apache Maven 3.9.16 e Quarkus 3.40.1.1  
**Projeto:** `quarkus-primeiro-projeto`  
**Objetivo:** construir e compreender uma API REST com Java, Quarkus, validação, persistência e testes.

> Este guia parte de uma pasta vazia e progride até uma aplicação executável. O código completo de produção do projeto auditado está reproduzido junto às etapas relevantes. Use os passos como roteiro de aprendizagem; não execute a criação sobre uma pasta que contenha trabalho seu.

## Sumário

1. O que vamos construir
2. Preparar e verificar o ambiente
3. Criar o esqueleto Maven/Quarkus
4. Entender e configurar o `pom.xml`
5. Organizar os pacotes
6. Configurar a aplicação
7. Criar o modelo persistente
8. Definir os contratos HTTP com DTOs
9. Implementar a validação de CPF
10. Criar os repositórios
11. Implementar os casos de uso
12. Padronizar mensagens e erros HTTP
13. Expor os endpoints REST
14. Criar e executar os testes
15. Executar, documentar e empacotar
16. Relacionar o código às certificações Java
17. Roteiro de conferência e diagnóstico
18. Auditoria resumida do projeto de referência
19. Anexo A — Código completo de produção
20. Apêndice B — Código completo dos testes

## 1. O que vamos construir

Ao final, a API permitirá cadastrar pessoas, manter um endereço opcional e registrar rendas mensais. O fluxo de uma requisição será:

```text
Cliente HTTP e JSON
    -> resource REST: rota, status HTTP e DTO
    -> service: regras de negócio e transação
    -> repository Panache: consultas
    -> Hibernate ORM/JPA
    -> banco H2
```

O projeto também terá validação de dados, respostas de erro consistentes, testes unitários, testes HTTP com Quarkus e um teste de integração do artefato empacotado.

**Conceitos Java já encontrados:** classes, enumerações, records, interfaces, exceções, generics, `Optional`, collections, streams, lambdas, method references, `LocalDate` e `BigDecimal`. Quarkus, CDI, JAX-RS, Hibernate e JPA são tecnologias do ecossistema Java, não recursos da linguagem Java SE.

## 2. Preparar e verificar o ambiente

### 2.1 Confirmar as instalações

Abra o PowerShell e confira o JDK solicitado:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

O resultado esperado informa a versão `17.0.12`. Confirme também o Maven informado:

```powershell
& 'C:\Desenvolvimento\apache-maven-3.9.16\bin\mvn.cmd' -version
```

Na validação deste projeto, o Maven Wrapper também resolveu o Maven 3.9.16. O Wrapper é conveniente porque fixa a versão usada pelo projeto sem exigir que cada desenvolvedor configure o mesmo diretório de instalação.

### 2.2 Criar uma pasta de trabalho limpa

Escolha uma pasta descartável ou uma nova localização. Não gere o projeto por cima do repositório existente:

```powershell
$workspace = 'C:\Desenvolvimento\estudo-quarkus-novo'
if (Test-Path $workspace) { throw 'A pasta já existe. Escolha um caminho novo para não sobrescrever arquivos.' }
New-Item -ItemType Directory -Path $workspace | Out-Null
Set-Location $workspace
```

**Checkpoint:** `java -version` mostra JDK 17.0.12 e o comando Maven mostra Apache Maven 3.9.16.

## 3. Criar o esqueleto Maven/Quarkus

Gere um projeto vazio com o plugin Maven do Quarkus. O comando cria uma subpasta com o artifactId:

```powershell
& 'C:\Desenvolvimento\apache-maven-3.9.16\bin\mvn.cmd' `
  'io.quarkus.platform:quarkus-maven-plugin:3.40.1.1:create' `
  '-DprojectGroupId=br.com.giba' `
  '-DprojectArtifactId=quarkus-primeiro-projeto' `
  '-DprojectVersion=1.0.0-SNAPSHOT' `
  '-DpackageName=com.giba' `
  '-DjavaVersion=17' `
  '-Dextensions=rest-jackson,hibernate-orm-panache,jdbc-h2,hibernate-validator,smallrye-openapi'
```

Entre na pasta gerada e verifique a estrutura:

```powershell
Set-Location 'C:\Desenvolvimento\estudo-quarkus-novo\quarkus-primeiro-projeto'
Get-ChildItem
```

O esqueleto pode incluir uma resource `GreetingResource` e testes de exemplo. Remova os arquivos de exemplo gerados antes de inserir as classes deste guia: a resource padrão também pode mapear `/hello`, que será implementado pela classe `HelloResource`.

```powershell
Get-ChildItem src\main\java,src\test\java -Recurse -File |
  Where-Object { $_.Name -in @('GreetingResource.java','GreetingResourceTest.java','NativeGreetingResourceIT.java') } |
  Remove-Item
```

> Se estiver criando em outro diretório, ajuste `Set-Location` e os caminhos dos comandos seguintes. A versão do plugin é explícita para que a geração seja reproduzível.

O plugin gera uma estrutura inicial e configurações básicas. Compare o POM gerado com o arquivo de referência abaixo: a aplicação deste guia usa também dependências de teste e configurações explícitas para `maven-compiler-plugin`, Surefire e Failsafe.

## 4. Entender e configurar o `pom.xml`

O POM identifica a aplicação (`groupId`, `artifactId` e `version`), importa o BOM do Quarkus, lista extensões e define os plugins de compilação/teste. O BOM controla versões compatíveis das bibliotecas Quarkus. Não atribua versões independentes às extensões sem uma necessidade concreta.

Use o POM do projeto como referência completa:

[[ARQUIVO:quarkus-primeiro-projeto/pom.xml]]

### 4.1 Para que serve cada extensão

| Dependência | Responsabilidade |
|---|---|
| `quarkus-rest-jackson` | REST HTTP e conversão JSON/Jackson. |
| `quarkus-arc` | Injeção de dependências CDI usada por `@Inject` e `@ApplicationScoped`. |
| `quarkus-hibernate-orm-panache` | Hibernate ORM/JPA e API simplificada de persistência Panache. |
| `quarkus-jdbc-h2` | Driver JDBC e integração com H2. |
| `quarkus-hibernate-validator` | Integra Bean Validation com o Quarkus. |
| `quarkus-smallrye-openapi` | Geração do contrato OpenAPI e Swagger UI. |
| `quarkus-junit`, `quarkus-junit-mockito` | Testes Quarkus com JUnit 5 e integração Mockito. |
| `rest-assured` | Testes HTTP declarativos usando RestAssured. |

O `maven.compiler.release=17` limita as APIs e a sintaxe Java ao release 17. `maven-surefire-plugin` executa testes unitários; `maven-failsafe-plugin` executa testes de integração nas fases `integration-test` e `verify`. O perfil `native` configura o empacotamento nativo; ele não é necessário para executar o modo JVM ensinado aqui.

**Checkpoint:**

```powershell
.\mvnw.cmd -version
.\mvnw.cmd test
```

## 5. Organizar os pacotes

Crie o código em `src/main/java/com/giba` e mantenha os papéis separados:

| Pacote | O que vai nele |
|---|---|
| `com.giba.model` | Entidades persistentes e enum de domínio. |
| `com.giba.dto` | Records de entrada, saída e erro HTTP. |
| `com.giba.validation` | Constraint e implementação de validação de CPF. |
| `com.giba.repository` | Consultas Panache. |
| `com.giba.service` | Casos de uso, transações e regras de negócio. |
| `com.giba.exception` | Exceções de domínio e conversores de erro REST. |
| `com.giba.resource` | Endpoints REST. |
| `com.giba` | Catálogo de códigos e textos de mensagens. |

No PowerShell, as pastas podem ser criadas assim:

```powershell
$packages = @('dto','exception','model','repository','resource','service','validation')
foreach ($package in $packages) {
    New-Item -ItemType Directory -Force "src\main\java\com\giba\$package" | Out-Null
}
New-Item -ItemType Directory -Force 'src\main\resources' | Out-Null
New-Item -ItemType Directory -Force 'src\test\java\com\giba' | Out-Null
```

**Por que não colocar tudo na resource?** A resource conhece HTTP; o service conhece regras de negócio; o repository conhece consultas. Essa separação permite testar e evoluir cada responsabilidade sem fazer do endpoint o lugar de toda a aplicação.

## 6. Configurar a aplicação

Crie `src/main/resources/application.properties`. Os perfis `%dev` e `%test` sobrescrevem propriedades somente no ambiente correspondente. O exemplo evita gravar senha real no documento: ajuste as credenciais localmente e não as versione.

```properties
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:file:./gestao-empresas;DB_CLOSE_ON_EXIT=FALSE
quarkus.datasource.username=sa
quarkus.datasource.password=${DB_PASSWORD:}

quarkus.hibernate-orm.schema-management.strategy=update

quarkus.log.category."com.giba".level=INFO
quarkus.log.console.format=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c{2.}] (%t) %s%e%n

quarkus.swagger-ui.always-include=true
mp.openapi.extensions.smallrye.info.title=Gestao de Empresas e Trabalhadores API
mp.openapi.extensions.smallrye.info.version=1.0.0

%test.quarkus.datasource.jdbc.url=jdbc:h2:mem:gestao-test;DB_CLOSE_DELAY=-1
%test.quarkus.hibernate-orm.schema-management.strategy=drop-and-create

%dev.quarkus.hibernate-orm.dev-ui.allow-hql=true
```

`update` facilita o estudo local, mas não substitui migrações versionadas e não deve ser assumido como estratégia de produção. H2 em arquivo serve a este exercício; produção requer configurar explicitamente banco, credenciais e estratégia de esquema. Swagger UI sempre incluído também deve ser avaliado antes de publicar a aplicação.

> O valor de `DB_PASSWORD` deve ser configurado no ambiente somente se a instalação local de H2 exigir senha. Não coloque credenciais no código nem em arquivos versionados.

## 7. Criar o modelo persistente

Comece pelas entidades antes dos repositories, porque as consultas e relações precisam conhecer o modelo. Estas classes usam anotações Jakarta Persistence (`@Entity`, `@Id`, `@OneToOne`, `@ManyToOne`). A aplicação acessa os campos diretamente; esse estilo de acesso por campo é conciso, mas deixa o estado mutável exposto e pode ser melhor encapsulado em uma evolução.

### 7.1 `SituacaoPessoa`

Enum restringe situação a dois valores válidos. Em Java, `enum` representa um conjunto finito de constantes.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/model/SituacaoPessoa.java]]

### 7.2 `Endereco`

Cada coluna obrigatória usa `nullable=false`; os comprimentos limitam o tamanho no esquema. A associação é controlada pelo campo `endereco` de `Pessoa`.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/model/Endereco.java]]

### 7.3 `Pessoa`

`@Table` declara uma restrição única para CPF. A relação um-para-um propaga operações com `cascade=ALL`; `orphanRemoval=true` remove o endereço que deixa de estar associado.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/model/Pessoa.java]]

### 7.4 `Renda`

`BigDecimal` é preferível a `double` para valores monetários por representar decimais sem os erros típicos de ponto flutuante binário. A constraint única impede duas rendas da mesma pessoa para o mesmo mês e ano, inclusive em concorrência.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/model/Renda.java]]

**Checkpoint:** as entidades e as anotações descrevem o esquema e as relações; nenhum endpoint ou regra HTTP ainda é necessário.

## 8. Definir os contratos HTTP com DTOs

DTO (`Data Transfer Object`) define o formato que entra e sai da API. Não devolva diretamente entidades JPA ao cliente: os DTOs desacoplam o contrato externo do modelo persistente.

### 8.1 Requests e validação declarativa

`PessoaRequest` usa um record com validações de presença, tamanho, data passada e CPF. `EnderecoRequest` valida campos e formatos. `RendaRequest` limita mês, ano, sinal e precisão decimal.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/dto/PessoaRequest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/dto/EnderecoRequest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/dto/RendaRequest.java]]

### 8.2 Responses

Os métodos estáticos `de(...)` mapeiam entidades para respostas. O `EnderecoResponse` retorna `null` quando a pessoa não tem endereço.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/dto/PessoaResponse.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/dto/EnderecoResponse.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/dto/RendaResponse.java]]

### 8.3 Erro HTTP

Uma resposta simples e estável dá ao cliente um campo `mensagem` em vez de expor a entidade ou detalhes internos.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/dto/ErroResponse.java]]

**Java para certificação:** records existem a partir do Java 16 e estão disponíveis em Java 17; não são sintaxe de Java 8. `LocalDate`, enums, construtores canônicos e tipos de referência também são bons pontos de revisão.

## 9. Implementar a validação de CPF

Bean Validation fornece o contrato de constraint. A annotation `@CpfValido` informa ao runtime qual validador chamar e onde a anotação pode ser aplicada. O `CpfValidator` verifica os dígitos, aceita `null` para permitir que `@NotBlank` seja responsável pelo campo ausente e normaliza pontuação.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/validation/CpfValido.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/validation/CpfValidator.java]]

O mesmo validador tem métodos estáticos `normalizar` e `validar`, reaproveitados nos serviços. Essa validação verifica consistência do CPF; ela não comprova identidade civil nem substitui controles de segurança ou validação externa.

## 10. Criar os repositórios

Panache oferece operações comuns como `persist`, `findByIdOptional`, `list`, `count` e `delete`. Os repositórios abaixo adicionam apenas consultas específicas do domínio; os parâmetros de consulta são vinculados, não concatenados a strings de entrada.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/repository/PessoaRepository.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/repository/RendaRepository.java]]

**Checkpoint:** compile o projeto; as entidades e repositórios devem ser reconhecidos pelo Quarkus. As consultas serão exercitadas pelos testes de repository e de integração.

## 11. Implementar os casos de uso

Um service é um bean CDI `@ApplicationScoped`. `@Transactional` cria uma fronteira transacional para a operação. O service aplica regras que não pertencem ao protocolo HTTP, como impedir CPF duplicado, bloquear exclusão com rendas e validar o período da renda.

### 11.1 `PessoaService`

Passos principais: normalizar e verificar CPF; copiar os dados de request; persistir; manter ou remover endereço; impedir exclusão enquanto existirem rendas. O método `copiarDados` reduz duplicação entre criação e atualização.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/service/PessoaService.java]]

### 11.2 `RendaService`

Passos principais: localizar a pessoa pelo CPF; verificar duplicidade de período; criar/atualizar/excluir a renda; associá-la à pessoa. `BigDecimal` mantém a representação decimal do valor.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/service/RendaService.java]]

### 11.3 Injeção e transações

`@Inject` pede ao CDI que forneça o repository. A aplicação não cria a dependência com `new PessoaRepository()`: o container controla o ciclo de vida e integra o proxy transacional.

> **Concorrência:** a consulta de duplicidade melhora a mensagem para o caso normal, mas não elimina corrida entre requisições simultâneas. A constraint única no banco continua sendo a garantia final. Para um contrato HTTP robusto, trate especificamente a violação esperada e teste concorrência.

## 12. Padronizar mensagens e erros HTTP

### 12.1 Mensagens centralizadas

`messages.properties` guarda os textos parametrizáveis; `CodigoMensagem` dá nomes às chaves e `Mensagens` consulta o `ResourceBundle`. Isso evita espalhar textos repetidos por services e validators.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/resources/messages.properties]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/CodigoMensagem.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/Mensagens.java]]

### 12.2 Exceções de domínio e `ExceptionMapper`

`ConflitoException` representa uma regra que impede a operação; `EntidadeNaoEncontradaException` representa ausência do recurso. Os mappers convertem essas exceções em 409 e 404. `ValidacaoMapper` ordena violações e responde 400.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/exception/ConflitoException.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/exception/EntidadeNaoEncontradaException.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/exception/ConflitoMapper.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/exception/EntidadeNaoEncontradaMapper.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/exception/ValidacaoMapper.java]]

### 12.3 Validar IDs da rota

`IdPathParam` converte o texto do caminho para `Long` e responde com JSON 400 padronizado se não for numérico. É package-private porque só é usado pelas resources do mesmo pacote.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/resource/IdPathParam.java]]

## 13. Expor os endpoints REST

JAX-RS associa métodos Java a verbos e caminhos HTTP. `@Consumes` e `@Produces` definem JSON. `@Valid` solicita Bean Validation antes de executar a operação. As resources delegam ao service e constroem DTOs e status HTTP.

### 13.1 Endpoint de demonstração

Comece pelo endpoint mais simples para verificar que o servidor responde.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/resource/HelloResource.java]]

### 13.2 API de pessoas

Inclui cadastro (`201`), consulta/listagem/atualização (`200`), exclusão (`204`) e gestão do endereço.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/resource/PessoaResource.java]]

### 13.3 API de rendas

A rota é associada ao CPF da pessoa; a atualização e a exclusão também recebem o ID do registro.

[[ARQUIVO:quarkus-primeiro-projeto/src/main/java/com/giba/resource/RendaResource.java]]

### 13.4 Resumo das rotas

| Método | Caminho | Resultado esperado |
|---|---|---|
| `GET` | `/hello` | Mensagem de demonstração. |
| `POST` | `/api/pessoas` | Cria pessoa; `201`. |
| `GET` | `/api/pessoas` | Lista pessoas; `200`. |
| `GET` | `/api/pessoas/{id}` | Consulta pessoa; `200` ou `404`. |
| `PUT` | `/api/pessoas/{id}` | Atualiza pessoa; `200`. |
| `DELETE` | `/api/pessoas/{id}` | Exclui pessoa; `204` ou `409`. |
| `PUT` | `/api/pessoas/{id}/endereco` | Cria ou atualiza endereço; `200`. |
| `DELETE` | `/api/pessoas/{id}/endereco` | Remove endereço; `204`. |
| `POST` | `/api/pessoas/{cpf}/rendas` | Cria renda; `201` ou `409`. |
| `GET` | `/api/pessoas/{cpf}/rendas` | Lista rendas; `200`. |
| `PUT` | `/api/pessoas/{cpf}/rendas/registro/{id}` | Atualiza renda; `200`. |
| `DELETE` | `/api/pessoas/{cpf}/rendas/registro/{id}` | Exclui renda; `204`. |

> As listagens do projeto de referência retornam todos os registros, sem paginação. É simples para aprender, mas deve ganhar limites/paginação antes de atender volumes maiores.

## 14. Criar e executar os testes

O projeto mantém a estratégia existente: JUnit 5, Mockito, `@QuarkusTest`, RestAssured e uma classe de teste para cada classe de produção. A classe `GestaoApiTestBase` compartilha cenários HTTP entre `GestaoApiTest` e `GestaoApiIT`.

| Grupo | O que validar |
|---|---|
| `dto` | Validação de entrada e conversão de entidade para response. |
| `model` | Mapeamento JPA, constraints e enum. |
| `repository` | Consultas Panache em transação de teste. |
| `service` | Regras de negócio e interações verificadas com Mockito. |
| `resource` | Delega ao service e monta respostas HTTP. |
| `exception` | Status HTTP e corpo de erro. |
| `validation` | CPF e metadados da annotation. |
| `GestaoApiTest` | Fluxos REST com a aplicação Quarkus em teste. |
| `GestaoApiIT` | Fluxos contra o artefato empacotado. |

Após cada grupo, rode o menor comando necessário. Para a suite e integração completa:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify -DskipITs=false
```

Na validação do projeto de referência, `verify -DskipITs=false` terminou com `BUILD SUCCESS`: Surefire executou 38 testes; Failsafe executou 4 testes de integração. Ambos sem falhas nem erros.

**Java para certificação:** os testes ilustram classes, métodos, exceções, `assertThrows`, collections, lambdas e method references. Mockito e JUnit são bibliotecas e não fazem parte do exame de linguagem Java SE.

O Apêndice B contém o código completo dos testes do projeto de referência, incluindo a classe-base compartilhada pelos testes REST e o teste de integração. Para reconstruir o projeto sem depender do checkout original, crie cada arquivo indicado nas pastas correspondentes e então execute os comandos acima.

## 15. Executar, documentar e empacotar

### 15.1 Modo desenvolvimento

```powershell
.\mvnw.cmd quarkus:dev
```

Abra `http://localhost:8080/hello`. A interface OpenAPI fica em `/q/openapi` e Swagger UI em `/q/swagger-ui`.

### 15.2 Empacotar e iniciar

```powershell
.\mvnw.cmd package
java -jar target\quarkus-app\quarkus-run.jar
```

O pacote padrão usa diretório `quarkus-app` com aplicação e dependências separadas. A imagem JVM de referência copia as camadas do pacote e executa com um runtime Java 17. Os Dockerfiles para pacote nativo e JAR legado são alternativas de empacotamento, não passos necessários para o tutorial inicial.

### 15.3 Documentar decisões

Registre endpoints, exemplos JSON, perfis, banco local, comandos de teste e limitações. Não declare autenticação, paginação ou migrações se não estiverem implementadas. A documentação não substitui testes executáveis.

## 16. Relacionar o código às certificações Java

Os exames avaliam Java SE, não Quarkus. Use o projeto como contexto para rever linguagem e APIs; estude separadamente os objetivos oficiais atualizados.

| Exame | Como usar este projeto | Cuidado com a versão |
|---|---|---|
| 1Z0-808 — Java SE 8 Programmer I | Revisar classes, encapsulamento, herança, interfaces, exceptions, collections, generics, lambdas e streams quando esses fundamentos forem abordados. | Não associar `record` ou recursos posteriores ao Java 8. |
| 1Z0-829 — Java SE 17 Developer | Revisar recursos usados pelo projeto Java 17: records, enum, `LocalDate`, collections, streams, method references, lambdas, generics e exceptions. | POM usa `maven.compiler.release=17`. |
| 1Z0-830 — Java SE 21 Developer Professional | Comparar os fundamentos do projeto com Java 21 e estudar recursos/objetivos próprios do exame à parte. | O projeto está compilado para 17; não afirmar uso de recursos exclusivos do Java 21. |

Exemplos: `PessoaResponse::de` é uma method reference; `stream().map(...).toList()` usa Stream; `PanacheRepository<Pessoa>` usa generics; `Optional<Pessoa>` representa resultado possivelmente ausente; `BigDecimal` representa decimais monetários. `@Path`, `@Transactional`, CDI e JPA pertencem ao framework/ecossistema, não ao núcleo dos exames Java SE.

Questões de revisão produzidas para esta apostila são originais e não são questões oficiais Oracle. A auditoria não conseguiu confirmar os objetivos diretamente nas páginas Oracle; confira as fontes oficiais antes de montar um plano definitivo.

## 17. Roteiro de conferência e diagnóstico

Ao reproduzir o projeto do zero, confirme nesta ordem:

1. `java -version` aponta para JDK 17.0.12.
2. O comando Maven/Wrapper identifica Apache Maven 3.9.16.
3. O POM importa o BOM Quarkus 3.40.1.1 e compila com `release=17`.
4. `.\mvnw.cmd test` compila e executa a suite unitária.
5. O perfil `%test` usa H2 em memória; não conecta ao arquivo local.
6. O modo `quarkus:dev` sobe e responde em `/hello`.
7. Uma pessoa válida retorna `201`; CPF inválido retorna `400`; CPF repetido retorna `409`.
8. Uma pessoa inexistente retorna `404`; exclusão com renda associada retorna `409`.
9. `.\mvnw.cmd verify -DskipITs=false` executa os testes de integração e empacota o artefato.

Se uma etapa falhar, corrija a etapa atual antes de avançar. Leia a primeira causa do erro Maven, não apenas as últimas linhas do stack trace. Para avisos de configuração, confirme o nome exato da propriedade e se a extensão que a fornece está presente.

## 18. Auditoria resumida do projeto de referência

Esta seção preserva o diagnóstico do repositório usado como fonte do tutorial. Não significa que os pontos abaixo precisem ser implementados para acompanhar os exercícios locais.

| ID | Prioridade | Observação | Ação recomendada |
|---|---|---|---|
| A-01 | Alta se exposta publicamente | Os endpoints de pessoas e rendas não implementam autenticação/autorização. | Proteger operações e dados antes de exposição a redes não confiáveis; testar respostas 401/403. |
| A-02 | Média | H2 em arquivo e estratégia `update` são propriedades padrão, fora de `%test`. | Definir configuração de produção, banco e migrações versionadas; não usar atualização automática como estratégia de produção. |
| A-03 | Média | Listagens carregam resultados sem paginação. | Definir limite, ordenação e contrato de paginação. |
| A-04 | Baixa | Verificação prévia de duplicidade tem corrida concorrente; constraint protege o banco, mas a violação pode não virar 409. | Traduzir somente a violação de unicidade esperada e testar a concorrência se for requisito. |
| A-05 | Baixa no contexto auditado | Guava 33.6.0-jre, transitivo de teste, corresponde a um advisory OSV; o código auditado não usa `ObjectInputStream`. | Acompanhar atualização compatível e evitar desserialização nativa de dados não confiáveis. |

Não foi identificado achado crítico. A severidade do A-01 depende da exposição da API; o A-05 está em escopo de teste e a alcançabilidade não foi identificada nas classes da aplicação. A consulta OSV abrangeu 293 coordenadas Maven resolvidas e não comprova ausência de outras vulnerabilidades.

## Anexo A. Código completo de produção

As etapas 4 e 6–13 incluem o conteúdo dos arquivos originais do projeto de referência para que cada classe possa ser estudada e recriada sem procurar o fonte. Para configuração com credenciais, use valores locais não versionados e nunca publique segredos.

## Apêndice B. Código completo dos testes

Os testes seguem os mesmos pacotes da aplicação em `src/test/java/com/giba`. Crie as pastas conforme os caminhos indicados; cada trecho abaixo é o conteúdo integral do arquivo correspondente.

### B.1 Teste de mensagens

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/CodigoMensagemTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/MensagensTest.java]]

### B.2 Testes de DTOs

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/dto/EnderecoRequestTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/dto/EnderecoResponseTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/dto/ErroResponseTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/dto/PessoaRequestTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/dto/PessoaResponseTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/dto/RendaRequestTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/dto/RendaResponseTest.java]]

### B.3 Testes de exceções e respostas HTTP de erro

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/exception/ConflitoExceptionTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/exception/ConflitoMapperTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/exception/EntidadeNaoEncontradaExceptionTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/exception/EntidadeNaoEncontradaMapperTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/exception/ValidacaoMapperTest.java]]

### B.4 Testes de domínio e persistência

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/model/EnderecoTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/model/PessoaTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/model/RendaTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/model/SituacaoPessoaTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/repository/PessoaRepositoryTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/repository/RendaRepositoryTest.java]]

### B.5 Testes das resources

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/resource/HelloResourceTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/resource/IdPathParamTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/resource/PessoaResourceTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/resource/RendaResourceTest.java]]

### B.6 Testes dos services

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/service/PessoaServiceTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/service/RendaServiceTest.java]]

### B.7 Testes de validação

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/validation/CpfValidatorTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/validation/CpfValidoTest.java]]

### B.8 Testes da API e de integração

`GestaoApiTestBase` compartilha cenários de ponta a ponta. `GestaoApiTest` executa a API no ambiente de teste do Quarkus; `GestaoApiIT` verifica o artefato empacotado com Failsafe.

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/GestaoApiTestBase.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/GestaoApiTest.java]]

[[ARQUIVO:quarkus-primeiro-projeto/src/test/java/com/giba/GestaoApiIT.java]]
