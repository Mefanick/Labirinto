package com.projetorobo.core.model;

public class Robo {
    private int posicaoX;
    private int posicaoY;

    public Robo(int posicaoX, int posicaoY) {
        this.posicaoX = posicaoX;
        this.posicaoY = posicaoY;
    }

    public int getPosicaoX() {
        return posicaoX;
    }

    public int getPosicaoY() {
        return posicaoY;
    }
    
    public Posicao getPosicao() {
        return new Posicao(posicaoX, posicaoY);
    }

    public void setPosicao(int x, int y) {
        this.posicaoX = x;
        this.posicaoY = y;
    }
    
    public void setPosicao(Posicao posicao) {
        this.posicaoX = posicao.getX();
        this.posicaoY = posicao.getY();
    }

    @Override
    public String toString() {
        return String.format("Robô na posição (%d, %d)", posicaoX, posicaoY);
    }
}
