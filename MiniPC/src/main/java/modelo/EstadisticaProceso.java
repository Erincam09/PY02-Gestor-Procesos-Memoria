package modelo;

/** Copia del resultado de un proceso; no depende de su BCP una vez liberado. */
public class EstadisticaProceso {
    private final int pid;
    private final String programa;
    private final String estado;
    private final int llegada;
    private final int inicio;
    private final int fin;
    private final int servicio;

    public EstadisticaProceso(Proceso proceso) {
        pid = proceso.getPid();
        programa = proceso.getPrograma().getNombreArchivo();
        estado = proceso.getEstado();
        llegada = proceso.getLlegada();
        inicio = proceso.getTiempoInicio();
        fin = proceso.getTiempoFin();
        servicio = proceso.getCpuRecibido();
    }

    public EstadisticaProceso(Trabajo trabajo, int tiempo) {
        pid = -1;
        programa = trabajo.getPrograma().getNombreArchivo();
        estado = "RECHAZADO";
        llegada = trabajo.getTiempoLlegada();
        inicio = -1;
        fin = tiempo;
        servicio = 0;
    }

    public boolean isFinalizado() { return "FINALIZADO".equals(estado); }
    public int getPid() { return pid; }
    public String getPrograma() { return programa; }
    public String getEstado() { return estado; }
    public int getInicio() { return inicio; }
    public int getFin() { return fin; }
    public int getEstancia() { return isFinalizado() ? fin - llegada : -1; }
    public Double getRelacion() {
        if (!isFinalizado() || servicio == 0) return null;
        return (double) getEstancia() / servicio;
    }
}
