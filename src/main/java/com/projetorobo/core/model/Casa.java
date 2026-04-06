package com.projetorobo.core.model;

import com.projetorobo.core.enums.TipoCelula;

public class Casa {
    private TipoCelula tipo;
    private int valor;
    private boolean visitada;
    private int contadorVisitas;

    public Casa() {
        this.tipo = TipoCelula.VAZIA;
        this.valor = 0;
        this.visitada = false;
        this.contadorVisitas = 0;
    }

    public Casa(TipoCelula tipo, int valor, boolean visitada) {
        this.tipo = tipo;
        this.valor = valor;
        this.visitada = visitada;
    }

    public TipoCelula getTipo() {
        return tipo;
    }

    public void setTipo(TipoCelula tipo) {
        this.tipo = tipo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public boolean isVisitada() {
        return visitada;
    }

    public boolean podeSerAcessada() {
        return tipo != TipoCelula.PAREDE && tipo != TipoCelula.OCUPADA;
    }

    public void marcarComoVisitada() {
        this.visitada = true;
    }
    
    public void resetarVisitacao() {
        this.visitada = false;
    }
    
    public int getContadorVisitas() {
        return contadorVisitas;
    }
    
    public void incrementarVisitas() {
        this.contadorVisitas++;
    }
    
    public void resetarContadorVisitas() {
        this.contadorVisitas = 0;
    }
}
