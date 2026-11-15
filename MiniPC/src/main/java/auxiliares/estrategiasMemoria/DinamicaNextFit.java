package auxiliares.estrategiasMemoria;

import auxiliares.EstrategiaMemoria;
import modelo.AsignacionMemoria;
import modelo.Memoria;

public class DinamicaNextFit implements EstrategiaMemoria {
    private int siguiente = -1;

    public String getNombre() {
        return "Dinámica Next Fit";
    }

    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor que cero");
        }
        int inicioUsuario = memoria.getEspacioKernel();
        int fin = memoria.getTamanio();
        int espacioUsuario = fin - inicioUsuario;
        if (cantidad > espacioUsuario) {
            return null;
        }
        if (siguiente < inicioUsuario || siguiente >= fin) {
            siguiente = inicioUsuario;
        }
        int inicio = siguiente;
        for (int revisadas = 0; revisadas < espacioUsuario; revisadas++) {
            if (cantidad <= fin - inicio) {
                boolean libre = true;
                for (int i = 0; i < cantidad; i++) {
                    if (!memoria.estaLibre(inicio + i)) {
                        libre = false;
                        break;
                    }
                }
                if (libre) {
                    siguiente = inicio + cantidad;
                    if (siguiente >= fin) {
                        siguiente = inicioUsuario;
                    }
                    return AsignacionMemoria.contigua(inicio, cantidad);
                }
            }
            inicio++;
            if (inicio >= fin) {
                inicio = inicioUsuario;
            }
        }
        return null;
    }
}
