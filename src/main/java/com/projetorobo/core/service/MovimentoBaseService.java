package com.projetorobo.core.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.ValidadorMovimento;

public class MovimentoBaseService {
    
    public boolean mover(Campo campo, Robo robo, int deltaX, int deltaY) {
        int novaX = robo.getPosicaoX() + deltaX;
        int novaY = robo.getPosicaoY() + deltaY;

        if (!ValidadorMovimento.podeAcessar(campo, novaX, novaY)) {
            return false;
        }

        atualizarCampo(campo, robo, novaX, novaY);
        return true;
    }
    
    public boolean moverPara(Campo campo, Robo robo, Posicao destino) {
        int deltaX = destino.getX() - robo.getPosicaoX();
        int deltaY = destino.getY() - robo.getPosicaoY();
        return mover(campo, robo, deltaX, deltaY);
    }

    public boolean moverParaCima(Campo campo, Robo robo) {
        return mover(campo, robo, -1, 0);
    }

    public boolean moverParaBaixo(Campo campo, Robo robo) {
        return mover(campo, robo, 1, 0);
    }

    public boolean moverParaEsquerda(Campo campo, Robo robo) {
        return mover(campo, robo, 0, -1);
    }

    public boolean moverParaDireita(Campo campo, Robo robo) {
        return mover(campo, robo, 0, 1);
    }

    protected void atualizarCampo(Campo campo, Robo robo, int novaX, int novaY) {
        int antigaX = robo.getPosicaoX();
        int antigaY = robo.getPosicaoY();
        
        Casa casaAntiga = campo.getCasa(antigaX, antigaY);
        casaAntiga.setTipo(TipoCelula.VAZIA);
        casaAntiga.marcarComoVisitada();

        Casa casaNova = campo.getCasa(novaX, novaY);
        if (casaNova.getTipo() != TipoCelula.FINAL) {
            casaNova.setTipo(TipoCelula.OCUPADA);
        }
        
        robo.setPosicao(novaX, novaY);
    }
    
    public boolean verificarChegadaAoFinal(Campo campo, Robo robo) {
        Casa casaAtual = campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY());
        return casaAtual.getTipo() == TipoCelula.FINAL;
    }
}
