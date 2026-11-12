package auxiliares;

import modelo.AsignacionMemoria;
import modelo.Memoria;

public class DinamicaNextFit implements EstrategiaMemoria {
    
    public String getNombre() {
        return "Dinámica Next Fit";
    }

    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
        throw new UnsupportedOperationException("Pendiente de implementar: Dinámica Next Fit");
    }
}
