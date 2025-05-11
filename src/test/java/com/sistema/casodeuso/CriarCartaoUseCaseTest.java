package com.sistema.casodeuso;

import com.sistema.adaptadores.dto.CartaoDeCreditoDTO;
import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.dominio.servico.CartaoDeCreditoService;
import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.mapper.CartaoDeCreditoMapper;
import com.sistema.infraestrutura.repositorio.CartaoDeCreditoRepository;
import com.sistema.dominio.repository.CustomerRepository;
import com.sistema.infraestrutura.mapper.ClienteMapper;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
public class CriarCartaoUseCaseTest {

    @InjectMock
    CustomerRepository customerRepository;

    @InjectMock
    CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @InjectMock
    private ClienteMapper clienteMapper;

    @InjectMock
    private CartaoDeCreditoMapper cartaoDeCreditoMapper;

    @InjectMock
    CartaoDeCreditoService cartaoDeCreditoService;

    @Inject
    CriarCartaoUseCase criarCartaoUseCase;

    @Tag("integra")
    @Test
    public void testCriarCartaoDeCreditoValidocomCliente(){

        //mock Cliente
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = new Cliente("João Silva", "1234567890");
        cliente.setId(clienteId);
        when(customerRepository.findById(clienteId)).thenReturn(cliente);

        //mock cartao de credito
        UUID cartaoId = UUID.randomUUID();
        CartaoDeCredito cartaoMock = new CartaoDeCredito(
                "1234567890123456",
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"));

        System.out.println("Recebido bandeira Mock: " + cartaoMock.getBandeira());

        when(cartaoDeCreditoService.criarCartao(
                eq("Mastercard"),
                eq("João Silva"),
                argThat(date -> date.isAfter(LocalDate.now().plusYears(4))),
                eq("123"),
                eq( new BigDecimal("1000.00")),
                eq( new BigDecimal("1000.00")),
                eq(cliente))).thenReturn(cartaoMock);

        when(cartaoDeCreditoService.criarCartao(
                eq("Mastercard"),
                eq("João Silva"),
                argThat(date -> date.isAfter(LocalDate.now().plusYears(4))),
                eq("123"),
                eq( new BigDecimal("1000.00")),
                eq( new BigDecimal("1000.00")))).thenReturn(cartaoMock);

        // Mock do CartaoDeCreditoMapper para converter o cartão de domínio para Entity
        CartaoDeCreditoEntity cartaoEntity = new CartaoDeCreditoEntity();
        cartaoEntity.setId(UUID.randomUUID());
        cartaoEntity.setNumero("1111222233334444");
        cartaoEntity.setNomeTitular("João Silva");
        cartaoEntity.setLimiteTotal(new BigDecimal("1000.00"));
        cartaoEntity.setLimiteDisponivel(new BigDecimal("1000.00"));
        cartaoEntity.setDataValidade(LocalDate.now().plusYears(5));

        when(cartaoDeCreditoMapper.toEntity(cartaoMock)).thenReturn(cartaoEntity);
        when(cartaoDeCreditoMapper.toDomain(cartaoEntity)).thenReturn(cartaoMock);

        doNothing().when(cartaoDeCreditoRepository).persist(cartaoEntity);

        //criar um dto do cartao
        CartaoDeCreditoDTO dto = new CartaoDeCreditoDTO();
        dto.setClienteId(cliente.getId().toString());
        dto.setBandeira("Mastercard");
        dto.setNomeTitular(cliente.getNome());
        dto.setCvv("123");

        //passar o dto do do cartao para o caso de uso
        CartaoDeCredito cartaoAValidar = criarCartaoUseCase.executar(dto);

        //verificar se criou o cartao de credito da forma esperada
        assertNotNull(cartaoAValidar);
        verify(customerRepository, times(1)).findById(clienteId);
        verify(cartaoDeCreditoRepository, times(1)).persist(cartaoEntity);
        
    }
}