package modelo;

public class RegistroGantt {
    private final int nucleo;
    private final int pid;
    private final String programa;
    private final int inicio;
    private final int fin;

    public RegistroGantt(int nucleo, int pid, String programa, int inicio, int fin) {
        this.nucleo = nucleo;
        this.pid = pid;
        this.programa = programa;
        this.inicio = inicio;
        this.fin = fin;
    }

    public int getNucleo() { return nucleo; }
    public int getPid() { return pid; }
    public String getPrograma() { return programa; }
    public int getInicio() { return inicio; }
    public int getFin() { return fin; }
}
