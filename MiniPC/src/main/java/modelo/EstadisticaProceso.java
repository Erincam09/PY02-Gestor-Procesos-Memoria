package modelo;

/** Copia del resultado de un proceso; no depende de su BCP una vez liberado. */
public class EstadisticaProceso {
    private final int pid;
    private final String programa;
    private final int nucleo;
    private final String estado;
    private final int llegada;
    private final int rafaga;
    private final int inicio;
    private final int fin;
    private final int servicio;
    private final String motivo;

    public EstadisticaProceso(Proceso proceso, String motivo) {
        pid = proceso.getPid();
        programa = proceso.getPrograma().getNombreArchivo();
        nucleo = proceso.getBcp().getNucleoAsignado();
        estado = proceso.getEstado();
        llegada = proceso.getLlegada();
        rafaga = proceso.getRafaga();
        inicio = proceso.getTiempoInicio();
        fin = proceso.getTiempoFin();
        servicio = proceso.getCpuRecibido();
        this.motivo = motivo == null ? "" : motivo;
    }

    public EstadisticaProceso(Trabajo trabajo, int tiempo, String motivo) {
        pid = -1;
        programa = trabajo.getPrograma().getNombreArchivo();
        nucleo = -1;
        estado = "RECHAZADO";
        llegada = trabajo.getTiempoLlegada();
        rafaga = trabajo.getRafaga();
        inicio = -1;
        fin = tiempo;
        servicio = 0;
        this.motivo = motivo;
    }

    public boolean isFinalizado() { return "FINALIZADO".equals(estado); }
    public int getPid() { return pid; }
    public String getPrograma() { return programa; }
    public int getNucleo() { return nucleo; }
    public String getEstado() { return estado; }
    public int getLlegada() { return llegada; }
    public int getRafaga() { return rafaga; }
    public int getInicio() { return inicio; }
    public int getFin() { return fin; }
    public int getServicio() { return servicio; }
    public String getMotivo() { return motivo; }
    public int getEstancia() { return isFinalizado() ? fin - llegada : -1; }
    public int getEspera() { return isFinalizado() ? getEstancia() - servicio : -1; }
    public Double getRelacion() {
        if (!isFinalizado() || servicio == 0) return null;
        return (double) getEstancia() / servicio;
    }
}
