package auxiliares;

import java.util.List;
import modelo.Proceso;

public class PorPrioridad implements AlgoritmoPlanificacion {
    private final boolean apropiativo;

    public PorPrioridad(boolean apropiativo) {
        this.apropiativo = apropiativo;
    }

    
    public String getNombre() {
        return apropiativo ? "Por Prioridad (Apropiativo)" : "Por Prioridad (No Apropiativo)";
    }

    
    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        if (listos.isEmpty()) {
            return actual;
        }

        // Si no es apropiativo y ya hay un proceso en ejecución, se queda con ese
        if (!apropiativo && actual != null) {
            return actual;
        }

        // Se determina el candidato inicial
        Proceso elegido = (actual != null) ? actual : listos.get(0);

        // Si es apropiativo, se evalua también al proceso actual junto con los listos
        List<Proceso> candidatos = new java.util.ArrayList<>(listos);
        if (actual != null && apropiativo) {
            candidatos.add(actual);
        }

        // Se elige el que tenga menor valor numérico de prioridad 
        for (Proceso p : candidatos) {
            if (p.getPrioridad() < elegido.getPrioridad()) {
                elegido = p;
            }
        }
        
        return elegido;
    }

   
    public void alLlegar(Proceso proceso, int tiempo) { }

    
    public void alTerminarSegundo(Proceso proceso, int tiempo) { }
}