package auxiliares;
import modelo.Proceso;

public class ResultadoSegundo {
    private final Proceso proceso;
    private final boolean terminado;
    private final String error;
    public ResultadoSegundo(Proceso proceso, boolean terminado, String error) {
        this.proceso = proceso;
        this.terminado = terminado;
        this.error = error;
    }
    public Proceso getProceso() { return proceso; }
    public boolean isTerminado() { return terminado; }
    public String getError() { return error; }
}
