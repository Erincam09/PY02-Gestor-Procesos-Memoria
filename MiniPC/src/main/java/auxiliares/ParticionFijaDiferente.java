package auxiliares;

import java.util.ArrayList;
import java.util.List;
import modelo.AsignacionMemoria;
import modelo.Memoria;

public class ParticionFijaDiferente implements EstrategiaMemoria {
    private final List<Integer> tamanios;

    public ParticionFijaDiferente(List<Integer> tamanios) {
        this.tamanios = new ArrayList<>(tamanios);
    }

    public List<Integer> getTamanios() {
        return new ArrayList<>(tamanios);
    }

    
    public String getNombre() {
        return "Partición Fija Diferente";
    }

    
    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
        throw new UnsupportedOperationException("Pendiente de implementar: Partición Fija Diferente");
    }
}
