package auxiliares;

import modelo.AsignacionMemoria;
import modelo.Memoria;

public interface EstrategiaMemoria {
    String getNombre();

    
    AsignacionMemoria asignar(Memoria memoria, int cantidad);

    
    default boolean puedeAlojar(Memoria memoria, int cantidad) {
        return cantidad <= memoria.getTamanio() - memoria.getEspacioKernel();
    }

    default void liberar(Memoria memoria, AsignacionMemoria asignacion) {
        for (int posicion : asignacion.getPosiciones()) {
            memoria.liberar(posicion);
        }
    }
}
