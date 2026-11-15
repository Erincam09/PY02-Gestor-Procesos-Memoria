package auxiliares.estrategiasMemoria;

import auxiliares.EstrategiaMemoria;
import modelo.AsignacionMemoria;
import modelo.EspacioMemoria;

public class ParticionFijaIgual implements EstrategiaMemoria {
    private final int particiones;
    private int tamanioParticion;
    private int[] inicios;
    private boolean[] ocupada;

    public ParticionFijaIgual(int particiones) {
        if (particiones <= 0) {
            throw new IllegalArgumentException("Debe haber al menos una partición");
        }
        this.particiones = particiones;
    } 

    public String getNombre() {
        return "Partición Fija Igual";
    }

    public void iniciar(EspacioMemoria memoria) {
        crearParticiones(memoria);
    }

    private void crearParticiones(EspacioMemoria memoria) {
        if (inicios != null) {
            return;
        }
        int espacioUsuario = memoria.getTamanio() - memoria.getEspacioKernel();
        if (particiones > espacioUsuario) {
            throw new IllegalArgumentException("No hay suficiente memoria para crear las particiones");
        }
        tamanioParticion = espacioUsuario / particiones;
        inicios = new int[particiones];
        ocupada = new boolean[particiones];
        int inicio = memoria.getEspacioKernel();
        for (int i = 0; i < particiones; i++) {
            inicios[i] = inicio;
            ocupada[i] = false;
            inicio += tamanioParticion;
        }
    }

    public int getParticion(int posicion) {
        if (inicios == null) {
            return -1;
        }
        for (int i = 0; i < particiones; i++) {
            int inicio = inicios[i];
            int fin = inicio + tamanioParticion;
            if (posicion >= inicio && posicion < fin) {
                return i;
            }
        }
        return -1;
    }

    public boolean puedeAlojar(EspacioMemoria memoria, int cantidad) {
        crearParticiones(memoria);
        return cantidad > 0 && cantidad <= tamanioParticion;
    }
  
    public AsignacionMemoria asignar(EspacioMemoria memoria, int cantidad) {
        crearParticiones(memoria);
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a 0");
        }
        if (cantidad > tamanioParticion) {
            return null;
        }
        for (int i = 0; i < particiones; i++) {
            if (!ocupada[i]) {
                ocupada[i] = true;
                return AsignacionMemoria.contigua(inicios[i], cantidad, tamanioParticion);
            }
        }
        return null;
    }

    public void liberar(EspacioMemoria memoria, AsignacionMemoria asignacion) {
        for (int posicion: asignacion.getPosicionesReservadas()) {
            memoria.liberar(posicion);
        }
        for (int i = 0; i < particiones; i++) {
            if (inicios[i] == asignacion.getBase()) {
                ocupada[i] = false;
                break;
            }
        }
    }
}
