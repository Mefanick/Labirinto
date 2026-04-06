package com.projetorobo.etapa4.model;

import com.projetorobo.core.model.Posicao;
import java.util.List;

public class ResultadoDijkstra {
    private final List<Posicao> caminho;
    private final boolean caminhoEncontrado;
    private final int custoTotal;
    private final int numeroPasos;

    public ResultadoDijkstra(List<Posicao> caminho, boolean caminhoEncontrado, int custoTotal) {
        this.caminho = caminho;
        this.caminhoEncontrado = caminhoEncontrado;
        this.custoTotal = custoTotal;
        this.numeroPasos = caminho != null ? caminho.size() - 1 : 0;
    }

    public static ResultadoDijkstra semCaminho() {
        return new ResultadoDijkstra(null, false, Integer.MAX_VALUE);
    }

    public List<Posicao> getCaminho() {
        return caminho;
    }

    public boolean isCaminhoEncontrado() {
        return caminhoEncontrado;
    }

    public int getCustoTotal() {
        return custoTotal;
    }

    public int getNumeroPasos() {
        return numeroPasos;
    }
}
