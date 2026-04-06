package com.projetorobo.etapa3.service;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.service.MovimentoBaseService;

import java.util.List;

public class ExecutorCaminhoService {
    private final MovimentoBaseService movimentoService;

    public ExecutorCaminhoService() {
        this.movimentoService = new MovimentoBaseService();
    }

    public void executarCaminho(Campo campo, Robo robo, List<Posicao> caminho) {
        for (int i = 1; i < caminho.size(); i++) {
            Posicao destino = caminho.get(i);
            movimentoService.moverPara(campo, robo, destino);
        }
    }

    public void executarCaminhoComDelay(Campo campo, Robo robo, List<Posicao> caminho, int delayMs) {
        for (int i = 1; i < caminho.size(); i++) {
            Posicao destino = caminho.get(i);
            
            System.out.println("\n→ Movendo para " + destino);
            movimentoService.moverPara(campo, robo, destino);
            
            campo.exibirCampo();
            
            pausar(delayMs);
        }
    }

    private void pausar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
