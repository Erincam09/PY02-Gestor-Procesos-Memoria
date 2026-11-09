package auxiliares;
import java.util.List;
import modelo.Proceso;

public interface AlgoritmoPlanificacion {
    String getNombre();
    Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo);
    void alLlegar(Proceso proceso, int tiempo);
    void alTerminarSegundo(Proceso proceso, int tiempo);
}
