package com.sistema.infraestrutura.mapper;

import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.dominio.entidade.Fatura;
import com.sistema.dominio.entidade.Transacao;
import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.entidade.FaturaEntity;
import com.sistema.infraestrutura.entidade.TransacaoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-10T21:35:30-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (JetBrains s.r.o.)"
)
@ApplicationScoped
public class FaturaMapperImpl implements FaturaMapper {

    @Override
    public FaturaEntity toEntity(Fatura fatura) {
        if ( fatura == null ) {
            return null;
        }

        FaturaEntity faturaEntity = new FaturaEntity();

        faturaEntity.setId( fatura.getId() );
        faturaEntity.setTotal( fatura.getTotal() );
        faturaEntity.setPagamentoMinimo( fatura.getPagamentoMinimo() );
        faturaEntity.setMesAno( fatura.getMesAno() );
        faturaEntity.setPaga( fatura.isPaga() );
        faturaEntity.setTransacoes( transacaoListToTransacaoEntityList( fatura.getTransacoes() ) );
        faturaEntity.setValorEmAberto( fatura.getValorEmAberto() );

        return faturaEntity;
    }

    @Override
    public Fatura toDomain(FaturaEntity faturaEntity) {
        if ( faturaEntity == null ) {
            return null;
        }

        Fatura fatura = new Fatura();

        fatura.setId( faturaEntity.getId() );
        fatura.setTotal( faturaEntity.getTotal() );
        fatura.setPagamentoMinimo( faturaEntity.getPagamentoMinimo() );
        fatura.setMesAno( faturaEntity.getMesAno() );
        fatura.setPaga( faturaEntity.isPaga() );
        fatura.setValorEmAberto( faturaEntity.getValorEmAberto() );
        if ( fatura.getTransacoes() != null ) {
            List<Transacao> list = transacaoEntityListToTransacaoList( faturaEntity.getTransacoes() );
            if ( list != null ) {
                fatura.getTransacoes().addAll( list );
            }
        }

        return fatura;
    }

    protected ClienteEntity clienteToClienteEntity(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteEntity clienteEntity = new ClienteEntity();

        clienteEntity.setId( cliente.getId() );
        clienteEntity.setCpf( cliente.getCpf() );
        clienteEntity.setCnpj( cliente.getCnpj() );
        clienteEntity.setNome( cliente.getNome() );
        clienteEntity.setEmail( cliente.getEmail() );
        clienteEntity.setTelefone( cliente.getTelefone() );
        clienteEntity.setAtivo( cliente.isAtivo() );
        clienteEntity.setDataCadastro( cliente.getDataCadastro() );

        return clienteEntity;
    }

    protected CartaoDeCreditoEntity cartaoDeCreditoToCartaoDeCreditoEntity(CartaoDeCredito cartaoDeCredito) {
        if ( cartaoDeCredito == null ) {
            return null;
        }

        CartaoDeCreditoEntity cartaoDeCreditoEntity = new CartaoDeCreditoEntity();

        cartaoDeCreditoEntity.setId( cartaoDeCredito.getId() );
        cartaoDeCreditoEntity.setNumero( cartaoDeCredito.getNumero() );
        cartaoDeCreditoEntity.setBandeira( cartaoDeCredito.getBandeira() );
        cartaoDeCreditoEntity.setNomeTitular( cartaoDeCredito.getNomeTitular() );
        cartaoDeCreditoEntity.setDataValidade( cartaoDeCredito.getDataValidade() );
        cartaoDeCreditoEntity.setCvv( cartaoDeCredito.getCvv() );
        cartaoDeCreditoEntity.setLimiteTotal( cartaoDeCredito.getLimiteTotal() );
        cartaoDeCreditoEntity.setLimiteDisponivel( cartaoDeCredito.getLimiteDisponivel() );
        cartaoDeCreditoEntity.setSaldoDevedor( cartaoDeCredito.getSaldoDevedor() );
        cartaoDeCreditoEntity.setAtivo( cartaoDeCredito.isAtivo() );
        cartaoDeCreditoEntity.setBloqueadoPorPerdaOuRoubo( cartaoDeCredito.isBloqueadoPorPerdaOuRoubo() );
        cartaoDeCreditoEntity.setCliente( clienteToClienteEntity( cartaoDeCredito.getCliente() ) );

        return cartaoDeCreditoEntity;
    }

    protected TransacaoEntity transacaoToTransacaoEntity(Transacao transacao) {
        if ( transacao == null ) {
            return null;
        }

        TransacaoEntity transacaoEntity = new TransacaoEntity();

        transacaoEntity.setId( transacao.getId() );
        transacaoEntity.setDescricao( transacao.getDescricao() );
        transacaoEntity.setValor( transacao.getValor() );
        transacaoEntity.setDataHora( transacao.getDataHora() );
        transacaoEntity.setCartao( cartaoDeCreditoToCartaoDeCreditoEntity( transacao.getCartao() ) );
        transacaoEntity.setFatura( toEntity( transacao.getFatura() ) );

        return transacaoEntity;
    }

    protected List<TransacaoEntity> transacaoListToTransacaoEntityList(List<Transacao> list) {
        if ( list == null ) {
            return null;
        }

        List<TransacaoEntity> list1 = new ArrayList<TransacaoEntity>( list.size() );
        for ( Transacao transacao : list ) {
            list1.add( transacaoToTransacaoEntity( transacao ) );
        }

        return list1;
    }

    protected Cliente clienteEntityToCliente(ClienteEntity clienteEntity) {
        if ( clienteEntity == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setId( clienteEntity.getId() );
        cliente.setCpf( clienteEntity.getCpf() );
        cliente.setCnpj( clienteEntity.getCnpj() );
        cliente.setNome( clienteEntity.getNome() );
        cliente.setEmail( clienteEntity.getEmail() );
        cliente.setTelefone( clienteEntity.getTelefone() );
        cliente.setAtivo( clienteEntity.isAtivo() );
        cliente.setDataCadastro( clienteEntity.getDataCadastro() );

        return cliente;
    }

    protected CartaoDeCredito cartaoDeCreditoEntityToCartaoDeCredito(CartaoDeCreditoEntity cartaoDeCreditoEntity) {
        if ( cartaoDeCreditoEntity == null ) {
            return null;
        }

        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();

        cartaoDeCredito.setId( cartaoDeCreditoEntity.getId() );
        cartaoDeCredito.setNumero( cartaoDeCreditoEntity.getNumero() );
        cartaoDeCredito.setBandeira( cartaoDeCreditoEntity.getBandeira() );
        cartaoDeCredito.setNomeTitular( cartaoDeCreditoEntity.getNomeTitular() );
        cartaoDeCredito.setCvv( cartaoDeCreditoEntity.getCvv() );
        cartaoDeCredito.setDataValidade( cartaoDeCreditoEntity.getDataValidade() );
        cartaoDeCredito.setLimiteTotal( cartaoDeCreditoEntity.getLimiteTotal() );
        cartaoDeCredito.setLimiteDisponivel( cartaoDeCreditoEntity.getLimiteDisponivel() );
        cartaoDeCredito.setSaldoDevedor( cartaoDeCreditoEntity.getSaldoDevedor() );
        cartaoDeCredito.setAtivo( cartaoDeCreditoEntity.isAtivo() );
        cartaoDeCredito.setBloqueadoPorPerdaOuRoubo( cartaoDeCreditoEntity.isBloqueadoPorPerdaOuRoubo() );
        cartaoDeCredito.setCliente( clienteEntityToCliente( cartaoDeCreditoEntity.getCliente() ) );

        return cartaoDeCredito;
    }

    protected Transacao transacaoEntityToTransacao(TransacaoEntity transacaoEntity) {
        if ( transacaoEntity == null ) {
            return null;
        }

        String descricao = null;
        BigDecimal valor = null;
        LocalDateTime dataHora = null;
        CartaoDeCredito cartao = null;

        descricao = transacaoEntity.getDescricao();
        valor = transacaoEntity.getValor();
        dataHora = transacaoEntity.getDataHora();
        cartao = cartaoDeCreditoEntityToCartaoDeCredito( transacaoEntity.getCartao() );

        Transacao transacao = new Transacao( descricao, valor, cartao, dataHora );

        transacao.setId( transacaoEntity.getId() );
        transacao.setFatura( toDomain( transacaoEntity.getFatura() ) );

        return transacao;
    }

    protected List<Transacao> transacaoEntityListToTransacaoList(List<TransacaoEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<Transacao> list1 = new ArrayList<Transacao>( list.size() );
        for ( TransacaoEntity transacaoEntity : list ) {
            list1.add( transacaoEntityToTransacao( transacaoEntity ) );
        }

        return list1;
    }
}
