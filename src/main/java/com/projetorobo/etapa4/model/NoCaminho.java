package com.projetorobo.etapa4.model;

import com.projetorobo.core.model.Posicao;

public class NoCaminho implements Comparable<NoCaminho> {
    private final Posicao posicao;
    private final int custoAcumulado;
    private final NoCaminho anterior;

    public NoCaminho(Posicao posicao, int custoAcumulado, NoCaminho anterior) {
        this.posicao = posicao;
        this.custoAcumulado = custoAcumulado;
        this.anterior = anterior;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public int getCustoAcumulado() {
        return custoAcumulado;
    }

    public NoCaminho getAnterior() {
        return anterior;
    }

    @Override
    public int compareTo(NoCaminho outro) {
        return Integer.compare(this.custoAcumulado, outro.custoAcumulado);
    }
}
