package com.giba.service;

import com.giba.CodigoMensagem;
import com.giba.Mensagens;
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
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class RendaService {

    private static final Logger LOG = Logger.getLogger(RendaService.class);

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
        LOG.infof("Renda cadastrada. id=%s pessoaId=%s periodo=%02d/%d",
                renda.id, pessoa.id, renda.mes, renda.ano);
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
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        Mensagens.obter(CodigoMensagem.REGISTRO_NAO_ENCONTRADO)));
        verificarPeriodo(request, pessoa.id, rendaId);
        copiarDados(request, renda);
        LOG.infof("Renda atualizada. id=%s pessoaId=%s periodo=%02d/%d",
                renda.id, pessoa.id, renda.mes, renda.ano);
        return renda;
    }

    @Transactional
    public void excluir(String cpfInformado, Long rendaId) {
        Pessoa pessoa = buscarPessoa(cpfInformado);
        Renda renda = rendas.find("id = ?1 and pessoa.id = ?2", rendaId, pessoa.id).firstResultOptional()
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        Mensagens.obter(CodigoMensagem.REGISTRO_NAO_ENCONTRADO)));
        rendas.delete(renda);
        LOG.infof("Renda excluida. id=%s pessoaId=%s", rendaId, pessoa.id);
    }

    private Pessoa buscarPessoa(String cpfInformado) {
        String cpf;
        try {
            cpf = CpfValidator.normalizar(cpfInformado);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(Mensagens.obter(CodigoMensagem.VALOR_INVALIDO, "cpf"));
        }
        if (!CpfValidator.validar(cpf)) {
            throw new BadRequestException(Mensagens.obter(CodigoMensagem.VALOR_INVALIDO, "cpf"));
        }
        return pessoas.buscarPorCpf(cpf)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        Mensagens.obter(CodigoMensagem.REGISTRO_NAO_ENCONTRADO)));
    }

    private void verificarPeriodo(RendaRequest request, Long pessoaId, Long ignorarId) {
        if (rendas.existeNoPeriodo(pessoaId, request.mes(), request.ano(), ignorarId)) {
            throw new ConflitoException(Mensagens.obter(CodigoMensagem.REGISTRO_JA_EXISTE));
        }
    }

    private void copiarDados(RendaRequest request, Renda renda) {
        renda.mes = request.mes();
        renda.ano = request.ano();
        renda.valor = request.valor();
    }
}
