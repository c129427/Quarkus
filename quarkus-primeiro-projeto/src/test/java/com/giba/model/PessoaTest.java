package com.giba.model;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PessoaTest {

    @Test
    void mapeiaCpfUnicoEEnderecoUmParaUm() throws ReflectiveOperationException {
        UniqueConstraint[] constraints = Pessoa.class.getAnnotation(Table.class).uniqueConstraints();
        assertTrue(java.util.Arrays.stream(constraints)
                .anyMatch(constraint -> java.util.Arrays.asList(constraint.columnNames()).contains("cpf")));
        assertTrue(Pessoa.class.getField("endereco").getAnnotation(OneToOne.class).orphanRemoval());
    }
}
