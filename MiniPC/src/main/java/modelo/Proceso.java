package modelo;

public class Proceso {
    private final Programa programa;
    private final BCP bcp;
    private int llegada;
    private final int rafaga;
    private int restante;
    private int tiempoInicio = -1;
    private int tiempoFin = -1;
    private int cpuRecibido;
    private int quantumUsado;
    private int nivelCola;
    private int tickets = 1;
    private AsignacionMemoria asignacionMemoria;

    public Proceso(Programa programa, BCP bcp) {
        this.programa = programa;
        this.bcp = bcp;
        rafaga = programa.getCantInstrucciones();
        restante = rafaga;
    }
    public Programa getPrograma() { return programa; }
    public BCP getBcp() { return bcp; }
    public int getPid() { return bcp.getPid(); }
    public String getEstado() { return bcp.getEstado(); }
    public int getLlegada() { return llegada; }
    public void setLlegada(int valor) { llegada = valor; }
    public int getRafaga() { return rafaga; }
    public int getRestante() { return restante; }
    public int getTiempoInicio() { return tiempoInicio; }
    public void setTiempoInicio(int valor) { tiempoInicio = valor; }
    public int getTiempoFin() { return tiempoFin; }
    public void setTiempoFin(int valor) { tiempoFin = valor; }
    public int getCpuRecibido() { return cpuRecibido; }
    public int getQuantumUsado() { return quantumUsado; }
    public void setQuantumUsado(int valor) { quantumUsado = valor; }
    public int getNivelCola() { return nivelCola; }
    public void setNivelCola(int valor) { nivelCola = valor; }
    public int getPrioridad() { return bcp.getPrioridad(); }
    public void setPrioridad(int valor) { bcp.setPrioridad(valor); }
    public AsignacionMemoria getAsignacionMemoria() { return asignacionMemoria; }
    public void setAsignacionMemoria(AsignacionMemoria valor) { asignacionMemoria = valor; }
    public int getTickets() { return tickets; }
    public void setTickets(int valor) { tickets = valor; }
    public int getEspera(int reloj) {
        int fin = tiempoFin < 0 ? reloj : tiempoFin;
        return fin - llegada - cpuRecibido;
    }
    public void registrarSegundo() {
        if (restante > 0) restante--;
        cpuRecibido++;
        quantumUsado++;
        bcp.setTiempoEmpleado(bcp.getTiempoEmpleado() + 1);
    }
}
