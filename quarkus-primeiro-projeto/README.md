# Gestao de Empresas e Trabalhadores

API REST para cadastro de pessoas, enderecos e rendas mensais, implementada com Java 17 e Quarkus 3.40.1.1. Os corpos e as respostas usam JSON; datas seguem `yyyy-MM-dd`. O CPF pode ser enviado com pontuacao e e sempre persistido e retornado somente com digitos.

## Executar

E necessario JDK 17. Com o Maven Wrapper no Windows:

```powershell
.\mvnw.cmd quarkus:dev
```

A API fica em <http://localhost:8080>. OpenAPI: `/q/openapi`; Swagger UI: `/q/swagger-ui`. O endpoint de demonstracao `GET /hello` tambem esta disponivel.

## Endpoints

| Metodo | Caminho | Descricao |
|---|---|---|
| `POST` | `/api/pessoas` | Cadastra pessoa |
| `GET` | `/api/pessoas` | Lista pessoas, sem paginacao |
| `GET` | `/api/pessoas/{id}` | Consulta pessoa |
| `PUT` | `/api/pessoas/{id}` | Atualiza pessoa |
| `DELETE` | `/api/pessoas/{id}` | Exclui pessoa sem rendas vinculadas |
| `PUT` | `/api/pessoas/{id}/endereco` | Cria ou atualiza endereco |
| `DELETE` | `/api/pessoas/{id}/endereco` | Remove endereco |
| `POST` | `/api/pessoas/{cpf}/rendas` | Cadastra renda mensal |
| `GET` | `/api/pessoas/{cpf}/rendas` | Lista rendas mensais |
| `PUT` | `/api/pessoas/{cpf}/rendas/registro/{id}` | Atualiza renda mensal |
| `DELETE` | `/api/pessoas/{cpf}/rendas/registro/{id}` | Exclui renda mensal |

Exemplo de pessoa:

```json
{
  "cpf": "529.982.247-25",
  "nome": "Ana Souza",
  "dataNascimento": "1990-01-02",
  "situacao": "ATIVA"
}
```

Exemplo de renda:

```json
{
  "mes": 5,
  "ano": 2025,
  "valor": 12000.00
}
```

Enderecos sao opcionais. Rendas nao podem ter valores negativos e so pode haver um registro por pessoa, mes e ano. Uma pessoa com rendas cadastradas nao pode ser excluida. As respostas de listagem sao arrays.

Erros de validacao e regras de data retornam `400 Bad Request`; consultas sem resultado retornam `404 Not Found`; CPF duplicado, renda repetida ou exclusao bloqueada por vinculos retornam `409 Conflict`.

## Persistencia

O perfil padrao usa H2 em arquivo (`./gestao-empresas`). O Hibernate cria o esquema quando necessario e atualiza-o ao iniciar. No perfil de testes e usado H2 em memoria, recriado a cada inicializacao. Antes de producao, configure um banco persistente, migracoes e uma estrategia de esquema apropriada.

## Logging

A aplicacao usa o logger integrado ao Quarkus (`org.jboss.logging.Logger`), com nivel `INFO` para o package `com.giba`. Os servicos registram operacoes de cadastro, atualizacao e exclusao sem incluir CPF, nome ou valores de renda nos logs.

## Testes e empacotamento

Os testes unitarios usam JUnit 5 e Mockito; os testes de API usam QuarkusTest e RestAssured. Execute a suite com:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify -DskipITs=false
.\mvnw.cmd package
```

O JAR JVM executavel e `target\quarkus-app\quarkus-run.jar`:

```powershell
java -jar target\quarkus-app\quarkus-run.jar
```

O pacote padrao mantem dependencias separadas. Para gerar um uber-JAR:

```powershell
.\mvnw.cmd package -Dquarkus.package.jar.type=uber-jar
```

Autenticacao/autorizacao, paginacao, migracoes de banco e configuracao de producao estao fora do escopo atual.
