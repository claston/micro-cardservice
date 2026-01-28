package com.sistema.creditcard.casodeuso;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.entidade.Transacao;
import com.sistema.creditcard.infraestrutura.entidade.TransacaoEntity;
import com.sistema.creditcard.infraestrutura.mapper.TransacaoMapper;
import com.sistema.creditcard.dominio.repository.CartaoRepository;
import com.sistema.creditcard.infraestrutura.repositorio.TransacaoRepository;
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

        CreditCard cartao = cartaoDeCreditoRepository.findById(transacao.getCartao().getId());
        if (cartao == null){
            throw new IllegalArgumentException("Cartão não encontrado");
        }

        if (cartao.getLimiteDisponivel().compareTo(transacao.getValor()) < 0){
            throw new IllegalArgumentException(("Limite Insuficiente para essa compra!"));
        }

        transacao.setCartao(cartao);

        TransacaoEntity transacaoEntity = transacaoMapper.toEntity(transacao);
        transacaoRepository.persist(transacaoEntity);

        return transacao;
    }
}


