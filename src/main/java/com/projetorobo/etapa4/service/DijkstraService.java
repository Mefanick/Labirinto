package com.projetorobo.etapa4.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.util.ValidadorMovimento;
import com.projetorobo.etapa4.model.NoCaminho;
import com.projetorobo.etapa4.model.ResultadoDijkstra;

import java.util.*;

public class DijkstraService {
    
    public ResultadoDijkstra calcularMenorCusto(Campo campo, Posicao inicio, Posicao fim) {
        PriorityQueue<NoCaminho> filaPrioridade = new PriorityQueue<>();
        Map<Posicao, Integer> custos = new HashMap<>();
        Map<Posicao, Posicao> predecessores = new HashMap<>();
        Set<Posicao> visitados = new HashSet<>();
        
        filaPrioridade.add(new NoCaminho(inicio, 0, null));
        custos.put(inicio, 0);
        
        while (!filaPrioridade.isEmpty()) {
            NoCaminho noAtual = filaPrioridade.poll();
            Posicao posicaoAtual = noAtual.getPosicao();
            
            if (visitados.contains(posicaoAtual)) {
                continue;
            }
            
            visitados.add(posicaoAtual);
            
            if (posicaoAtual.equals(fim)) {
                List<Posicao> caminho = reconstruirCaminho(predecessores, inicio, fim);
                int custoTotal = custos.get(fim);
                return new ResultadoDijkstra(caminho, true, custoTotal);
            }
            
            List<Posicao> vizinhos = ValidadorMovimento.obterVizinhosValidos(campo, posicaoAtual);
            
            for (Posicao vizinho : vizinhos) {
                if (visitados.contains(vizinho)) {
                    continue;
                }
                
                Casa casaVizinha = campo.getCasa(vizinho.getX(), vizinho.getY());
                int custoVizinho = casaVizinha.getValor();
                if (custoVizinho == 0) custoVizinho = 1;
                
                int novoCusto = custos.get(posicaoAtual) + custoVizinho;
                
                if (novoCusto < custos.getOrDefault(vizinho, Integer.MAX_VALUE)) {
                    custos.put(vizinho, novoCusto);
                    predecessores.put(vizinho, posicaoAtual);
                    filaPrioridade.add(new NoCaminho(vizinho, novoCusto, noAtual));
                }
            }
        }
        
        return ResultadoDijkstra.semCaminho();
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
