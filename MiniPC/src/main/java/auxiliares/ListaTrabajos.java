package auxiliares;

import java.util.ArrayList;
import modelo.Trabajo;
import modelo.Programa;

public class ListaTrabajos {
    private ArrayList<Trabajo> trabajos;
    private int siguienteId;

    public ListaTrabajos() {
        trabajos = new ArrayList<>();
        siguienteId = 1;
    }

    public Trabajo agregarTrabajo(Programa programa) {
        Trabajo trabajo = new Trabajo(siguienteId, programa);
        trabajos.add(trabajo);
        siguienteId++;
        return trabajo;
    }

    public ArrayList<Trabajo> getTrabajos() {
        return trabajos;
    }

    public int cantidadTrabajos() {
        return trabajos.size();
    }

    public boolean estaVacia() {
        return trabajos.isEmpty();
    }

}
