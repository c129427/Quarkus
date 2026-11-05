package com.giba.service;

import com.giba.dto.EnderecoRequest;
import com.giba.dto.PessoaRequest;
import com.giba.exception.ConflitoException;
import com.giba.exception.EntidadeNaoEncontradaException;
import com.giba.model.Endereco;
import com.giba.model.Pessoa;
import com.giba.repository.PessoaRepository;
import com.giba.repository.RendaRepository;
import com.giba.validation.CpfValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
public class PessoaService {

    @Inject
    PessoaRepository pessoas;

    @Inject
    RendaRepository rendas;

    @Transactional
    public Pessoa criar(PessoaRequest request) {
        String cpf = CpfValidator.normalizar(request.cpf());
        if (!CpfValidator.validar(cpf)) {
            throw new BadRequestException("CPF invalido");
        }
        if (pessoas.buscarPorCpf(cpf).isPresent()) {
            throw new ConflitoException("Ja existe uma pessoa cadastrada com este CPF");
        }
        Pessoa pessoa = new Pessoa();
        pessoa.cpf = cpf;
        copiarDados(request, pessoa);
        pessoas.persist(pessoa);
        return pessoa;
    }

    @Transactional
    public List<Pessoa> listar() {
        return pessoas.listAll(io.quarkus.panache.common.Sort.by("nome"));
    }

    @Transactional
    public Pessoa buscar(Long id) {
        return pessoas.findByIdOptional(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pessoa nao encontrada"));
    }

    @Transactional
    public Pessoa atualizar(Long id, PessoaRequest request) {
        Pessoa pessoa = buscar(id);
        String cpf = CpfValidator.normalizar(request.cpf());
        if (!CpfValidator.validar(cpf)) {
            throw new BadRequestException("CPF invalido");
        }
        pessoas.buscarPorCpf(cpf).filter(outra -> !outra.id.equals(id)).ifPresent(outra -> {
            throw new ConflitoException("Ja existe uma pessoa cadastrada com este CPF");
        });
        pessoa.cpf = cpf;
        copiarDados(request, pessoa);
        return pessoa;
    }

    @Transactional
    public void excluir(Long id) {
        Pessoa pessoa = buscar(id);
        if (rendas.contarPorPessoa(id) > 0) {
            throw new ConflitoException("Pessoa nao pode ser excluida enquanto possuir rendas cadastradas");
        }
        pessoas.delete(pessoa);
    }

    @Transactional
    public Pessoa atualizarEndereco(Long id, EnderecoRequest request) {
        Pessoa pessoa = buscar(id);
        Endereco endereco = pessoa.endereco;
        if (endereco == null) {
            endereco = new Endereco();
            pessoa.endereco = endereco;
        }
        endereco.logradouro = request.logradouro().trim();
        endereco.numero = request.numero().trim();
        endereco.complemento = request.complemento() == null ? null : request.complemento().trim();
        endereco.bairro = request.bairro().trim();
        endereco.cidade = request.cidade().trim();
        endereco.estado = request.estado().toUpperCase(java.util.Locale.ROOT);
        endereco.cep = request.cep().replace("-", "");
        return pessoa;
    }

    @Transactional
    public void excluirEndereco(Long id) {
        Pessoa pessoa = buscar(id);
        if (pessoa.endereco == null) {
            throw new EntidadeNaoEncontradaException("Endereco nao encontrado");
        }
        pessoa.endereco = null;
    }

    private void copiarDados(PessoaRequest request, Pessoa pessoa) {
        pessoa.nome = request.nome().trim();
        pessoa.dataNascimento = request.dataNascimento();
        pessoa.situacao = request.situacao();
    }
}
