package com.giba;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class GestaoApiTest {

    @Test
    void deveGerenciarPessoaEnderecoERenda() {
        String cpf = gerarCpfValidoUnico();
        Integer pessoaId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"cpf":"%s","nome":"Ana Souza","dataNascimento":"1990-01-02","situacao":"ATIVA"}
                        """.formatted(cpf))
                .when().post("/api/pessoas")
                .then().statusCode(201)
                .body("cpf", equalTo(cpf))
                .body("endereco", equalTo(null))
                .extract().path("id");

        given().when().get("/api/pessoas/{id}", pessoaId)
                .then().statusCode(200).body("nome", equalTo("Ana Souza"));

        given().contentType(ContentType.JSON)
                .body("""
                        {"cpf":"%s","nome":"Ana Souza Atualizada","dataNascimento":"1990-01-02","situacao":"INATIVA"}
                        """.formatted(cpf))
                .when().put("/api/pessoas/{id}", pessoaId)
                .then().statusCode(200).body("nome", equalTo("Ana Souza Atualizada"));

        given().when().get("/api/pessoas")
                .then().statusCode(200).body("cpf", hasItem(cpf));

        given().contentType(ContentType.JSON)
                .body("""
                        {"cpf":"%s","nome":"Outra Pessoa","dataNascimento":"1990-01-02","situacao":"ATIVA"}
                        """.formatted(cpf))
                .when().post("/api/pessoas")
                .then().statusCode(409);

        given().contentType(ContentType.JSON)
                .body("""
                        {"logradouro":"Rua das Flores","numero":"10","complemento":"Apto 2","bairro":"Centro","cidade":"Sao Paulo","estado":"SP","cep":"01001-000"}
                        """)
                .when().put("/api/pessoas/{id}/endereco", pessoaId)
                .then().statusCode(200).body("cep", equalTo("01001000"));

        Integer rendaId = given().contentType(ContentType.JSON)
                .body("""
                        {"mes":5,"ano":2025,"valor":12000.00}
                        """)
                .when().post("/api/pessoas/{cpf}/rendas", cpf)
                .then().statusCode(201).body("id", notNullValue())
                .extract().path("id");

        given().contentType(ContentType.JSON)
                .body("""
                        {"mes":5,"ano":2025,"valor":100.00}
                        """)
                .when().post("/api/pessoas/{cpf}/rendas", cpf)
                .then().statusCode(409);

        given().contentType(ContentType.JSON)
                .body("""
                        {"mes":6,"ano":2025,"valor":12500.00}
                        """)
                .when().put("/api/pessoas/{cpf}/rendas/registro/{id}", cpf, rendaId)
                .then().statusCode(200).body("mes", equalTo(6));

        given().when().get("/api/pessoas/{cpf}/rendas", cpf)
                .then().statusCode(200).body("size()", equalTo(1));

        given().when().delete("/api/pessoas/{id}", pessoaId)
                .then().statusCode(409);

        given().when().delete("/api/pessoas/{cpf}/rendas/registro/{id}", cpf, rendaId)
                .then().statusCode(204);

        given().when().delete("/api/pessoas/{id}/endereco", pessoaId)
                .then().statusCode(204);

        given().when().delete("/api/pessoas/{id}", pessoaId)
                .then().statusCode(204);

        given().when().get("/api/pessoas/{id}", pessoaId)
                .then().statusCode(404);
    }

    @Test
    void deveRejeitarCpfInvalidoEDataDeNascimentoFutura() {
        given().contentType(ContentType.JSON)
                .body("""
                        {"cpf":"111.111.111-11","nome":"Pessoa Invalida","dataNascimento":"1990-01-01","situacao":"ATIVA"}
                        """)
                .when().post("/api/pessoas")
                .then().statusCode(400);

        given().contentType(ContentType.JSON)
                .body("""
                        {"cpf":"529.982.247-25","nome":"Pessoa Invalida","dataNascimento":"2999-01-01","situacao":"ATIVA"}
                        """)
                .when().post("/api/pessoas")
                .then().statusCode(400);
    }

    @Test
    void deveRejeitarRendaNegativa() {
        String cpf = gerarCpfValidoUnico();
        given().contentType(ContentType.JSON)
                .body("""
                        {"cpf":"%s","nome":"Bruna Souza","dataNascimento":"1990-01-02","situacao":"ATIVA"}
                        """.formatted(cpf))
                .when().post("/api/pessoas")
                .then().statusCode(201);

        given().contentType(ContentType.JSON)
                .body("""
                        {"mes":5,"ano":2025,"valor":-1}
                        """)
                .when().post("/api/pessoas/{cpf}/rendas", cpf)
                .then().statusCode(400);
    }

    private String gerarCpfValidoUnico() {
        String base = String.format("%09d", ThreadLocalRandom.current().nextInt(1_000_000_000));
        int primeiro = calcularDigito(base, 9);
        int segundo = calcularDigito(base + primeiro, 10);
        return base + primeiro + segundo;
    }

    private int calcularDigito(String cpf, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
