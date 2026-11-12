package auxiliares;

import modelo.AsignacionMemoria;
import modelo.Memoria;

public class DinamicaBestFit implements EstrategiaMemoria {
    
    public String getNombre() {
        return "Dinámica Best Fit";
    }

    
    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
        throw new UnsupportedOperationException("Pendiente de implementar: Dinámica Best Fit");
    }
}
