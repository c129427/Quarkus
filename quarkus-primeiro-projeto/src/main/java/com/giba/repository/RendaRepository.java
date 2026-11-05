package com.giba.repository;

import com.giba.model.Renda;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RendaRepository implements PanacheRepository<Renda> {
    public List<Renda> listarPorCpf(String cpf) {
        return list("pessoa.cpf = ?1 order by ano desc, mes desc", cpf);
    }

    public long contarPorPessoa(Long pessoaId) {
        return count("pessoa.id", pessoaId);
    }

    public boolean existeNoPeriodo(Long pessoaId, Integer mes, Integer ano, Long ignorarId) {
        if (ignorarId == null) {
            return count("pessoa.id = ?1 and mes = ?2 and ano = ?3", pessoaId, mes, ano) > 0;
        }
        return count("pessoa.id = ?1 and mes = ?2 and ano = ?3 and id <> ?4",
                pessoaId, mes, ano, ignorarId) > 0;
    }
}
