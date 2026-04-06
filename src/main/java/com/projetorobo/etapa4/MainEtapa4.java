package com.projetorobo.etapa4;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.GeradorCampo;
import com.projetorobo.etapa3.service.ExecutorCaminhoService;
import com.projetorobo.etapa4.service.DijkstraVisualService;
import com.projetorobo.etapa4.model.ResultadoDijkstra;

public class MainEtapa4 {
    public static void executar() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║     ETAPA 4: DIJKSTRA - CAMINHO DE MENOR CUSTO     ║");
        System.out.println("║  Cada casa tem um peso - Encontra menor custo      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        
        Campo campo = GeradorCampo.criarCampoEtapa4ComPesos();
        Posicao posicaoInicial = new Posicao(0, 5);
        Posicao posicaoFinal = new Posicao(9, 5);
        
        campo.getCasa(posicaoInicial.getX(), posicaoInicial.getY()).setTipo(TipoCelula.OCUPADA);
        campo.getCasa(posicaoInicial.getX(), posicaoInicial.getY()).setValor(0);
        campo.getCasa(posicaoFinal.getX(), posicaoFinal.getY()).setTipo(TipoCelula.FINAL);
        campo.getCasa(posicaoFinal.getX(), posicaoFinal.getY()).setValor(0);
        
        Robo robo = new Robo(posicaoInicial.getX(), posicaoInicial.getY());
        
        System.out.println("\n► CONFIGURAÇÃO");
        System.out.println("  Início: " + posicaoInicial);
        System.out.println("  Final: " + posicaoFinal);
        System.out.println("\n► MAPA DE CUSTOS:");
        exibirMapaCustos(campo);
        campo.exibirCampo();
        
        pausar(1500);
        
        DijkstraVisualService dijkstraService = new DijkstraVisualService();
        
        ResultadoDijkstra resultado = dijkstraService.calcularMenorCustoVisual(campo, posicaoInicial, posicaoFinal, 150);
        
        if (resultado.isCaminhoEncontrado()) {
            System.out.println("\n► AGORA O ROBÔ EXECUTARÁ O CAMINHO ENCONTRADO...\n");
            pausar(1000);
            
            ExecutorCaminhoService executor = new ExecutorCaminhoService();
            campo.resetarTodasVisitacoes();
            
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║            EXECUTANDO CAMINHO DIJKSTRA              ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            
            executor.executarCaminhoComDelay(campo, robo, resultado.getCaminho(), 300);
            
            System.out.println("\n╔══════════════════════════════════════════════════════╗");
            System.out.println("║            ROBÔ CHEGOU AO DESTINO FINAL!            ║");
            System.out.println("║          Custo total: " + resultado.getCustoTotal() + "                            ║");
            System.out.println("╚══════════════════════════════════════════════════════╝\n");
            
        } else {
            System.out.println("✗ NENHUM CAMINHO ENCONTRADO!");
        }
    }
    
    private static void exibirMapaCustos(Campo campo) {
        for (int i = 0; i < campo.getLinhas(); i++) {
            System.out.print("  ");
            for (int j = 0; j < campo.getColunas(); j++) {
                int valor = campo.getCasa(i, j).getValor();
                TipoCelula tipo = campo.getCasa(i, j).getTipo();
                
                if (tipo == TipoCelula.PAREDE) {
                    System.out.print(" ## ");
                } else if (tipo == TipoCelula.OCUPADA) {
                    System.out.print(" O  ");
                } else if (tipo == TipoCelula.FINAL) {
                    System.out.print(" F  ");
                } else {
                    System.out.printf(" %2d ", valor);
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    private static void pausar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
