package com.sistema.casodeuso;

import com.sistema.dominio.entidade.Transacao;
import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.entidade.TransacaoEntity;
import com.sistema.infraestrutura.mapper.CartaoDeCreditoMapper;
import com.sistema.infraestrutura.mapper.TransacaoMapper;
import com.sistema.infraestrutura.repositorio.CartaoDeCreditoRepository;
import com.sistema.infraestrutura.repositorio.TransacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RegistarCompraCasoDeUso {

    @Inject
    CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Inject
    TransacaoRepository transacaoRepository;

    @Inject
    TransacaoMapper transacaoMapper;

    @Inject
    CartaoDeCreditoMapper cartaoDeCreditoMapper;

    @Transactional
    public Transacao registraCompra(Transacao transacao) {

        CartaoDeCreditoEntity cartaoEntity = cartaoDeCreditoRepository.findById(transacao.getCartao().getId());
        if (cartaoEntity == null){
            throw new IllegalArgumentException("Cartão não encontrado");
        }

        if (cartaoEntity.getLimiteDisponivel().compareTo(transacao.getValor()) < 0){
            throw new IllegalArgumentException(("Limite Insuficiente para essa compra!"));
        }

        System.out.println("TESTE###########: " + cartaoEntity.getCliente().getDataCadastro());
        transacao.setCartao(cartaoDeCreditoMapper.toDomain(cartaoEntity));

        TransacaoEntity transacaoEntity = transacaoMapper.toEntity(transacao);
        transacaoRepository.persist(transacaoEntity);

        return transacao;
    }
}
