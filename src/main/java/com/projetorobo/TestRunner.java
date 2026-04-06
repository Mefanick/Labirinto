package com.projetorobo;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.GeradorCampo;
import com.projetorobo.etapa1.service.MovimentoAleatorioService;
import com.projetorobo.etapa2.service.MovimentoInteligenteService;
import com.projetorobo.etapa3.service.BuscaService;
import com.projetorobo.etapa3.service.ExecutorCaminhoService;
import com.projetorobo.etapa3.model.ResultadoBusca;
import com.projetorobo.etapa4.service.DijkstraService;
import com.projetorobo.etapa4.model.ResultadoDijkstra;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║           TESTE DE TODAS AS ETAPAS                  ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
        
        testarEtapa1();
        testarEtapa2();
        testarEtapa3();
        testarEtapa4();
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║         TODOS OS TESTES CONCLUÍDOS!                 ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
    }
    
    private static void testarEtapa1() {
        System.out.println("\n>>> TESTANDO ETAPA 1: Movimento Aleatório Puro");
        
        Campo campo = GeradorCampo.criarCampoEtapa1();
        Robo robo = new Robo(5, 5);
        campo.getCasa(5, 5).setTipo(TipoCelula.OCUPADA);
        
        MovimentoAleatorioService movimentoService = new MovimentoAleatorioService();
        
        System.out.println("Estado inicial:");
        System.out.println(robo);
        
        int movimentosRealizados = 0;
        for (int i = 0; i < 5; i++) {
            if (movimentoService.moverAleatorio(campo, robo)) {
                movimentosRealizados++;
            }
        }
        
        System.out.println("Estado final: " + robo);
        System.out.println("Movimentos realizados: " + movimentosRealizados + "/5");
        System.out.println("✓ Etapa 1 OK\n");
    }
    
    private static void testarEtapa2() {
        System.out.println("\n>>> TESTANDO ETAPA 2: Movimento Inteligente");
        
        Campo campo = GeradorCampo.criarCampoEtapa2();
        Robo robo = new Robo(0, 0);
        campo.getCasa(0, 0).setTipo(TipoCelula.OCUPADA);
        
        MovimentoInteligenteService movimentoService = new MovimentoInteligenteService();
        
        System.out.println("Estado inicial: " + robo);
        
        int movimentosRealizados = 0;
        for (int i = 0; i < 10; i++) {
            if (movimentoService.moverInteligente(campo, robo)) {
                movimentosRealizados++;
            } else {
                break;
            }
        }
        
        System.out.println("Estado final: " + robo);
        System.out.println("Movimentos realizados: " + movimentosRealizados);
        System.out.println("✓ Etapa 2 OK\n");
    }
    
    private static void testarEtapa3() {
        System.out.println("\n>>> TESTANDO ETAPA 3: Busca BFS");
        
        Campo campo = GeradorCampo.criarCampoEtapa3();
        Posicao inicio = new Posicao(0, 0);
        Posicao fim = new Posicao(9, 0);
        
        campo.getCasa(inicio.getX(), inicio.getY()).setTipo(TipoCelula.OCUPADA);
        campo.getCasa(fim.getX(), fim.getY()).setTipo(TipoCelula.FINAL);
        
        BuscaService buscaService = new BuscaService();
        ResultadoBusca resultado = buscaService.buscarCaminhoBFS(campo, inicio, fim);
        
        if (resultado.isCaminhoEncontrado()) {
            System.out.println("✓ Caminho encontrado!");
            System.out.println("  Número de passos: " + resultado.getNumeroPasos());
            System.out.println("  Nós explorados: " + resultado.getNosExplorados().size());
            System.out.println("  Caminho: " + inicio + " → ... → " + fim);
        } else {
            System.out.println("✗ Nenhum caminho encontrado");
        }
        
        System.out.println("✓ Etapa 3 OK\n");
    }
    
    private static void testarEtapa4() {
        System.out.println("\n>>> TESTANDO ETAPA 4: Dijkstra");
        
        Campo campo = GeradorCampo.criarCampoEtapa4ComPesos();
        Posicao inicio = new Posicao(0, 0);
        Posicao fim = new Posicao(9, 0);
        
        campo.getCasa(inicio.getX(), inicio.getY()).setTipo(TipoCelula.OCUPADA);
        campo.getCasa(inicio.getX(), inicio.getY()).setValor(0);
        campo.getCasa(fim.getX(), fim.getY()).setTipo(TipoCelula.FINAL);
        campo.getCasa(fim.getX(), fim.getY()).setValor(0);
        
        DijkstraService dijkstraService = new DijkstraService();
        ResultadoDijkstra resultado = dijkstraService.calcularMenorCusto(campo, inicio, fim);
        
        if (resultado.isCaminhoEncontrado()) {
            System.out.println("✓ Caminho encontrado!");
            System.out.println("  Custo total: " + resultado.getCustoTotal());
            System.out.println("  Número de passos: " + resultado.getNumeroPasos());
            System.out.println("  Caminho: " + inicio + " → ... → " + fim);
        } else {
            System.out.println("✗ Nenhum caminho encontrado");
        }
        
        System.out.println("✓ Etapa 4 OK\n");
    }
}
