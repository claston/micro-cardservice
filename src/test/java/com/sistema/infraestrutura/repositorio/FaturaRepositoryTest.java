package com.sistema.infraestrutura.repositorio;

import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.entidade.FaturaEntity;
import com.sistema.infraestrutura.entidade.TransacaoEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class FaturaRepositoryTest {

    @Inject
    FaturaRepository faturaRepository;

    @Inject
    CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Inject
    ClienteRepository clienteRepository;

    //TODO: Faz sentido colocar o FaturaEntity para ser injetado?

    @Tag("repository-fatura")
    @Test
    @Transactional
    public void testFindByMesAno() {

        LocalDate mesAno = LocalDate.of(2024, 10, 1);
        FaturaEntity fatura = new FaturaEntity();
        fatura.setMesAno(mesAno);
        fatura.setTotal(new BigDecimal("500.00"));
        //Pagto minimo 15 porcento do total
        fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        faturaRepository.persist(fatura);
        Optional<FaturaEntity> result = faturaRepository.findByMesAno(mesAno);

        assertTrue(result.isPresent(), "A fatura deve ser encontrada pelo mês/ano informado");
        FaturaEntity faturaRecuperada = result.get();
        assertNotNull(faturaRecuperada.getId());

        assertEquals(new BigDecimal("500.00"), faturaRecuperada.getTotal(), "O total da Fatura deve ser 500.00");
        assertEquals(mesAno, faturaRecuperada.getMesAno(), "A data deve corresponder à data definida");

    }

    @Tag("repository-fatura-2")
    @Test
    @Transactional
    public void testNovaFaturaEntityComNovaTransacaoEntity() {

        //Arrange:
        LocalDate mesAno = LocalDate.of(2024, 12, 1);
        FaturaEntity fatura = new FaturaEntity();
        fatura.setMesAno(mesAno);
        fatura.setTotal(new BigDecimal("500.00"));
        //Pagto minimo 15 porcento do total
        fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        // Cria um cliente para o cartão;

        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("João da Silva");
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDate.now());
        cliente.setCpf("222.222.222-22");
        cliente.setEmail("teste@teste.com");
        cliente.setTelefone("11 99999999");

        clienteRepository.persist(cliente);

        // Cria cartão de crédito para a Transação
        CartaoDeCreditoEntity cartao = new CartaoDeCreditoEntity();
        cartao.setBandeira("MasterCard");
        cartao.setDataValidade(LocalDate.now().plusYears(5));
        cartao.setNumero("1234567890");
        cartao.setNomeTitular("João da Silva");
        cartao.setLimiteTotal(new BigDecimal("100.00"));
        cartao.setLimiteDisponivel(new BigDecimal("100.00"));
        cartao.setCvv("123");
        cartao.setBloqueadoPorPerdaOuRoubo(false);
        cartao.setCliente(cliente);

        cartaoDeCreditoRepository.persist(cartao);

        // Cria a transação com o cartão de crédito que acabou de ser criado.
        TransacaoEntity transacao = new TransacaoEntity();
        transacao.setDescricao("Compra Supermercado");
        transacao.setValor(new BigDecimal("100.00"));
        transacao.setDataHora(LocalDateTime.now());
        transacao.setCartao(cartao);

        //Adiciona a transacao na fatura
        fatura.addTransacao(transacao);

        //Act
        faturaRepository.persist(fatura);
        Optional<FaturaEntity> result = faturaRepository.findByMesAno(mesAno);


        //Assert

        //Fatura
        assertTrue(result.isPresent(), "A fatura deve ser encontrada pelo mês/ano informado");
        FaturaEntity faturaRecuperada = result.get();
        assertNotNull(faturaRecuperada.getId());
        assertEquals(new BigDecimal("500.00"), faturaRecuperada.getTotal(), "O total da Fatura deve ser 500.00");
        assertEquals(mesAno, faturaRecuperada.getMesAno(), "A data deve corresponder à data definida");

        //Transasao
        assertEquals(1, faturaRecuperada.getTransacoes().size());
        assertNotNull(faturaRecuperada.getTransacoes().get(0).getId());
        assertNotNull(faturaRecuperada.getTransacoes().get(0).getFatura());
        System.out.println("Farura_ID_____________:" + transacao.getFatura().getId());

    }
}