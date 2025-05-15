package es.etg.dam.tfg.programa.utils;

import java.util.Collections;
import java.util.List;


public class Paginador<T> {

    private final List<T> elementos;
    private final int elementosPorPagina;
    private int paginaActual;

    public Paginador(List<T> elementos, int elementosPorPagina) {
        this.elementos = elementos != null ? elementos : Collections.emptyList();
        this.elementosPorPagina = elementosPorPagina;
        this.paginaActual = 0;
    }

    public List<T> getPaginaActual() {
        int start = paginaActual * elementosPorPagina;
        int end = Math.min(start + elementosPorPagina, elementos.size());
        return elementos.subList(start, end);
    }

    public int getPaginaActualNumero() {
        return paginaActual + 1;
    }

    public int getTotalPaginas() {
        return (int) Math.ceil((double) elementos.size() / elementosPorPagina);
    }

    public boolean puedeIrAnterior() {
        return paginaActual > 0;
    }

    public boolean puedeIrSiguiente() {
        return (paginaActual + 1) * elementosPorPagina < elementos.size();
    }

    public void irAnterior() {
        if (puedeIrAnterior()) paginaActual--;
    }

    public void irSiguiente() {
        if (puedeIrSiguiente()) paginaActual++;
    }

    public void reset() {
        paginaActual = 0;
    }

    public boolean estaVacio() {
        return elementos.isEmpty();
    }
    
}

