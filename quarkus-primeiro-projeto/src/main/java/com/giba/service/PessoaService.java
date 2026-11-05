package com.giba.service;

import com.giba.CodigoMensagem;
import com.giba.Mensagens;
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
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class PessoaService {

    private static final Logger LOG = Logger.getLogger(PessoaService.class);

    @Inject
    PessoaRepository pessoas;

    @Inject
    RendaRepository rendas;

    @Transactional
    public Pessoa criar(PessoaRequest request) {
        String cpf = normalizarCpf(request.cpf());
        if (pessoas.buscarPorCpf(cpf).isPresent()) {
            throw new ConflitoException(Mensagens.obter(CodigoMensagem.REGISTRO_JA_EXISTE));
        }
        Pessoa pessoa = new Pessoa();
        pessoa.cpf = cpf;
        copiarDados(request, pessoa);
        pessoas.persist(pessoa);
        LOG.infof("Pessoa cadastrada. id=%s", pessoa.id);
        return pessoa;
    }

    @Transactional
    public List<Pessoa> listar() {
        return pessoas.listAll(io.quarkus.panache.common.Sort.by("nome"));
    }

    @Transactional
    public Pessoa buscar(Long id) {
        return pessoas.findByIdOptional(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        Mensagens.obter(CodigoMensagem.REGISTRO_NAO_ENCONTRADO)));
    }

    @Transactional
    public Pessoa atualizar(Long id, PessoaRequest request) {
        Pessoa pessoa = buscar(id);
        String cpf = normalizarCpf(request.cpf());
        pessoas.buscarPorCpf(cpf).filter(outra -> !outra.id.equals(id)).ifPresent(outra -> {
            throw new ConflitoException(Mensagens.obter(CodigoMensagem.REGISTRO_JA_EXISTE));
        });
        pessoa.cpf = cpf;
        copiarDados(request, pessoa);
        LOG.infof("Pessoa atualizada. id=%s", id);
        return pessoa;
    }

    @Transactional
    public void excluir(Long id) {
        Pessoa pessoa = buscar(id);
        if (rendas.contarPorPessoa(id) > 0) {
            throw new ConflitoException(Mensagens.obter(CodigoMensagem.REGISTRO_POSSUI_DEPENDENCIAS));
        }
        pessoas.delete(pessoa);
        LOG.infof("Pessoa excluida. id=%s", id);
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
        LOG.infof("Endereco atualizado. pessoaId=%s", id);
        return pessoa;
    }

    @Transactional
    public void excluirEndereco(Long id) {
        Pessoa pessoa = buscar(id);
        if (pessoa.endereco == null) {
            throw new EntidadeNaoEncontradaException(Mensagens.obter(CodigoMensagem.REGISTRO_NAO_ENCONTRADO));
        }
        pessoa.endereco = null;
        LOG.infof("Endereco excluido. pessoaId=%s", id);
    }

    private void copiarDados(PessoaRequest request, Pessoa pessoa) {
        pessoa.nome = request.nome().trim();
        pessoa.dataNascimento = request.dataNascimento();
        pessoa.situacao = request.situacao();
    }

    private String normalizarCpf(String valor) {
        try {
            String cpf = CpfValidator.normalizar(valor);
            if (!CpfValidator.validar(cpf)) {
                throw new BadRequestException(Mensagens.obter(CodigoMensagem.VALOR_INVALIDO, "cpf"));
            }
            return cpf;
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(Mensagens.obter(CodigoMensagem.VALOR_INVALIDO, "cpf"));
        }
    }
}
