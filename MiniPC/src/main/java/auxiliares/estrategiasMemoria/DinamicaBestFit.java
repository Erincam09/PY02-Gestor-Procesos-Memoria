package auxiliares.estrategiasMemoria;

import auxiliares.EstrategiaMemoria;
import modelo.AsignacionMemoria;
import modelo.EspacioMemoria;

public class DinamicaBestFit implements EstrategiaMemoria {
    
    public String getNombre() {
        return "Dinámica Best Fit";
    }

    public AsignacionMemoria asignar(EspacioMemoria memoria, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor que cero");
        }
        int inicioUsuario = memoria.getEspacioKernel();
        int fin = memoria.getTamanio();
        if (cantidad > fin - inicioUsuario) {
            return null;
        }
        int inicioHueco = -1;
        int tamanioHueco = 0;
        int mejorInicio = -1;
        int mejorTamanio = 0;
        for (int posicion = inicioUsuario; posicion <= fin; posicion++) {
            if (posicion < fin && memoria.estaLibre(posicion)) {
                if (tamanioHueco == 0) {
                    inicioHueco = posicion;
                }
                tamanioHueco++;
            } else {
                if (tamanioHueco >= cantidad) {
                    if (mejorInicio == -1 || tamanioHueco < mejorTamanio) {
                        mejorInicio = inicioHueco;
                        mejorTamanio = tamanioHueco;
                    }
                }
                inicioHueco = -1;
                tamanioHueco = 0;
            }
        }
        if (mejorInicio == -1) {
            return null;
        }
        return AsignacionMemoria.contigua(mejorInicio, cantidad);   
    }
}
