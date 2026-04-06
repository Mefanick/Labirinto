package com.projetorobo.etapa2.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.service.MovimentoBaseService;
import com.projetorobo.core.util.ValidadorMovimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovimentoInteligenteService extends MovimentoBaseService {
    private final Random random;

    public MovimentoInteligenteService() {
        this.random = new Random();
    }

    public MovimentoInteligenteService(long seed) {
        this.random = new Random(seed);
    }

    public boolean moverInteligente(Campo campo, Robo robo) {
        List<Posicao> vizinhosValidos = ValidadorMovimento.obterVizinhosValidos(
            campo, 
            robo.getPosicaoX(), 
            robo.getPosicaoY()
        );

        if (vizinhosValidos.isEmpty()) {
            return false;
        }

        List<Posicao> naoVisitadas = filtrarNaoVisitadas(campo, vizinhosValidos);
        List<Posicao> visitadas = filtrarVisitadas(campo, vizinhosValidos);

        if (!naoVisitadas.isEmpty()) {
            Posicao posicaoEscolhida = naoVisitadas.get(random.nextInt(naoVisitadas.size()));
            return moverPara(campo, robo, posicaoEscolhida);
        } else if (!visitadas.isEmpty()) {
            Posicao posicaoEscolhida = visitadas.get(random.nextInt(visitadas.size()));
            return moverPara(campo, robo, posicaoEscolhida);
        }

        return false;
    }

    private List<Posicao> filtrarNaoVisitadas(Campo campo, List<Posicao> posicoes) {
        List<Posicao> naoVisitadas = new ArrayList<>();
        for (Posicao pos : posicoes) {
            if (!campo.getCasa(pos.getX(), pos.getY()).isVisitada()) {
                naoVisitadas.add(pos);
            }
        }
        return naoVisitadas;
    }

    private List<Posicao> filtrarVisitadas(Campo campo, List<Posicao> posicoes) {
        List<Posicao> visitadas = new ArrayList<>();
        for (Posicao pos : posicoes) {
            if (campo.getCasa(pos.getX(), pos.getY()).isVisitada()) {
                visitadas.add(pos);
            }
        }
        return visitadas;
    }
}
