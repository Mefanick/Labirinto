package com.projetorobo.etapa3;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.GeradorCampo;
import com.projetorobo.etapa3.service.BuscaVisualService;
import com.projetorobo.etapa3.service.ExecutorCaminhoService;
import com.projetorobo.etapa3.model.ResultadoBusca;

public class MainEtapa3 {
    public static void executar() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║      ETAPA 3: BUSCA BFS - MENOR CAMINHO            ║");
        System.out.println("║  Encontra o caminho com menor número de passos     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        
        Campo campo = GeradorCampo.criarCampoEtapa3();
        Posicao posicaoInicial = new Posicao(0, 0);
        Posicao posicaoFinal = new Posicao(9, 0);
        
        campo.getCasa(posicaoInicial.getX(), posicaoInicial.getY()).setTipo(TipoCelula.OCUPADA);
        campo.getCasa(posicaoFinal.getX(), posicaoFinal.getY()).setTipo(TipoCelula.FINAL);
        
        Robo robo = new Robo(posicaoInicial.getX(), posicaoInicial.getY());
        
        System.out.println("\n► CONFIGURAÇÃO");
        System.out.println("  Início: " + posicaoInicial);
        System.out.println("  Final: " + posicaoFinal);
        campo.exibirCampo();
        
        pausar(1500);
        
        BuscaVisualService buscaService = new BuscaVisualService();
        
        ResultadoBusca resultado = buscaService.buscarCaminhoBFSVisual(campo, posicaoInicial, posicaoFinal, 150);
        
        if (resultado.isCaminhoEncontrado()) {
            System.out.println("\n► AGORA O ROBÔ EXECUTARÁ O CAMINHO ENCONTRADO...\n");
            pausar(1000);
            
            ExecutorCaminhoService executor = new ExecutorCaminhoService();
            campo.resetarTodasVisitacoes();
            
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║              EXECUTANDO CAMINHO BFS                 ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            
            executor.executarCaminhoComDelay(campo, robo, resultado.getCaminho(), 300);
            
            System.out.println("\n╔══════════════════════════════════════════════════════╗");
            System.out.println("║            ROBÔ CHEGOU AO DESTINO FINAL!            ║");
            System.out.println("╚══════════════════════════════════════════════════════╝\n");
            
        } else {
            System.out.println("✗ NENHUM CAMINHO ENCONTRADO!");
            System.out.println("  Nós explorados: " + resultado.getNosExplorados().size());
        }
    }
    
    private static void pausar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
