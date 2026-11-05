# Instruções do GitHub Copilot

1. Responder sempre em português do Brasil.

Código-fonte, nomes de APIs, bibliotecas, classes, métodos, propriedades e palavras-chave das linguagens podem permanecer em inglês quando esse for o padrão técnico.

2. Usar Quarkus como framework da aplicação. Não introduzir Spring sem solicitação explícita.

3. Usar Java 17 e preservar a estratégia existente de testes:
   - JUnit 5
   - Mockito
   - `@QuarkusTest`
   - RestAssured
   - Uma classe de teste por classe de produção

   Não alterar essa estratégia sem solicitação explícita.