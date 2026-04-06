package com.projetorobo.etapa3.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.util.ValidadorMovimento;
import com.projetorobo.etapa3.model.ResultadoBusca;

import java.util.*;

public class BuscaService {
    
    public ResultadoBusca buscarCaminhoBFS(Campo campo, Posicao inicio, Posicao fim) {
        Queue<Posicao> fila = new LinkedList<>();
        Map<Posicao, Posicao> predecessores = new HashMap<>();
        Set<Posicao> visitadas = new HashSet<>();
        
        fila.add(inicio);
        visitadas.add(inicio);
        predecessores.put(inicio, null);
        
        while (!fila.isEmpty()) {
            Posicao atual = fila.poll();
            
            if (atual.equals(fim)) {
                List<Posicao> caminho = reconstruirCaminho(predecessores, inicio, fim);
                return new ResultadoBusca(caminho, true, visitadas);
            }
            
            List<Posicao> vizinhos = ValidadorMovimento.obterVizinhosValidos(campo, atual);
            
            for (Posicao vizinho : vizinhos) {
                if (!visitadas.contains(vizinho)) {
                    visitadas.add(vizinho);
                    predecessores.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }
        
        return ResultadoBusca.semCaminho(visitadas);
    }
    
    private List<Posicao> reconstruirCaminho(Map<Posicao, Posicao> predecessores, 
                                             Posicao inicio, Posicao fim) {
        List<Posicao> caminho = new ArrayList<>();
        Posicao atual = fim;
        
        while (atual != null) {
            caminho.add(0, atual);
            atual = predecessores.get(atual);
        }
        
        return caminho;
    }
}
