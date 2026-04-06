package com.projetorobo.etapa2;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.GeradorCampo;
import com.projetorobo.etapa2.service.MovimentoInteligenteService;
import com.projetorobo.etapa2.service.MovimentoFeromonioService;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainEtapa2 {
    private static final AtomicBoolean continuar = new AtomicBoolean(true);
    
    public static void executar() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║       ETAPA 2: MOVIMENTO INTELIGENTE                ║");
        System.out.println("║  Com obstáculos - Algoritmos de navegação          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        
        System.out.println("\n► ESCOLHA O MODO:");
        System.out.println("  [1] Modo BURRO       - Prefere não visitadas (simples)");
        System.out.println("  [2] Modo INTELIGENTE - Sistema de feromônios (ACO)");
        System.out.print("\nOpção: ");
        
        int modo = scanner.nextInt();
        scanner.nextLine();
        
        if (modo != 1 && modo != 2) {
            System.out.println("✗ Opção inválida! Usando modo BURRO.\n");
            modo = 1;
        }
        
        Campo campo = GeradorCampo.criarCampoEtapa2();
        Robo robo = new Robo(0, 0);
        
        campo.getCasa(0, 0).setTipo(TipoCelula.OCUPADA);
        campo.getCasa(9, 0).setTipo(TipoCelula.FINAL);
        
        if (modo == 1) {
            executarModoBurro(campo, robo);
        } else {
            executarModoInteligente(campo, robo);
        }
        
        continuar.set(true);
    }
    
    private static void executarModoBurro(Campo campo, Robo robo) {
        MovimentoInteligenteService movimentoService = new MovimentoInteligenteService();
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              MODO BURRO ATIVADO                     ║");
        System.out.println("║  Lógica: Prefere não visitadas → se não, visitadas ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        
        System.out.println("\n► ESTADO INICIAL");
        System.out.println("  Posição: " + robo);
        System.out.println("  Objetivo: Chegar em (9, 0)");
        campo.exibirCampo();
        
        System.out.println("► O robô começará a se mover automaticamente.");
        System.out.println("► Digite 'n' e pressione ENTER para parar.\n");
        
        Thread inputThread = criarThreadInput();
        inputThread.start();
        
        pausar(2000);
        
        int contadorMovimentos = 0;
        
        while (continuar.get()) {
            contadorMovimentos++;
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("                  MOVIMENTO #" + contadorMovimentos);
            System.out.println("═══════════════════════════════════════════════════════");
            
            boolean movimentoRealizado = movimentoService.moverInteligente(campo, robo);
            
            if (movimentoRealizado) {
                System.out.println("\n✓ Movimento realizado!");
                System.out.println("  Nova posição: " + robo);
                
                if (movimentoService.verificarChegadaAoFinal(campo, robo)) {
                    System.out.println("\n╔══════════════════════════════════════════════════════╗");
                    System.out.println("║            ROBÔ CHEGOU AO DESTINO FINAL!            ║");
                    System.out.println("╚══════════════════════════════════════════════════════╝");
                    campo.exibirCampo();
                    break;
                }
                
                campo.exibirCampo();
                pausar(800);
            } else {
                System.out.println("\n✗ ROBÔ PRESO!");
                campo.exibirCampo();
                break;
            }
        }
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              SIMULAÇÃO ENCERRADA                    ║");
        System.out.println("║    Total de movimentos: " + contadorMovimentos + "                        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
    }
    
    private static void executarModoInteligente(Campo campo, Robo robo) {
        MovimentoFeromonioService movimentoService = new MovimentoFeromonioService();
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║           MODO INTELIGENTE ATIVADO                  ║");
        System.out.println("║  Sistema de Feromônios - Inspirado em Formigas     ║");
        System.out.println("║  Lógica: Escolhe vizinho com MENOR contador        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        
        System.out.println("\n► ESTADO INICIAL");
        System.out.println("  Posição: " + robo);
        System.out.println("  Objetivo: Chegar em (9, 0)");
        campo.exibirCampo();
        
        System.out.println("► O robô começará a se mover automaticamente.");
        System.out.println("► Digite 'n' e pressione ENTER para parar.");
        System.out.println("► [Mapa de Calor exibe contador de visitas]\n");
        
        Thread inputThread = criarThreadInput();
        inputThread.start();
        
        pausar(2000);
        
        int contadorMovimentos = 0;
        
        while (continuar.get()) {
            contadorMovimentos++;
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("                  MOVIMENTO #" + contadorMovimentos);
            System.out.println("═══════════════════════════════════════════════════════");
            
            boolean movimentoRealizado = movimentoService.moverComFeromonio(campo, robo);
            
            if (movimentoRealizado) {
                System.out.println("\n✓ Movimento realizado!");
                System.out.println("  Nova posição: " + robo);
                
                exibirMapaCalor(campo, robo);
                
                if (movimentoService.verificarChegadaAoFinal(campo, robo)) {
                    System.out.println("\n╔══════════════════════════════════════════════════════╗");
                    System.out.println("║            ROBÔ CHEGOU AO DESTINO FINAL!            ║");
                    System.out.println("╚══════════════════════════════════════════════════════╝");
                    campo.exibirCampo();
                    exibirMapaCalor(campo, robo);
                    break;
                }
                
                campo.exibirCampo();
                pausar(800);
            } else {
                System.out.println("\n✗ ROBÔ PRESO!");
                campo.exibirCampo();
                break;
            }
        }
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              SIMULAÇÃO ENCERRADA                    ║");
        System.out.println("║    Total de movimentos: " + contadorMovimentos + "                        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
    }
    
    private static Thread criarThreadInput() {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (continuar.get()) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim().toLowerCase();
                    if (input.equals("n")) {
                        continuar.set(false);
                        System.out.println("\n✓ Parando simulação...");
                        break;
                    }
                }
            }
        });
        inputThread.setDaemon(true);
        return inputThread;
    }
    
    private static void exibirMapaCalor(Campo campo, Robo robo) {
        System.out.println("\n  Mapa de Feromônios (contador de visitas):");
        System.out.print("  ");
        for (int j = 0; j < campo.getColunas(); j++) {
            System.out.printf(" %2d ", j);
        }
        System.out.println();
        
        for (int i = 0; i < campo.getLinhas(); i++) {
            System.out.printf("%2d ", i);
            for (int j = 0; j < campo.getColunas(); j++) {
                Casa casa = campo.getCasa(i, j);
                
                if (i == robo.getPosicaoX() && j == robo.getPosicaoY()) {
                    System.out.print(" O  ");
                } else {
                    switch (casa.getTipo()) {
                        case PAREDE:
                            System.out.print(" ## ");
                            break;
                        case FINAL:
                            System.out.print(" F  ");
                            break;
                        default:
                            int contador = casa.getContadorVisitas();
                            if (contador == 0) {
                                System.out.print("  . ");
                            } else {
                                System.out.printf(" %2d ", contador);
                            }
                    }
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
