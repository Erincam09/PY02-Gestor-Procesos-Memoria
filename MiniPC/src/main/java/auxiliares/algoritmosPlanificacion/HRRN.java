package auxiliares;
import java.util.List;
import modelo.Proceso;

public class HRRN implements AlgoritmoPlanificacion {
    public String getNombre() { 
        return "HRRN"; 
    }
    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        if (actual != null) {
            return actual;
        }
        if (listos.isEmpty()) {
            return null;
        }
        // Aqui elegimos el proceso que tiene la mayor respuesta
        Proceso elegido = listos.get(0);
        for (Proceso proceso: listos) {
            double respuestaElegido = (double)(tiempo - elegido.getLlegada() + elegido.getRafaga()) / elegido.getRafaga();
            double respuestaProceso = (double)(tiempo - proceso.getLlegada() + proceso.getRafaga()) / proceso.getRafaga();
            if (respuestaProceso > respuestaElegido) {
                elegido = proceso; 
            }
        }
        return elegido;
    }
    public void alLlegar(Proceso proceso, int tiempo) { }
    public void alTerminarSegundo(Proceso proceso, int tiempo) { }
}
