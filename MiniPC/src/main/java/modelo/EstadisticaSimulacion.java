package modelo;

import java.util.ArrayList;
import java.util.List;

/** Resultado final independiente de los objetos que se reinician en el simulador. */
public class EstadisticaSimulacion {
    private final ConfiguracionSistema configuracion;
    private final int fin;
    private final List<EstadisticaProceso> procesos;
    private final int[] ociosidad;

    public EstadisticaSimulacion(ConfiguracionSistema configuracion, int fin,
            List<EstadisticaProceso> procesos, int[] ociosidad) {
        this.configuracion = new ConfiguracionSistema(configuracion);
        this.fin = fin;
        this.procesos = new ArrayList<>(procesos);
        this.ociosidad = ociosidad.clone();
    }

    public ConfiguracionSistema getConfiguracion() { return new ConfiguracionSistema(configuracion); }
    public String getAlgoritmo() { return configuracion.getNombreAlgoritmo(); }
    public String getMemoria() { return configuracion.getNombreEstrategiaMemoria(); }
    public int getCantidadNucleos() { return configuracion.getCantidadNucleos(); }
    public int getInicio() { return 0; }
    public int getFin() { return fin; }
    public int getDuracion() { return fin; }
    public List<EstadisticaProceso> getProcesos() { return new ArrayList<>(procesos); }
    public int getTiempoOcioso(int nucleo) { return ociosidad[nucleo]; }
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
    public int getErrores() { return contarEstado("ERROR"); }
    public int getRechazados() { return contarEstado("RECHAZADO"); }
    public double getPromedioEstancia() {
        double suma = 0;
        for (EstadisticaProceso proceso : procesos) {
            if (proceso.isFinalizado()) suma += proceso.getEstancia();
        }
        return getFinalizados() == 0 ? 0 : suma / getFinalizados();
    }
    public double getPromedioEspera() {
        double suma = 0;
        for (EstadisticaProceso proceso : procesos) {
            if (proceso.isFinalizado()) suma += proceso.getEspera();
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
