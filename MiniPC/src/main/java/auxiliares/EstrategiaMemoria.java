package auxiliares;

import modelo.AsignacionMemoria;
import modelo.EspacioMemoria;

public interface EstrategiaMemoria {
    String getNombre();

    
    default void iniciar(EspacioMemoria memoria) {
    }

    
    default int getParticion(int posicion) {
        return -1;
    }
    
    AsignacionMemoria asignar(EspacioMemoria memoria, int cantidad);

    
    default boolean puedeAlojar(EspacioMemoria memoria, int cantidad) {
        return cantidad <= memoria.getTamanio() - memoria.getEspacioKernel();
    }

    default void liberar(EspacioMemoria memoria, AsignacionMemoria asignacion) {
        for (int posicion : asignacion.getPosicionesReservadas()) {
            memoria.liberar(posicion);
        }
    }
}
