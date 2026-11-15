package auxiliares.estrategiasMemoria;

import java.util.ArrayList;
import java.util.List;

import auxiliares.EstrategiaMemoria;
import modelo.AsignacionMemoria;
import modelo.EspacioMemoria;

public class ParticionFijaDiferente implements EstrategiaMemoria {
    private final List<Integer> tamanios;

    // Una posición por partición: dónde empieza, cuánto mide y si está ocupada
    private int[] inicios;
    private int[] tamaniosParticion;
    private boolean[] ocupada;

    public ParticionFijaDiferente(List<Integer> tamanios) {
        this.tamanios = new ArrayList<>(tamanios);
    }
    
    
    public String getNombre() {
        return "Partición Fija Diferente";
    }

    
    public void iniciar(EspacioMemoria memoria) {
        crearParticiones(memoria);
    }

    
    public int getParticion(int posicion) {
        if (inicios == null) {
            return -1;
        }
        for (int i = 0; i < inicios.length; i++) {
            if (posicion >= inicios[i] && posicion < inicios[i] + tamaniosParticion[i]) {
                return i;
            }
        }
        return -1;
    }

    
    private void crearParticiones(EspacioMemoria memoria) {
        if (inicios != null) {
            return;
        }
        int cantidad = tamanios.size();
        long suma = 0;
        for (int tamanio : tamanios) {
            if (tamanio <= 0) {
                throw new IllegalArgumentException("Los tamaños de partición deben ser positivos");
            }
            suma += tamanio;
        }
        if (cantidad == 0 || suma > memoria.getTamanio() - memoria.getEspacioKernel()) {
            throw new IllegalArgumentException("Las particiones no caben en la memoria indicada");
        }
        inicios = new int[cantidad];
        tamaniosParticion = new int[cantidad];
        ocupada = new boolean[cantidad];
        int inicio = memoria.getEspacioKernel();
        for (int i = 0; i < cantidad; i++) {
            inicios[i] = inicio;
            tamaniosParticion[i] = tamanios.get(i);
            inicio += tamaniosParticion[i];
        }
    }

    
    
    public boolean puedeAlojar(EspacioMemoria memoria, int cantidad) {
        crearParticiones(memoria);
        for (int tamanio : tamanios) {
            if (cantidad <= tamanio) {
                return true;
            }
        }
        return false;
    }

    
   
    public AsignacionMemoria asignar(EspacioMemoria memoria, int cantidad) {
        crearParticiones(memoria);
        int elegida = -1;
        for (int i = 0; i < inicios.length; i++) {
            if (!ocupada[i] && cantidad <= tamaniosParticion[i]) {
                if (elegida == -1 || tamaniosParticion[i] < tamaniosParticion[elegida]) {
                    elegida = i;
                }
            }
        }
        if (elegida == -1) {
            return null;
        }
        ocupada[elegida] = true;
        return AsignacionMemoria.contigua(inicios[elegida], cantidad, tamaniosParticion[elegida]);
    }

    
    public void liberar(EspacioMemoria memoria, AsignacionMemoria asignacion) {
        for (int posicion : asignacion.getPosicionesReservadas()) {
            memoria.liberar(posicion);
        }
        for (int i = 0; i < inicios.length; i++) {
            if (inicios[i] == asignacion.getBase()) {
                ocupada[i] = false;
            }
        }
    }
}
