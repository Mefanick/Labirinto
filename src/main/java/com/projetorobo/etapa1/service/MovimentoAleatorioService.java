package com.projetorobo.etapa1.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.service.MovimentoBaseService;
import com.projetorobo.core.util.ValidadorMovimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovimentoAleatorioService extends MovimentoBaseService {
    private final Random random;

    public MovimentoAleatorioService() {
        this.random = new Random();
    }

    public MovimentoAleatorioService(long seed) {
        this.random = new Random(seed);
    }

    public boolean moverAleatorio(Campo campo, Robo robo) {
        List<Posicao> movimentosPossiveis = ValidadorMovimento.obterVizinhosValidos(
            campo, 
            robo.getPosicaoX(), 
            robo.getPosicaoY()
        );

        if (movimentosPossiveis.isEmpty()) {
            return false;
        }

        Posicao posicaoEscolhida = movimentosPossiveis.get(random.nextInt(movimentosPossiveis.size()));
        return moverPara(campo, robo, posicaoEscolhida);
    }
}
