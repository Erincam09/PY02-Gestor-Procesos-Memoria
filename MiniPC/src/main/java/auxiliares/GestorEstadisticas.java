package auxiliares;

import modelo.EstadisticaSimulacion;
import modelo.EstadisticaProceso;
import java.util.ArrayList;
import java.util.List;

/** Historial de ejecuciones completadas durante la sesión de la aplicación. */
public class GestorEstadisticas {
    private final ArrayList<EstadisticaSimulacion> ejecuciones = new ArrayList<>();

    public void registrar(EstadisticaSimulacion resultado) {
        if (resultado != null && !ejecuciones.contains(resultado)) ejecuciones.add(resultado);
    }
    public List<EstadisticaSimulacion> getEjecuciones() { return new ArrayList<>(ejecuciones); }
    public boolean hayResultados() { return !ejecuciones.isEmpty(); }
    public int getFinalizados() {
        int total = 0;
        for (EstadisticaSimulacion e : ejecuciones) total += e.getFinalizados();
        return total;
    }
    public int getErrores() {
        int total = 0;
        for (EstadisticaSimulacion e : ejecuciones) total += e.getErrores();
        return total;
    }
    public int getRechazados() {
        int total = 0;
        for (EstadisticaSimulacion e : ejecuciones) total += e.getRechazados();
        return total;
    }
    public long getTiempoOciosoTotal() {
        long total = 0;
        for (EstadisticaSimulacion e : ejecuciones) total += e.getTiempoOciosoTotal();
        return total;
    }
    public double getPromedioEstancia() {
        double suma = 0;
        for (EstadisticaSimulacion e : ejecuciones) {
            for (EstadisticaProceso p : e.getProcesos()) {
                if (p.isFinalizado()) suma += p.getEstancia();
            }
        }
        return getFinalizados() == 0 ? 0 : suma / getFinalizados();
    }
    public double getPromedioEspera() {
        double suma = 0;
        for (EstadisticaSimulacion e : ejecuciones) {
            for (EstadisticaProceso p : e.getProcesos()) {
                if (p.isFinalizado()) suma += p.getEspera();
            }
        }
        return getFinalizados() == 0 ? 0 : suma / getFinalizados();
    }
    public double getPromedioRelacion() {
        double suma = 0;
        int cantidad = 0;
        for (EstadisticaSimulacion e : ejecuciones) {
            for (EstadisticaProceso p : e.getProcesos()) {
                Double relacion = p.getRelacion();
                if (relacion != null) {
                    suma += relacion;
                    cantidad++;
                }
            }
        }
        return cantidad == 0 ? 0 : suma / cantidad;
    }
}
