package com.projetorobo.etapa3.model;

import com.projetorobo.core.model.Posicao;
import java.util.List;
import java.util.Set;

public class ResultadoBusca {
    private final List<Posicao> caminho;
    private final boolean caminhoEncontrado;
    private final Set<Posicao> nosExplorados;
    private final int numeroPasos;

    public ResultadoBusca(List<Posicao> caminho, boolean caminhoEncontrado, 
                          Set<Posicao> nosExplorados) {
        this.caminho = caminho;
        this.caminhoEncontrado = caminhoEncontrado;
        this.nosExplorados = nosExplorados;
        this.numeroPasos = caminho != null ? caminho.size() - 1 : 0;
    }

    public static ResultadoBusca semCaminho(Set<Posicao> explorados) {
        return new ResultadoBusca(null, false, explorados);
    }

    public List<Posicao> getCaminho() {
        return caminho;
    }

    public boolean isCaminhoEncontrado() {
        return caminhoEncontrado;
    }

    public Set<Posicao> getNosExplorados() {
        return nosExplorados;
    }

    public int getNumeroPasos() {
        return numeroPasos;
    }
}
