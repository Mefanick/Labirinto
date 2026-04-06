package com.projetorobo.core.model;

import com.projetorobo.core.enums.TipoCelula;

public class Campo {
    private final int linhas;
    private final int colunas;
    private final int[][] campoPosicao;
    private final Casa[][] matrizCasas;

    public Campo(int linhas, int colunas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.campoPosicao = new int[linhas][colunas];
        this.matrizCasas = new Casa[linhas][colunas];
        
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                this.matrizCasas[i][j] = new Casa(TipoCelula.VAZIA, 0, false);
                this.campoPosicao[i][j] = 0;
            }
        }
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public boolean posicaoValida(int x, int y) {
        return x >= 0 && x < linhas && y >= 0 && y < colunas;
    }
    
    public boolean posicaoValida(Posicao posicao) {
        return posicaoValida(posicao.getX(), posicao.getY());
    }

    public Casa getCasa(int linha, int coluna) {
        if (!posicaoValida(linha, coluna)) {
            throw new IndexOutOfBoundsException(
                String.format("Posição inválida: (%d, %d). Limites: [0-%d, 0-%d]", 
                    linha, coluna, linhas - 1, colunas - 1)
            );
        }
        return matrizCasas[linha][coluna];
    }
    
    public Casa getCasa(Posicao posicao) {
        return getCasa(posicao.getX(), posicao.getY());
    }

    public void setCasa(int linha, int coluna, Casa casa) {
        if (!posicaoValida(linha, coluna)) {
            throw new IndexOutOfBoundsException(
                String.format("Posição inválida: (%d, %d). Limites: [0-%d, 0-%d]", 
                    linha, coluna, linhas - 1, colunas - 1)
            );
        }
        this.matrizCasas[linha][coluna] = casa;
    }

    public int getPosicaoValor(int linha, int coluna) {
        if (!posicaoValida(linha, coluna)) {
            throw new IndexOutOfBoundsException(
                String.format("Posição inválida: (%d, %d). Limites: [0-%d, 0-%d]", 
                    linha, coluna, linhas - 1, colunas - 1)
            );
        }
        return campoPosicao[linha][coluna];
    }

    public void setPosicaoValor(int linha, int coluna, int valor) {
        if (!posicaoValida(linha, coluna)) {
            throw new IndexOutOfBoundsException(
                String.format("Posição inválida: (%d, %d). Limites: [0-%d, 0-%d]", 
                    linha, coluna, linhas - 1, colunas - 1)
            );
        }
        this.campoPosicao[linha][coluna] = valor;
    }
    
    public void resetarTodasVisitacoes() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                matrizCasas[i][j].resetarVisitacao();
            }
        }
    }

    public void exibirCampo() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║         CAMPO DE MOVIMENTAÇÃO " + linhas + "x" + colunas + "           ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        System.out.print("      ");
        for (int j = 0; j < colunas; j++) {
            System.out.printf(" %d  ", j);
        }
        System.out.println();
        
        System.out.print("    ┌");
        for (int j = 0; j < colunas; j++) {
            System.out.print("───");
            if (j < colunas - 1) System.out.print("┬");
        }
        System.out.println("┐");
        
        for (int i = 0; i < linhas; i++) {
            System.out.printf(" %d  │", i);
            
            for (int j = 0; j < colunas; j++) {
                Casa casa = matrizCasas[i][j];
                String simbolo;
                
                switch (casa.getTipo()) {
                    case VAZIA:
                        simbolo = casa.isVisitada() ? " . " : "   ";
                        break;
                    case OCUPADA:
                        simbolo = " O ";
                        break;
                    case PAREDE:
                        simbolo = "███";
                        break;
                    case FINAL:
                        simbolo = " F ";
                        break;
                    default:
                        simbolo = " ? ";
                }
                
                System.out.print(simbolo);
                if (j < colunas - 1) System.out.print("│");
            }
            
            System.out.println("│");
            
            if (i < linhas - 1) {
                System.out.print("    ├");
                for (int j = 0; j < colunas; j++) {
                    System.out.print("───");
                    if (j < colunas - 1) System.out.print("┼");
                }
                System.out.println("┤");
            }
        }
        
        System.out.print("    └");
        for (int j = 0; j < colunas; j++) {
            System.out.print("───");
            if (j < colunas - 1) System.out.print("┴");
        }
        System.out.println("┘\n");
    }
}
