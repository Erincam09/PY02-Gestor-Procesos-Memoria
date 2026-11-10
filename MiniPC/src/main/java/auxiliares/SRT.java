package auxiliares;
import java.util.List;
import modelo.Proceso;

public class SRT implements AlgoritmoPlanificacion{

    public String getNombre() {
        return "SRT";
    }

    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        if(actual  == null) {
            return null;
        }
        if(listos.isEmpty()) {
            return actual;
        }

        // se elige proceso con el menor tiempo restante
        Proceso siguiente = actual;
        for(Proceso p : listos) {
            if(p.getRestante() < siguiente.getRestante()) {
                siguiente = p;
            }
        }
        return siguiente;

    }

    public void alLlegar(Proceso proceso, int tiempo) { }
    public void alTerminarSegundo(Proceso proceso, int tiempo) { }
    
}
