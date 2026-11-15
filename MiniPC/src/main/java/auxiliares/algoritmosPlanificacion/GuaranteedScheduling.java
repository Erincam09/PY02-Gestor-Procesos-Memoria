package auxiliares;
import java.util.List;
import java.util.ArrayList;
import modelo.Proceso;

public class GuaranteedScheduling implements AlgoritmoPlanificacion {
    private final int quantum;

    public GuaranteedScheduling(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum debe ser mayor que cero");
        }
        this.quantum = quantum;
    }

    public String getNombre() {
        return "Guaranteed Scheduling";
    }

    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        if(listos.isEmpty() && actual == null) {
            return null;
        }
        
         if (actual != null) {
            if (actual.getQuantumUsado() < quantum) {
                
                return actual;
            } else {
                
                actual.setQuantumUsado(0);
            }
        }
        ArrayList<Proceso> candidatos = new ArrayList<>(listos);
        if(actual!= null && actual.getRestante() > 0) {
            candidatos.add(actual);
        }

        int procesos = candidatos.size();

        Proceso elegido = candidatos.get(0);

        double ratioMinimo = Double.MAX_VALUE;
        for(Proceso p: candidatos) {
            int tiempoEnSistema = tiempo - p.getLlegada();
            if (tiempoEnSistema <= 0) {
                tiempoEnSistema = 1; 
            }
            double tiempoMerecido = (double) tiempoEnSistema / procesos;

            double tiempoConsumido = p.getCpuRecibido();
            double ratio = tiempoConsumido / tiempoMerecido;
            if (ratio < ratioMinimo) {
                ratioMinimo = ratio;
                elegido = p;
            }
            
        }
        return elegido;
    }

    public void alLlegar(Proceso proceso, int tiempo) {
       
    }

    public void alTerminarSegundo(Proceso proceso, int tiempo) {
        
    }
}
