package auxiliares;

import modelo.AsignacionMemoria;
import modelo.Memoria;

public class DinamicaFirstFit implements EstrategiaMemoria {
    private final AdministradorMemoria administrador = new AdministradorMemoria();

    
    public String getNombre() {
        return "Dinámica First Fit";
    }

    
    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
        int base = administrador.buscarEspacio(memoria, cantidad);
        if(base == -1) {
            return null;
        }
        return AsignacionMemoria.contigua(base, cantidad);
    }
}
