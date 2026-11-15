package auxiliares.estrategiasMemoria;

import auxiliares.AdministradorMemoria;
import auxiliares.EstrategiaMemoria;
import modelo.AsignacionMemoria;
import modelo.EspacioMemoria;

public class DinamicaFirstFit implements EstrategiaMemoria {
    private final AdministradorMemoria administrador = new AdministradorMemoria();

    
    public String getNombre() {
        return "Dinámica First Fit";
    }

    
    public AsignacionMemoria asignar(EspacioMemoria memoria, int cantidad) {
        int base = administrador.buscarEspacio(memoria, cantidad);
        if(base == -1) {
            return null;
        }
        return AsignacionMemoria.contigua(base, cantidad);
    }
}
