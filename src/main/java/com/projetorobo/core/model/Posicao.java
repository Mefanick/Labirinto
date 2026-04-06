package com.projetorobo.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Posicao {
    private final int x;
    private final int y;

    public Posicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Posicao> getVizinhos() {
        List<Posicao> vizinhos = new ArrayList<>();
        vizinhos.add(new Posicao(x - 1, y));
        vizinhos.add(new Posicao(x + 1, y));
        vizinhos.add(new Posicao(x, y - 1));
        vizinhos.add(new Posicao(x, y + 1));
        return vizinhos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posicao posicao = (Posicao) o;
        return x == posicao.x && y == posicao.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
