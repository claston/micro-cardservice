package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.TransacaoDTO;
import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.dominio.entidade.Transacao;
import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.entidade.TransacaoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-10T21:35:30-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (JetBrains s.r.o.)"
)
@ApplicationScoped
public class TransacaoMapperImpl implements TransacaoMapper {

    @Override
    public TransacaoEntity toEntity(Transacao transacao) {
        if ( transacao == null ) {
            return null;
        }

        TransacaoEntity transacaoEntity = new TransacaoEntity();

        transacaoEntity.setCartao( cartaoDeCreditoToCartaoDeCreditoEntity( transacao.getCartao() ) );
        transacaoEntity.setId( transacao.getId() );
        transacaoEntity.setDescricao( transacao.getDescricao() );
        transacaoEntity.setValor( transacao.getValor() );
        transacaoEntity.setDataHora( transacao.getDataHora() );

        return transacaoEntity;
    }

    @Override
    public Transacao toDomain(TransacaoEntity transacaoEntityEntity) {
        if ( transacaoEntityEntity == null ) {
            return null;
        }

        CartaoDeCredito cartao = null;
        String descricao = null;
        BigDecimal valor = null;
        LocalDateTime dataHora = null;

        cartao = cartaoDeCreditoEntityToCartaoDeCredito( transacaoEntityEntity.getCartao() );
        descricao = transacaoEntityEntity.getDescricao();
        valor = transacaoEntityEntity.getValor();
        dataHora = transacaoEntityEntity.getDataHora();

        Transacao transacao = new Transacao( descricao, valor, cartao, dataHora );

        transacao.setId( transacaoEntityEntity.getId() );

        return transacao;
    }

    @Override
    public Transacao toDomain(TransacaoDTO transacaoDTO) {
        if ( transacaoDTO == null ) {
            return null;
        }

        CartaoDeCredito cartao = null;
        String descricao = null;
        BigDecimal valor = null;
        LocalDateTime dataHora = null;

        cartao = transacaoDTOToCartaoDeCredito( transacaoDTO );
        descricao = transacaoDTO.getDescricao();
        valor = transacaoDTO.getValor();
        dataHora = transacaoDTO.getDataHora();

        Transacao transacao = new Transacao( descricao, valor, cartao, dataHora );

        return transacao;
    }

    @Override
    public TransacaoDTO toDTO(Transacao transacao) {
        if ( transacao == null ) {
            return null;
        }

        TransacaoDTO transacaoDTO = new TransacaoDTO();

        transacaoDTO.setCartaoId( transacaoCartaoId( transacao ) );
        transacaoDTO.setDescricao( transacao.getDescricao() );
        transacaoDTO.setValor( transacao.getValor() );
        transacaoDTO.setDataHora( transacao.getDataHora() );

        return transacaoDTO;
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

    protected CartaoDeCredito transacaoDTOToCartaoDeCredito(TransacaoDTO transacaoDTO) {
        if ( transacaoDTO == null ) {
            return null;
        }

        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();

        cartaoDeCredito.setId( transacaoDTO.getCartaoId() );

        return cartaoDeCredito;
    }

    private UUID transacaoCartaoId(Transacao transacao) {
        if ( transacao == null ) {
            return null;
        }
        CartaoDeCredito cartao = transacao.getCartao();
        if ( cartao == null ) {
            return null;
        }
        UUID id = cartao.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
