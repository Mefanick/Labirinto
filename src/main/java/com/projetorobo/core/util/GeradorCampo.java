package com.projetorobo.core.util;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import java.util.Random;

public class GeradorCampo {
    
    public static Campo criarCampoEtapa1() {
        Campo campo = new Campo(10, 10);
        return campo;
    }
    
    public static Campo criarCampoEtapa2() {
        Campo campo = new Campo(10, 10);
        
        // Novo padrão de paredes baseado na imagem fornecida
        // Linha 0
        campo.getCasa(0, 4).setTipo(TipoCelula.PAREDE);
        
        // Linha 1
        campo.getCasa(1, 0).setTipo(TipoCelula.PAREDE);
        campo.getCasa(1, 3).setTipo(TipoCelula.PAREDE);
        
        // Linha 2
        campo.getCasa(2, 2).setTipo(TipoCelula.PAREDE);
        
        // Linha 3
        campo.getCasa(3, 2).setTipo(TipoCelula.PAREDE);
        campo.getCasa(3, 5).setTipo(TipoCelula.PAREDE);
        
        // Linha 5
        campo.getCasa(5, 6).setTipo(TipoCelula.PAREDE);
        
        // Linha 6
        campo.getCasa(6, 5).setTipo(TipoCelula.PAREDE);
        campo.getCasa(6, 8).setTipo(TipoCelula.PAREDE);
        
        // Linha 7
        campo.getCasa(7, 5).setTipo(TipoCelula.PAREDE);
        campo.getCasa(7, 8).setTipo(TipoCelula.PAREDE);
        
        // Linha 8
        campo.getCasa(8, 5).setTipo(TipoCelula.PAREDE);
        campo.getCasa(8, 7).setTipo(TipoCelula.PAREDE);
        campo.getCasa(8, 8).setTipo(TipoCelula.PAREDE);
        
        return campo;
    }
    
    public static Campo criarCampoEtapa3() {
        return criarCampoEtapa2();
    }
    
    public static Campo criarCampoEtapa4() {
        Campo campo = new Campo(10, 10);
        
        // Mapa base da Etapa 4 baseado no formato fornecido
        // i = posição inicial (0,5)
        // f = posição final (9,5)
        // Demais números = pesos das células
        
        // Inicializar todas as células com peso 1 (padrão)
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                campo.getCasa(i, j).setValor(1);
            }
        }
        
        // Linha 1 (índice 1): peso 3 na coluna 5
        campo.getCasa(1, 5).setValor(3);
        
        // Linha 2 (índice 2)
        campo.getCasa(2, 2).setValor(2);
        campo.getCasa(2, 3).setValor(2);
        campo.getCasa(2, 5).setValor(3);
        campo.getCasa(2, 6).setValor(3);
        campo.getCasa(2, 7).setValor(2);
        
        // Linha 3 (índice 3)
        campo.getCasa(3, 2).setValor(2);
        campo.getCasa(3, 4).setValor(3);
        campo.getCasa(3, 5).setValor(3);
        campo.getCasa(3, 6).setValor(3);
        campo.getCasa(3, 7).setValor(2);
        
        // Linha 4 (índice 4)
        campo.getCasa(4, 2).setValor(2);
        campo.getCasa(4, 3).setValor(2);
        campo.getCasa(4, 4).setValor(3);
        campo.getCasa(4, 5).setValor(3);
        campo.getCasa(4, 6).setValor(3);
        campo.getCasa(4, 7).setValor(2);
        campo.getCasa(4, 8).setValor(2);
        
        // Linha 5 (índice 5)
        campo.getCasa(5, 3).setValor(2);
        campo.getCasa(5, 4).setValor(3);
        campo.getCasa(5, 6).setValor(2);
        campo.getCasa(5, 7).setValor(2);
        
        // Linha 6 (índice 6)
        campo.getCasa(6, 4).setValor(2);
        campo.getCasa(6, 5).setValor(3);
        
        return campo;
    }
    
    public static Campo criarCampoEtapa4ComPesos() {
        Campo campo = criarCampoEtapa2();
        
        Random random = new Random();
        for (int i = 0; i < campo.getLinhas(); i++) {
            for (int j = 0; j < campo.getColunas(); j++) {
                if (campo.getCasa(i, j).getTipo() == TipoCelula.VAZIA) {
                    int peso = random.nextInt(10) + 1;
                    campo.getCasa(i, j).setValor(peso);
                }
            }
        }
        
        return campo;
    }
    
    public static Campo criarCampoAleatorio(int linhas, int colunas, int percentualParedes) {
        Campo campo = new Campo(linhas, colunas);
        Random random = new Random();
        
        int totalCelulas = linhas * colunas;
        int numeroParedesDesejadas = (totalCelulas * percentualParedes) / 100;
        int paredesCriadas = 0;
        
        while (paredesCriadas < numeroParedesDesejadas) {
            int x = random.nextInt(linhas);
            int y = random.nextInt(colunas);
            
            if (campo.getCasa(x, y).getTipo() == TipoCelula.VAZIA) {
                campo.getCasa(x, y).setTipo(TipoCelula.PAREDE);
                paredesCriadas++;
            }
        }
        
        return campo;
    }
    
    public static void definirPosicaoInicialAleatoria(Campo campo) {
        Random random = new Random();
        int x, y;
        
        do {
            x = random.nextInt(campo.getLinhas());
            y = random.nextInt(campo.getColunas());
        } while (campo.getCasa(x, y).getTipo() != TipoCelula.VAZIA);
        
        campo.getCasa(x, y).setTipo(TipoCelula.OCUPADA);
    }
    
    public static Posicao definirPosicaoFinalAleatoria(Campo campo) {
        Random random = new Random();
        int x, y;
        
        do {
            x = random.nextInt(campo.getLinhas());
            y = random.nextInt(campo.getColunas());
        } while (campo.getCasa(x, y).getTipo() != TipoCelula.VAZIA);
        
        campo.getCasa(x, y).setTipo(TipoCelula.FINAL);
        return new Posicao(x, y);
    }
}
