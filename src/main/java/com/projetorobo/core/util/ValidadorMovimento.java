package com.projetorobo.core.util;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.model.Posicao;

import java.util.ArrayList;
import java.util.List;

public class ValidadorMovimento {
    
    public static boolean posicaoValida(Campo campo, int x, int y) {
        return campo.posicaoValida(x, y);
    }
    
    public static boolean posicaoValida(Campo campo, Posicao posicao) {
        return campo.posicaoValida(posicao.getX(), posicao.getY());
    }
    
    public static boolean podeAcessar(Campo campo, int x, int y) {
        if (!posicaoValida(campo, x, y)) {
            return false;
        }
        Casa casa = campo.getCasa(x, y);
        return casa.podeSerAcessada();
    }
    
    public static boolean podeAcessar(Campo campo, Posicao posicao) {
        return podeAcessar(campo, posicao.getX(), posicao.getY());
    }
    
    public static List<Posicao> obterVizinhosValidos(Campo campo, Posicao posicao) {
        List<Posicao> vizinhosValidos = new ArrayList<>();
        
        for (Posicao vizinho : posicao.getVizinhos()) {
            if (podeAcessar(campo, vizinho)) {
                vizinhosValidos.add(vizinho);
            }
        }
        
        return vizinhosValidos;
    }
    
    public static List<Posicao> obterVizinhosValidos(Campo campo, int x, int y) {
        return obterVizinhosValidos(campo, new Posicao(x, y));
    }
}
