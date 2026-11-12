/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import modelo.AsignacionMemoria;
import modelo.Instruccion;
import modelo.Programa;
import modelo.Memoria;

/**
 *
 * @author erinc
 */
public class CargarPrograma {
    private final EstrategiaMemoria estrategia;

    public CargarPrograma(EstrategiaMemoria estrategia) {
        this.estrategia = estrategia;
    }

    public AsignacionMemoria cargar(Programa programa, Memoria memoria) {
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
            memoria.escribir(asignacion.getPosiciones().get(indice), instruccion.getEntradaTexto());
            indice++;
        }
        return asignacion;
    }
}
