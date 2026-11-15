package auxiliares.algoritmosPlanificacion;
import java.util.List;

import auxiliares.AlgoritmoPlanificacion;
import modelo.Proceso;

public class RR implements AlgoritmoPlanificacion {
    private final int quantum;

    public RR(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum debe ser mayor que cero");
        }
        this.quantum = quantum;
    }
    public String getNombre() { 
        return "Round Robin"; 
    }
    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        if (actual != null) {
            if (actual.getQuantumUsado() < quantum) {
                return actual;
            }
            else {
                actual.setQuantumUsado(0);
            }
        }
        if (listos.isEmpty()) {
            return actual;
        }
        return listos.get(0);
    }
    public void alLlegar(Proceso proceso, int tiempo) { }
    public void alTerminarSegundo(Proceso proceso, int tiempo) { }
}
