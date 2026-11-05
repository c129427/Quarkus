package com.giba.service;

import com.giba.dto.RendaRequest;
import com.giba.exception.ConflitoException;
import com.giba.exception.EntidadeNaoEncontradaException;
import com.giba.model.Pessoa;
import com.giba.model.Renda;
import com.giba.repository.PessoaRepository;
import com.giba.repository.RendaRepository;
import com.giba.validation.CpfValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
public class RendaService {

    @Inject
    PessoaRepository pessoas;

    @Inject
    RendaRepository rendas;

    @Transactional
    public Renda criar(String cpfInformado, RendaRequest request) {
        Pessoa pessoa = buscarPessoa(cpfInformado);
        verificarPeriodo(request, pessoa.id, null);
        Renda renda = new Renda();
        renda.pessoa = pessoa;
        copiarDados(request, renda);
        rendas.persist(renda);
        return renda;
    }

    @Transactional
    public List<Renda> listar(String cpfInformado) {
        Pessoa pessoa = buscarPessoa(cpfInformado);
        return rendas.listarPorCpf(pessoa.cpf);
    }

    @Transactional
    public Renda atualizar(String cpfInformado, Long rendaId, RendaRequest request) {
        Pessoa pessoa = buscarPessoa(cpfInformado);
        Renda renda = rendas.find("id = ?1 and pessoa.id = ?2", rendaId, pessoa.id).firstResultOptional()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Renda nao encontrada"));
        verificarPeriodo(request, pessoa.id, rendaId);
        copiarDados(request, renda);
        return renda;
    }

    @Transactional
    public void excluir(String cpfInformado, Long rendaId) {
        Pessoa pessoa = buscarPessoa(cpfInformado);
        Renda renda = rendas.find("id = ?1 and pessoa.id = ?2", rendaId, pessoa.id).firstResultOptional()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Renda nao encontrada"));
        rendas.delete(renda);
    }

    private Pessoa buscarPessoa(String cpfInformado) {
        String cpf;
        try {
            cpf = CpfValidator.normalizar(cpfInformado);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("CPF invalido");
        }
        if (!CpfValidator.validar(cpf)) {
            throw new BadRequestException("CPF invalido");
        }
        return pessoas.buscarPorCpf(cpf)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pessoa nao encontrada"));
    }

    private void verificarPeriodo(RendaRequest request, Long pessoaId, Long ignorarId) {
        if (rendas.existeNoPeriodo(pessoaId, request.mes(), request.ano(), ignorarId)) {
            throw new ConflitoException("Ja existe uma renda cadastrada para este mes e ano");
        }
    }

    private void copiarDados(RendaRequest request, Renda renda) {
        renda.mes = request.mes();
        renda.ano = request.ano();
        renda.valor = request.valor();
    }
}
