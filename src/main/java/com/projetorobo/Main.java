package com.projetorobo;

import com.projetorobo.etapa1.MainEtapa1;
import com.projetorobo.etapa2.MainEtapa2;
import com.projetorobo.etapa3.MainEtapa3;
import com.projetorobo.etapa4.MainEtapa4;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            exibirMenu();
            
            System.out.print("Escolha uma opção: ");
            
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("\n✗ Opção inválida! Digite um número.\n");
                continue;
            }
            
            int opcao = scanner.nextInt();
            scanner.nextLine();
            
            if (opcao == 0) {
                System.out.println("\n╔══════════════════════════════════════════════════════╗");
                System.out.println("║              ENCERRANDO SIMULADOR                   ║");
                System.out.println("╚══════════════════════════════════════════════════════╝\n");
                break;
            }
            
            switch (opcao) {
                case 1:
                    MainEtapa1.executar();
                    break;
                case 2:
                    MainEtapa2.executar();
                    break;
                case 3:
                    MainEtapa3.executar();
                    break;
                case 4:
                    MainEtapa4.executar();
                    break;
                default:
                    System.out.println("\n✗ Opção inválida! Escolha entre 0 e 4.\n");
            }
            
            System.out.println("\n");
        }
        
        scanner.close();
    }
    
    private static void exibirMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║         SIMULADOR DE ROBÔ - LABIRINTO               ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║                                                      ║");
        System.out.println("║  [1] Etapa 1 - Movimento Aleatório Puro             ║");
        System.out.println("║      • Campo vazio (sem obstáculos)                  ║");
        System.out.println("║      • Movimento 100% aleatório                      ║");
        System.out.println("║                                                      ║");
        System.out.println("║  [2] Etapa 2 - Movimento Inteligente                ║");
        System.out.println("║      • Campo com obstáculos                          ║");
        System.out.println("║      • Prefere casas não visitadas                   ║");
        System.out.println("║                                                      ║");
        System.out.println("║  [3] Etapa 3 - Busca BFS (Menor Caminho)            ║");
        System.out.println("║      • Encontra caminho com menos passos             ║");
        System.out.println("║      • Posições aleatórias                           ║");
        System.out.println("║                                                      ║");
        System.out.println("║  [4] Etapa 4 - Dijkstra (Menor Custo)               ║");
        System.out.println("║      • Casas com pesos diferentes                    ║");
        System.out.println("║      • Caminho otimizado por custo                   ║");
        System.out.println("║                                                      ║");
        System.out.println("║  [0] Sair                                            ║");
        System.out.println("║                                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
