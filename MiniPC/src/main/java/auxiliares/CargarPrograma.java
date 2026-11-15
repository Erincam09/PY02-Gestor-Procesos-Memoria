/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import modelo.AsignacionMemoria;
import modelo.Instruccion;
import modelo.Programa;
import modelo.EspacioMemoria;
import modelo.TablaPaginas;

/**
 *
 * @author erinc
 */
public class CargarPrograma {
    private final EstrategiaMemoria estrategia;

    public CargarPrograma(EstrategiaMemoria estrategia) {
        this.estrategia = estrategia;
    }

    public AsignacionMemoria cargar(Programa programa, EspacioMemoria memoria) {
        if (programa == null || memoria == null) {
            throw new IllegalArgumentException("El programa y la memoria son obligatorios.");
        }
        int cantidadInstrucciones = programa.getCantInstrucciones();
        AsignacionMemoria asignacion = estrategia.asignar(memoria, cantidadInstrucciones);
        if (asignacion == null) {
            return null;
        }
        int indice = 0;
        for (Instruccion instruccion : programa.getInstrucciones()) {
            memoria.escribir(asignacion.traducir(indice), instruccion.getEntradaTexto());
            indice++;
        }
        return asignacion;
    }

    public AsignacionMemoria copiarDesdeMemoria(EspacioMemoria origen,
            AsignacionMemoria asignacionOrigen, EspacioMemoria destino) {
        int cantidad = asignacionOrigen.getAlcance();
        // Primero reservar el proceso completo. Si no cabe, no se mueve ninguna página.
        AsignacionMemoria asignacionDestino = estrategia.asignar(destino, cantidad);
        if (asignacionDestino == null) {
            return null;
        }

        TablaPaginas tabla = asignacionOrigen.getTablaPaginas();
        int tamanioBloque = tabla == null ? cantidad : tabla.getTamanioPagina();
        // En paginación, cada bloque es una página; la última puede estar incompleta.
        for (int inicio = 0; inicio < cantidad; inicio += tamanioBloque) {
            for (int desplazamiento = 0; desplazamiento < tamanioBloque
                    && inicio + desplazamiento < cantidad; desplazamiento++) {
                int logica = inicio + desplazamiento;
                String contenido = origen.leer(asignacionOrigen.traducir(logica));
                destino.escribir(asignacionDestino.traducir(logica), contenido);
            }
        }
        return asignacionDestino;
    }
}
