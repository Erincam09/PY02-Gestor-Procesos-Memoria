package auxiliares;
import java.util.ArrayList;
import modelo.Proceso;
import modelo.ConfiguracionSistema;

public class ListaProcesos {
    private ArrayList<Proceso> procesos;
    private final int MAX_PROCESOS = ConfiguracionSistema.maxProcesosTotal;

    public ListaProcesos() {
        procesos = new ArrayList<>();
    }

    public void agregarProceso(Proceso proceso) {
        if (procesos.size() < MAX_PROCESOS) {
            procesos.add(proceso);
        } else {
            throw new IllegalStateException("No se pueden agregar más procesos");
        }
    }

    public ArrayList<Proceso> getProcesos() {
        return procesos;
    }

    public void eliminarProceso(Proceso proceso) {
        procesos.remove(proceso);
    }
}
