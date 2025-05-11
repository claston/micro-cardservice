package com.sistema.dominio.servico;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Random;

@ApplicationScoped
public class GeradorNumeroCartao {

    public String gerarNumero() {

        Random random = new Random();
        StringBuilder numero = new StringBuilder();

        for(int i = 0; i< 16; i++)
        {
            numero.append(random.nextInt(10)); //gera numeros de 0 a 9
        }

        return numero.toString();
    }
}
