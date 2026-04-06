package com.projetorobo.etapa1.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.service.MovimentoBaseService;
import com.projetorobo.core.util.ValidadorMovimento;

import java.util.Random;

public class MovimentoLinhaRetaService extends MovimentoBaseService {
    private final Random random;
    private int direcaoAtual; // 0=cima, 1=direita, 2=baixo, 3=esquerda
    private boolean primeiroDirecao;
    
    public MovimentoLinhaRetaService() {
        this.random = new Random();
        this.primeiroDirecao = true;
    }
    
    public boolean moverLinhaReta(Campo campo, Robo robo) {
        if (primeiroDirecao) {
            direcaoAtual = random.nextInt(4);
            primeiroDirecao = false;
        }
        
        int tentativas = 0;
        boolean moveu = false;
        
        while (tentativas < 4 && !moveu) {
            int novoX = robo.getPosicaoX();
            int novoY = robo.getPosicaoY();
            
            switch (direcaoAtual) {
                case 0: novoX--; break; // Cima
                case 1: novoY++; break; // Direita
                case 2: novoX++; break; // Baixo
                case 3: novoY--; break; // Esquerda
            }
            
            if (ValidadorMovimento.podeAcessar(campo, novoX, novoY)) {
                atualizarCampo(campo, robo, novoX, novoY);
                moveu = true;
            } else {
                direcaoAtual = random.nextInt(4);
                tentativas++;
            }
        }
        
        return moveu;
    }
    
    public void resetarDirecao() {
        primeiroDirecao = true;
    }
}
