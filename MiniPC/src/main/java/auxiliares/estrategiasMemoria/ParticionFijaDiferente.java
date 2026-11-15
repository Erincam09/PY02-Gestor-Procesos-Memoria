package auxiliares;

import java.util.ArrayList;
import java.util.List;
import modelo.AsignacionMemoria;
import modelo.Memoria;

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

    
    public void iniciar(Memoria memoria) {
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

    
    private void crearParticiones(Memoria memoria) {
        if (inicios != null) {
            return;
        }
        int cantidad = tamanios.size();
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

    
    
    public boolean puedeAlojar(Memoria memoria, int cantidad) {
        for (int tamanio : tamanios) {
            if (cantidad <= tamanio) {
                return true;
            }
        }
        return false;
    }

    
   
    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
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

    
    public void liberar(Memoria memoria, AsignacionMemoria asignacion) {
        for (int posicion : asignacion.getPosiciones()) {
            memoria.liberar(posicion);
        }
        for (int i = 0; i < inicios.length; i++) {
            if (inicios[i] == asignacion.getBase()) {
                ocupada[i] = false;
            }
        }
    }
}
