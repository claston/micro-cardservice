package com.sistema.casodeuso;

import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Transacao;
import com.sistema.infraestrutura.entidade.TransacaoEntity;
import com.sistema.infraestrutura.mapper.TransacaoMapper;
import com.sistema.dominio.repository.CartaoRepository;
import com.sistema.infraestrutura.repositorio.TransacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RegistarCompraCasoDeUso {

    @Inject
    CartaoRepository cartaoDeCreditoRepository;

    @Inject
    TransacaoRepository transacaoRepository;

    @Inject
    TransacaoMapper transacaoMapper;

    @Transactional
    public Transacao registraCompra(Transacao transacao) {

        CartaoDeCredito cartao = cartaoDeCreditoRepository.findById(transacao.getCartao().getId());
        if (cartao == null){
            throw new IllegalArgumentException("Cartão não encontrado");
        }

        if (cartao.getLimiteDisponivel().compareTo(transacao.getValor()) < 0){
            throw new IllegalArgumentException(("Limite Insuficiente para essa compra!"));
        }

        System.out.println("TESTE###########: " + cartao.getCliente().getDataCadastro());
        transacao.setCartao(cartao);

        TransacaoEntity transacaoEntity = transacaoMapper.toEntity(transacao);
        transacaoRepository.persist(transacaoEntity);

        return transacao;
    }
}
