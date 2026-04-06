package com.projetorobo.etapa1;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.GeradorCampo;
import com.projetorobo.etapa1.service.MovimentoAleatorioService;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainEtapa1 {
    private static final AtomicBoolean continuar = new AtomicBoolean(true);
    
    public static void executar() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║         ETAPA 1: MOVIMENTO ALEATÓRIO PURO           ║");
        System.out.println("║  Campo vazio - Robô se move aleatoriamente          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        
        Campo campo = GeradorCampo.criarCampoEtapa1();
        Robo robo = new Robo(0, 0);
        MovimentoAleatorioService movimentoService = new MovimentoAleatorioService();
        
        campo.getCasa(0, 0).setTipo(TipoCelula.OCUPADA);
        
        System.out.println("\n► ESTADO INICIAL");
        System.out.println("  Posição: " + robo);
        campo.exibirCampo();
        
        System.out.println("► O robô começará a se mover automaticamente.");
        System.out.println("► Digite 'n' e pressione ENTER para parar.\n");
        
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
        inputThread.start();
        
        pausar(2000);
        
        int contadorMovimentos = 0;
        
        while (continuar.get()) {
            contadorMovimentos++;
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("                  MOVIMENTO #" + contadorMovimentos);
            System.out.println("═══════════════════════════════════════════════════════");
            
            boolean movimentoRealizado = movimentoService.moverAleatorio(campo, robo);
            
            if (movimentoRealizado) {
                System.out.println("\n✓ Movimento realizado!");
                System.out.println("  Nova posição: " + robo);
            } else {
                System.out.println("\n✗ Nenhum movimento possível!");
                campo.exibirCampo();
                break;
            }
            
            campo.exibirCampo();
            pausar(800);
        }
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              SIMULAÇÃO ENCERRADA                    ║");
        System.out.println("║    Total de movimentos: " + contadorMovimentos + "                        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
        
        continuar.set(true);
    }
    
    private static void pausar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
