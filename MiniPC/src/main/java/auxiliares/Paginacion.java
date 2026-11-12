package auxiliares;

import modelo.AsignacionMemoria;
import modelo.Memoria;

public class Paginacion implements EstrategiaMemoria {
   
    public String getNombre() {
        return "Paginación";
    }

   
    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
        throw new UnsupportedOperationException("Pendiente de implementar: Paginación");
    }
}
