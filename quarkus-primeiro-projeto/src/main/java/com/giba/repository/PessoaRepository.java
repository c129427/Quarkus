package com.giba.repository;

import com.giba.model.Pessoa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PessoaRepository implements PanacheRepository<Pessoa> {
    public Optional<Pessoa> buscarPorCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }
}
