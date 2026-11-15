package auxiliares.algoritmosPlanificacion;
import java.util.List;

import auxiliares.AlgoritmoPlanificacion;
import modelo.Proceso;

public class SPN implements AlgoritmoPlanificacion {
    public String getNombre() { 
        return "SPN"; 
    }
    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        if (actual != null) {
            return actual;
        }
        if (listos.isEmpty()) {
            return null;
        }
        // Aqui elegimos el proceso que tiene la menor rafaga
        Proceso elegido = listos.get(0);
        for (Proceso proceso: listos) {
            if (proceso.getRafaga() < elegido.getRafaga()) {
                elegido = proceso;
            }
        }
        return elegido;
    }
    public void alLlegar(Proceso proceso, int tiempo) { }
    public void alTerminarSegundo(Proceso proceso, int tiempo) { }
}