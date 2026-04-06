package com.projetorobo.etapa2.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.service.MovimentoBaseService;
import com.projetorobo.core.util.ValidadorMovimento;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MovimentoFeromonioService extends MovimentoBaseService {
    private final Random random;
    
    public MovimentoFeromonioService() {
        this.random = new Random();
    }
    
    public boolean moverComFeromonio(Campo campo, Robo robo) {
        List<Posicao> vizinhosValidos = ValidadorMovimento.obterVizinhosValidos(
            campo, 
            robo.getPosicaoX(), 
            robo.getPosicaoY()
        );
        
        if (vizinhosValidos.isEmpty()) {
            return false;
        }
        
        Posicao melhorPosicao = escolherMelhorPosicao(campo, vizinhosValidos);
        
        Casa casaAtual = campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY());
        casaAtual.incrementarVisitas();
        
        int deltaX = melhorPosicao.getX() - robo.getPosicaoX();
        int deltaY = melhorPosicao.getY() - robo.getPosicaoY();
        
        return mover(campo, robo, deltaX, deltaY);
    }
    
    private Posicao escolherMelhorPosicao(Campo campo, List<Posicao> vizinhos) {
        int menorContador = Integer.MAX_VALUE;
        
        for (Posicao pos : vizinhos) {
            Casa casa = campo.getCasa(pos);
            int contador = casa.getContadorVisitas();
            
            if (contador < menorContador) {
                menorContador = contador;
            }
        }
        
        final int contadorMinimo = menorContador;
        List<Posicao> melhoresOpcoes = vizinhos.stream()
            .filter(pos -> campo.getCasa(pos).getContadorVisitas() == contadorMinimo)
            .collect(Collectors.toList());
        
        if (melhoresOpcoes.isEmpty()) {
            return vizinhos.get(random.nextInt(vizinhos.size()));
        }
        
        return melhoresOpcoes.get(random.nextInt(melhoresOpcoes.size()));
    }
}
