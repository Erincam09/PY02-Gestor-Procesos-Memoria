package auxiliares;
import java.util.List;
import modelo.Proceso;

public class MultilevelFQueue implements AlgoritmoPlanificacion {
    private final int quantum;
    private boolean turnoAgotado;

    public MultilevelFQueue(int quantum) {
        if (quantum <= 0 || quantum > Integer.MAX_VALUE / 4) {
            throw new IllegalArgumentException("Quantum fuera de rango");
        }
        this.quantum = quantum;
        turnoAgotado = false;
    }

    public String getNombre() {
        return "Multilevel Feedback Queue";
    }

    private int obtenerQuantum(int nivel) {
        if (nivel == 0) {
            return quantum;
        }
        if (nivel == 1) {
            return quantum * 2;
        }
        return quantum * 4;
    }

    public void alLlegar(Proceso proceso, int tiempo) {
        proceso.setNivelCola(0);
        proceso.setQuantumUsado(0);
    }

    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        Proceso elegido = null;

        for (Proceso proceso : listos) {
            if (elegido == null) {
                elegido = proceso;
            } else if (proceso.getNivelCola() < elegido.getNivelCola()) {
                elegido = proceso;
            }
        }
        if (actual == null) {
            turnoAgotado = false;
            return elegido;
        }
        boolean debeRotar = turnoAgotado;
        turnoAgotado = false;

        if (elegido == null) {
            return actual;
        }
        if (elegido.getNivelCola() < actual.getNivelCola()) {
            return elegido;
        }
        if (debeRotar && elegido.getNivelCola() == actual.getNivelCola()) {
            return elegido;
        }
        return actual;
    }

    public void alTerminarSegundo(Proceso proceso, int tiempo) {
        int nivel = proceso.getNivelCola();
        int limite = obtenerQuantum(nivel);

        if (proceso.getQuantumUsado() >= limite) {
            if (nivel < 2) {
                proceso.setNivelCola(nivel + 1);
            }
            proceso.setQuantumUsado(0);
            turnoAgotado = true;
        }
    }
}
