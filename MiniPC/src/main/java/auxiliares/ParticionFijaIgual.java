package auxiliares;

import modelo.AsignacionMemoria;
import modelo.Memoria;

public class ParticionFijaIgual implements EstrategiaMemoria {
    private final int particiones;

    public ParticionFijaIgual(int particiones) {
        this.particiones = particiones;
    }

    public int getParticiones() {
        return particiones;
    }

    
    public String getNombre() {
        return "Partición Fija Igual";
    }

  
    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
        throw new UnsupportedOperationException("Pendiente de implementar: Partición Fija Igual");
    }
}
