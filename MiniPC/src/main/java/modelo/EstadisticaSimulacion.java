package modelo;

import java.util.ArrayList;
import java.util.List;

/** Resultado final independiente de los objetos que se reinician en el simulador. */
public class EstadisticaSimulacion {
    private final String algoritmo;
    private final int fin;
    private final List<EstadisticaProceso> procesos;
    private final int[] ociosidad;

    public EstadisticaSimulacion(ConfiguracionSistema configuracion, int fin,
            List<EstadisticaProceso> procesos, int[] ociosidad) {
        algoritmo = configuracion.getNombreAlgoritmo();
        this.fin = fin;
        this.procesos = new ArrayList<>(procesos);
        this.ociosidad = ociosidad.clone();
    }

    public String getAlgoritmo() { return algoritmo; }
    public int getInicio() { return 0; }
    public int getFin() { return fin; }
    public List<EstadisticaProceso> getProcesos() { return new ArrayList<>(procesos); }
    public long getTiempoOciosoTotal() {
        long total = 0;
        for (int tiempo : ociosidad) total += tiempo;
        return total;
    }
    public int contarEstado(String estado) {
        int cantidad = 0;
        for (EstadisticaProceso proceso : procesos) {
            if (proceso.getEstado().equals(estado)) cantidad++;
        }
        return cantidad;
    }
    public int getFinalizados() { return contarEstado("FINALIZADO"); }
    public double getPromedioEstancia() {
        double suma = 0;
        for (EstadisticaProceso proceso : procesos) {
            if (proceso.isFinalizado()) suma += proceso.getEstancia();
        }
        return getFinalizados() == 0 ? 0 : suma / getFinalizados();
    }
    public double getPromedioRelacion() {
        double suma = 0;
        int cantidad = 0;
        for (EstadisticaProceso proceso : procesos) {
            Double relacion = proceso.getRelacion();
            if (relacion != null) {
                suma += relacion;
                cantidad++;
            }
        }
        return cantidad == 0 ? 0 : suma / cantidad;
    }
}
