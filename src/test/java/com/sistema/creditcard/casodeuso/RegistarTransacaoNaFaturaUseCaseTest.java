package com.sistema.creditcard.casodeuso;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.entidade.CartaoDeCreditoTestFactory;
import com.sistema.creditcard.dominio.entidade.Fatura;
import com.sistema.creditcard.dominio.entidade.Transacao;
import com.sistema.creditcard.dominio.repository.FaturaRepository;
import com.sistema.creditcard.dominio.servico.FaturaService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

@QuarkusTest
public class RegistarTransacaoNaFaturaUseCaseTest {

    @InjectMock
    FaturaRepository faturaRepository;

    FaturaService faturaService;

    @Inject
    RegistarTransacaoNaFaturaUseCase registarTransacaoNaFaturaUseCase;

    @BeforeEach
    public void setup() {
        // Cria os mocks para as dependências
        faturaService = Mockito.mock(FaturaService.class);

    }

    @Tag("uc-fatura")
    @Test
    public void testRegistarTransacaoNaFatura_newFatura(){
        //Arrange: Cria uma transação com data/pré definida para janeiro de 2024
        LocalDateTime dataHora = LocalDateTime.of(2024, 1, 10, 10, 0);
        CreditCard cartao = CartaoDeCreditoTestFactory.criaCartaoValido();
        Transacao transacao = new Transacao("Compra A", new BigDecimal("100.00"), cartao, dataHora);

        //O período da transação (primeiro dia do mês)
        LocalDate periodoTransacao = dataHora.toLocalDate().withDayOfMonth(1);

        // Configura o repositório para retornar Optional.empty(), simulando que não existe fatura para o período
        when(faturaRepository.findByMesAno(periodoTransacao)).thenReturn(Optional.empty());

        // Simula o comportamento do serviço: quando adicionar a transação à fatura, ele chama o método addTransacao do objeto fatura
        doAnswer (invocation -> {
            Fatura faturaArg = invocation.getArgument(0);
            Transacao transacaoArg = invocation.getArgument(1);
            faturaArg.addTransacao(transacaoArg);
            return null;
        }).when(faturaService).adicionarTransacao(any(Fatura.class), any(Transacao.class));

        //Simula que o método save no repositório retorna a própia fatura passada
        when(faturaRepository.save(any(Fatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Executa o caso de uso para registrar a transação da fatura

        Fatura result = registarTransacaoNaFaturaUseCase.registarTransacaoNaFatura(transacao);

        // Assert: Verifica se a fatura retornada está correta
        assertNotNull(result, "A fatura não deve ser nula");
        assertEquals(periodoTransacao, result.getMesAno(), "O período da fatura deve ser o primeiro dia do mês da transação");
        assertEquals(1, result.getTransacoes().size(), "A Transação adicionada de ser a mesma enviada");
        assertEquals(transacao, result.getTransacoes().get(0), "A transação adicionada deve ser a mesma enviada");
        assertEquals(new BigDecimal("100.00"), result.getTotal(), "O total da fatura deve ser 100");

        BigDecimal expectedPagamentoMinimo = new BigDecimal("100.00")
                .multiply(new BigDecimal("0.15"))
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedPagamentoMinimo, result.getPagamentoMinimo(), "O pagamento mínimo de ser 15% do total, arrendondado para 2 duas casas decimal");

    }

    @Tag("uc-fatura")
    @Test
    public void testRegistrarTransacaoNaFatura_ExistingFatura(){
    // Arrange: Cria uma transação para Janeiro de 2024
        LocalDateTime dataHora = LocalDateTime.of(2024, 1, 15, 12, 0);
        CreditCard cartao = CartaoDeCreditoTestFactory.criaCartaoValido();
        Transacao transacao = new Transacao("Compra B", new BigDecimal("50.00"), cartao, dataHora);

        LocalDate periodoTransacao = dataHora.toLocalDate().withDayOfMonth(1);

        // Cria uma fatura já existente para o mesmo período
        Fatura existingFatura = new Fatura(periodoTransacao);

        // Configura o repositório para retornar a fatura existente
        when(faturaRepository.findByMesAno(periodoTransacao)).thenReturn(Optional.of(existingFatura));

        // Simula o comportamento do serviço, adicionando a transação à fatura existente
        doAnswer(invocation -> {
            Fatura faturaArg = invocation.getArgument(0);
            Transacao transacaoArg = invocation.getArgument(1);
            faturaArg.addTransacao(transacaoArg);
            return null;
        }).when(faturaService).adicionarTransacao(any(Fatura.class), any(Transacao.class));

        // Simula que o método save retorna a fatura desejada
        when(faturaRepository.save(any(Fatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act: Executa o caso de uso
        Fatura result = registarTransacaoNaFaturaUseCase.registarTransacaoNaFatura(transacao);

        // Assert: Verifica se a fatura retornada é a mesma fatura existente e que a transação foi adicionada
        assertSame(existingFatura, result, "A fatura retornada deve ser a fatura existente");
        assertEquals(1, result.getTransacoes().size(), "A fatura deve conter 1 transação" );
        assertEquals(transacao, result.getTransacoes().get(0), "A transação adicionada deve ser a mesma enviada");
        assertEquals(new BigDecimal("50.00"), result.getTotal(), "O total da fatura deve ser 50.00");

        BigDecimal expectedPagamentoMinimo = new BigDecimal("50.00")
                .multiply(new BigDecimal("0.15"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}


