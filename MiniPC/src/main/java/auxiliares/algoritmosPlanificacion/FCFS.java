package auxiliares.algoritmosPlanificacion;
import java.util.List;

import auxiliares.AlgoritmoPlanificacion;
import modelo.Proceso;

public class FCFS implements AlgoritmoPlanificacion {
    public String getNombre() { 
        return "FCFS"; 
    }
    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        if (actual != null) {
            return actual;
        }
        if (listos.isEmpty()) {
            return null;
        }
        return listos.get(0);
    }
    public void alLlegar(Proceso proceso, int tiempo) { }
    public void alTerminarSegundo(Proceso proceso, int tiempo) { }
}
