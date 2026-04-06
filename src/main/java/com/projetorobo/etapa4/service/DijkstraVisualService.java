package com.projetorobo.etapa4.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.ValidadorMovimento;
import com.projetorobo.etapa4.model.NoCaminho;
import com.projetorobo.etapa4.model.ResultadoDijkstra;

import java.util.*;

public class DijkstraVisualService {
    
    public ResultadoDijkstra calcularMenorCustoVisual(Campo campo, Posicao inicio, Posicao fim, int delayMs) {
        PriorityQueue<NoCaminho> filaPrioridade = new PriorityQueue<>();
        Map<Posicao, Integer> custos = new HashMap<>();
        Map<Posicao, Posicao> predecessores = new HashMap<>();
        Set<Posicao> visitados = new HashSet<>();
        
        filaPrioridade.add(new NoCaminho(inicio, 0, null));
        custos.put(inicio, 0);
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║      EXPLORANDO COM DIJKSTRA - MENOR CUSTO          ║");
        System.out.println("║  Usando fila de prioridade (menor custo primeiro)  ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
        
        int iteracao = 0;
        
        while (!filaPrioridade.isEmpty()) {
            NoCaminho noAtual = filaPrioridade.poll();
            Posicao posicaoAtual = noAtual.getPosicao();
            
            if (visitados.contains(posicaoAtual)) {
                continue;
            }
            
            visitados.add(posicaoAtual);
            iteracao++;
            
            int custoAtual = custos.get(posicaoAtual);
            
            System.out.println("───────────────────────────────────────────────────────");
            System.out.println("  Iteração #" + iteracao + " | Explorando: " + posicaoAtual);
            System.out.println("  Custo acumulado: " + custoAtual + " | Visitados: " + visitados.size());
            System.out.println("───────────────────────────────────────────────────────");
            
            if (posicaoAtual.equals(fim)) {
                System.out.println("\n✓ DESTINO ENCONTRADO em " + posicaoAtual + "!");
                System.out.println("  Custo total: " + custoAtual);
                
                List<Posicao> caminho = reconstruirCaminho(predecessores, inicio, fim);
                
                System.out.println("\n╔══════════════════════════════════════════════════════╗");
                System.out.println("║           CAMINHO DE MENOR CUSTO ENCONTRADO!        ║");
                System.out.println("║    Custo total: " + custoAtual + "                                  ║");
                System.out.println("╚══════════════════════════════════════════════════════╝");
                
                exibirCampoComCaminho(campo, inicio, fim, visitados, caminho, custos);
                pausar(2000);
                
                return new ResultadoDijkstra(caminho, true, custoAtual);
            }
            
            exibirCampoExploracao(campo, inicio, fim, visitados, posicaoAtual, custos);
            pausar(delayMs);
            
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
        
        System.out.println("\n✗ NENHUM CAMINHO ENCONTRADO!");
        return ResultadoDijkstra.semCaminho();
    }
    
    private void exibirCampoExploracao(Campo campo, Posicao inicio, Posicao fim,
                                       Set<Posicao> visitados, Posicao explorando,
                                       Map<Posicao, Integer> custos) {
        System.out.print("      ");
        for (int j = 0; j < campo.getColunas(); j++) {
            System.out.printf(" %d  ", j);
        }
        System.out.println();
        
        System.out.print("    ┌");
        for (int j = 0; j < campo.getColunas(); j++) {
            System.out.print("───");
            if (j < campo.getColunas() - 1) System.out.print("┬");
        }
        System.out.println("┐");
        
        for (int i = 0; i < campo.getLinhas(); i++) {
            System.out.printf(" %d  │", i);
            
            for (int j = 0; j < campo.getColunas(); j++) {
                Posicao posAtual = new Posicao(i, j);
                Casa casa = campo.getCasa(i, j);
                String simbolo;
                
                if (posAtual.equals(explorando)) {
                    simbolo = " ? ";
                } else if (posAtual.equals(inicio)) {
                    simbolo = " I ";
                } else if (posAtual.equals(fim)) {
                    simbolo = " F ";
                } else if (casa.getTipo() == TipoCelula.PAREDE) {
                    simbolo = "███";
                } else if (visitados.contains(posAtual)) {
                    int custo = custos.getOrDefault(posAtual, 0);
                    if (custo < 10) {
                        simbolo = " " + custo + " ";
                    } else {
                        simbolo = custo + " ";
                    }
                } else {
                    simbolo = "   ";
                }
                
                System.out.print(simbolo);
                if (j < campo.getColunas() - 1) System.out.print("│");
            }
            
            System.out.println("│");
            
            if (i < campo.getLinhas() - 1) {
                System.out.print("    ├");
                for (int j = 0; j < campo.getColunas(); j++) {
                    System.out.print("───");
                    if (j < campo.getColunas() - 1) System.out.print("┼");
                }
                System.out.println("┤");
            }
        }
        
        System.out.print("    └");
        for (int j = 0; j < campo.getColunas(); j++) {
            System.out.print("───");
            if (j < campo.getColunas() - 1) System.out.print("┴");
        }
        System.out.println("┘");
        
        System.out.println("\n  Legenda: I=Início | F=Final | ?=Explorando | Números=Custo | ███=Parede\n");
    }
    
    private void exibirCampoComCaminho(Campo campo, Posicao inicio, Posicao fim,
                                       Set<Posicao> visitados, List<Posicao> caminho,
                                       Map<Posicao, Integer> custos) {
        Set<Posicao> caminhoSet = new HashSet<>(caminho);
        
        System.out.print("      ");
        for (int j = 0; j < campo.getColunas(); j++) {
            System.out.printf(" %d  ", j);
        }
        System.out.println();
        
        System.out.print("    ┌");
        for (int j = 0; j < campo.getColunas(); j++) {
            System.out.print("───");
            if (j < campo.getColunas() - 1) System.out.print("┬");
        }
        System.out.println("┐");
        
        for (int i = 0; i < campo.getLinhas(); i++) {
            System.out.printf(" %d  │", i);
            
            for (int j = 0; j < campo.getColunas(); j++) {
                Posicao posAtual = new Posicao(i, j);
                Casa casa = campo.getCasa(i, j);
                String simbolo;
                
                if (posAtual.equals(inicio)) {
                    simbolo = " I ";
                } else if (posAtual.equals(fim)) {
                    simbolo = " F ";
                } else if (caminhoSet.contains(posAtual)) {
                    int custo = custos.getOrDefault(posAtual, 0);
                    if (custo < 10) {
                        simbolo = " " + custo + " ";
                    } else {
                        simbolo = custo + " ";
                    }
                } else if (casa.getTipo() == TipoCelula.PAREDE) {
                    simbolo = "███";
                } else if (visitados.contains(posAtual)) {
                    simbolo = " · ";
                } else {
                    simbolo = "   ";
                }
                
                System.out.print(simbolo);
                if (j < campo.getColunas() - 1) System.out.print("│");
            }
            
            System.out.println("│");
            
            if (i < campo.getLinhas() - 1) {
                System.out.print("    ├");
                for (int j = 0; j < campo.getColunas(); j++) {
                    System.out.print("───");
                    if (j < campo.getColunas() - 1) System.out.print("┼");
                }
                System.out.println("┤");
            }
        }
        
        System.out.print("    └");
        for (int j = 0; j < campo.getColunas(); j++) {
            System.out.print("───");
            if (j < campo.getColunas() - 1) System.out.print("┴");
        }
        System.out.println("┘");
        
        System.out.println("\n  Legenda: I=Início | F=Final | Números=Caminho | ·=Explorado | ███=Parede\n");
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
    
    private void pausar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
