package com.projetorobo.etapa3.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.ValidadorMovimento;
import com.projetorobo.etapa3.model.ResultadoBusca;

import java.util.*;

public class BuscaVisualService {
    
    public ResultadoBusca buscarCaminhoBFSVisual(Campo campo, Posicao inicio, Posicao fim, int delayMs) {
        Queue<Posicao> fila = new LinkedList<>();
        Map<Posicao, Posicao> predecessores = new HashMap<>();
        Set<Posicao> visitadas = new HashSet<>();
        
        fila.add(inicio);
        visitadas.add(inicio);
        predecessores.put(inicio, null);
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║         EXPLORANDO COM BFS - BUSCA EM LARGURA       ║");
        System.out.println("║  Expandindo em ondas até encontrar o destino...     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
        
        int iteracao = 0;
        
        while (!fila.isEmpty()) {
            Posicao atual = fila.poll();
            iteracao++;
            
            System.out.println("───────────────────────────────────────────────────────");
            System.out.println("  Iteração #" + iteracao + " | Explorando: " + atual);
            System.out.println("  Fila: " + fila.size() + " posições | Visitadas: " + visitadas.size());
            System.out.println("───────────────────────────────────────────────────────");
            
            if (atual.equals(fim)) {
                System.out.println("\n✓ DESTINO ENCONTRADO em " + atual + "!");
                List<Posicao> caminho = reconstruirCaminho(predecessores, inicio, fim);
                
                System.out.println("\n╔══════════════════════════════════════════════════════╗");
                System.out.println("║              CAMINHO ÓTIMO ENCONTRADO!              ║");
                System.out.println("║    Número de passos: " + (caminho.size() - 1) + "                           ║");
                System.out.println("╚══════════════════════════════════════════════════════╝");
                
                exibirCampoComCaminho(campo, inicio, fim, visitadas, caminho);
                pausar(2000);
                
                return new ResultadoBusca(caminho, true, visitadas);
            }
            
            exibirCampoExploracao(campo, inicio, fim, visitadas, atual);
            pausar(delayMs);
            
            List<Posicao> vizinhos = ValidadorMovimento.obterVizinhosValidos(campo, atual);
            
            for (Posicao vizinho : vizinhos) {
                if (!visitadas.contains(vizinho)) {
                    visitadas.add(vizinho);
                    predecessores.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }
        
        System.out.println("\n✗ NENHUM CAMINHO ENCONTRADO!");
        return ResultadoBusca.semCaminho(visitadas);
    }
    
    private void exibirCampoExploracao(Campo campo, Posicao inicio, Posicao fim, 
                                       Set<Posicao> visitadas, Posicao explorando) {
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
                } else if (visitadas.contains(posAtual)) {
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
        
        System.out.println("\n  Legenda: I=Início | F=Final | ?=Explorando | ·=Visitado | ███=Parede\n");
    }
    
    private void exibirCampoComCaminho(Campo campo, Posicao inicio, Posicao fim,
                                       Set<Posicao> visitadas, List<Posicao> caminho) {
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
                    simbolo = " ★ ";
                } else if (casa.getTipo() == TipoCelula.PAREDE) {
                    simbolo = "███";
                } else if (visitadas.contains(posAtual)) {
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
        
        System.out.println("\n  Legenda: I=Início | F=Final | ★=Caminho | ·=Explorado | ███=Parede\n");
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
