package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.CartaoDeCreditoDTO;
import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-10T21:35:30-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (JetBrains s.r.o.)"
)
@ApplicationScoped
public class CartaoDeCreditoMapperImpl implements CartaoDeCreditoMapper {

    @Override
    public CartaoDeCreditoEntity toEntity(CartaoDeCredito cartao) {
        if ( cartao == null ) {
            return null;
        }

        CartaoDeCreditoEntity cartaoDeCreditoEntity = new CartaoDeCreditoEntity();

        cartaoDeCreditoEntity.setCliente( clienteToClienteEntity( cartao.getCliente() ) );
        cartaoDeCreditoEntity.setId( cartao.getId() );
        cartaoDeCreditoEntity.setNumero( cartao.getNumero() );
        cartaoDeCreditoEntity.setBandeira( cartao.getBandeira() );
        cartaoDeCreditoEntity.setNomeTitular( cartao.getNomeTitular() );
        cartaoDeCreditoEntity.setDataValidade( cartao.getDataValidade() );
        cartaoDeCreditoEntity.setCvv( cartao.getCvv() );
        cartaoDeCreditoEntity.setLimiteTotal( cartao.getLimiteTotal() );
        cartaoDeCreditoEntity.setLimiteDisponivel( cartao.getLimiteDisponivel() );
        cartaoDeCreditoEntity.setSaldoDevedor( cartao.getSaldoDevedor() );
        cartaoDeCreditoEntity.setAtivo( cartao.isAtivo() );
        cartaoDeCreditoEntity.setBloqueadoPorPerdaOuRoubo( cartao.isBloqueadoPorPerdaOuRoubo() );

        return cartaoDeCreditoEntity;
    }

    @Override
    public CartaoDeCredito toDomain(CartaoDeCreditoEntity cartaoEntity) {
        if ( cartaoEntity == null ) {
            return null;
        }

        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();

        cartaoDeCredito.setCliente( clienteEntityToCliente( cartaoEntity.getCliente() ) );
        cartaoDeCredito.setId( cartaoEntity.getId() );
        cartaoDeCredito.setNumero( cartaoEntity.getNumero() );
        cartaoDeCredito.setBandeira( cartaoEntity.getBandeira() );
        cartaoDeCredito.setNomeTitular( cartaoEntity.getNomeTitular() );
        cartaoDeCredito.setCvv( cartaoEntity.getCvv() );
        cartaoDeCredito.setDataValidade( cartaoEntity.getDataValidade() );
        cartaoDeCredito.setLimiteTotal( cartaoEntity.getLimiteTotal() );
        cartaoDeCredito.setLimiteDisponivel( cartaoEntity.getLimiteDisponivel() );
        cartaoDeCredito.setSaldoDevedor( cartaoEntity.getSaldoDevedor() );
        cartaoDeCredito.setAtivo( cartaoEntity.isAtivo() );
        cartaoDeCredito.setBloqueadoPorPerdaOuRoubo( cartaoEntity.isBloqueadoPorPerdaOuRoubo() );

        return cartaoDeCredito;
    }

    @Override
    public CartaoDeCredito toDomain(CartaoDeCreditoDTO cartaoDeCreditoDTO) {
        if ( cartaoDeCreditoDTO == null ) {
            return null;
        }

        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();

        cartaoDeCredito.setCliente( cartaoDeCreditoDTOToCliente( cartaoDeCreditoDTO ) );
        cartaoDeCredito.setId( cartaoDeCreditoDTO.getId() );
        cartaoDeCredito.setNumero( cartaoDeCreditoDTO.getNumero() );
        cartaoDeCredito.setBandeira( cartaoDeCreditoDTO.getBandeira() );
        cartaoDeCredito.setNomeTitular( cartaoDeCreditoDTO.getNomeTitular() );
        cartaoDeCredito.setCvv( cartaoDeCreditoDTO.getCvv() );
        cartaoDeCredito.setDataValidade( cartaoDeCreditoDTO.getDataValidade() );
        cartaoDeCredito.setLimiteTotal( cartaoDeCreditoDTO.getLimiteTotal() );
        cartaoDeCredito.setLimiteDisponivel( cartaoDeCreditoDTO.getLimiteDisponivel() );
        cartaoDeCredito.setSaldoDevedor( cartaoDeCreditoDTO.getSaldoDevedor() );
        cartaoDeCredito.setAtivo( cartaoDeCreditoDTO.isAtivo() );
        cartaoDeCredito.setBloqueadoPorPerdaOuRoubo( cartaoDeCreditoDTO.isBloqueadoPorPerdaOuRoubo() );

        return cartaoDeCredito;
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

    protected Cliente cartaoDeCreditoDTOToCliente(CartaoDeCreditoDTO cartaoDeCreditoDTO) {
        if ( cartaoDeCreditoDTO == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        if ( cartaoDeCreditoDTO.getClienteId() != null ) {
            cliente.setId( UUID.fromString( cartaoDeCreditoDTO.getClienteId() ) );
        }

        return cliente;
    }
}
