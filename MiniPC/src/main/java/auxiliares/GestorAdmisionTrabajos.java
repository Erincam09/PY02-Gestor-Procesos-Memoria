package auxiliares;
import modelo.Trabajo;

public class GestorAdmisionTrabajos {
    private ListaTrabajos listaTrabajos;

    public GestorAdmisionTrabajos(ListaTrabajos listaTrabajos) {
        this.listaTrabajos = listaTrabajos;
    }

    public java.util.List<Trabajo> obtenerPendientes(int reloj) {
        java.util.ArrayList<Trabajo> pendientes = new java.util.ArrayList<>();
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            if (trabajo.getEstado().equals("PENDIENTE") && trabajo.getTiempoLlegada() <= reloj) {
                pendientes.add(trabajo);
            }
        }
        return pendientes;
    }

    public Trabajo obtenerSiguienteTrabajo() {
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            if (trabajo.getEstado().equals("PENDIENTE")) {
                return trabajo;
            }
        }
        return null;
    }

    public boolean hayTrabajosPendientes() {
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            if (trabajo.getEstado().equals("PENDIENTE")) {
                return true;
            }
        }
        return false;
    }
}
