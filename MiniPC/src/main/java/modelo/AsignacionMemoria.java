package modelo;

import java.util.ArrayList;
import java.util.List;

public class AsignacionMemoria {
    private final List<Integer> posiciones;
    
    private final int reservado;
    private final TablaPaginas tablaPaginas;

    public AsignacionMemoria(List<Integer> posiciones) {
        this(posiciones, posiciones.size());
    }

    public AsignacionMemoria(List<Integer> posiciones, int reservado) {
        this(posiciones, reservado, null);
    }

    public AsignacionMemoria(List<Integer> posiciones, int reservado, TablaPaginas tablaPaginas) {
        this.tablaPaginas = tablaPaginas;
        this.posiciones = new ArrayList<>(posiciones);
        this.reservado = reservado;
    }

    public static AsignacionMemoria contigua(int base, int cantidad) {
        return contigua(base, cantidad, cantidad);
    }

    public static AsignacionMemoria contigua(int base, int cantidad, int reservado) {
        List<Integer> posiciones = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            posiciones.add(base + i);
        }
        return new AsignacionMemoria(posiciones, reservado);
    }

   
    public TablaPaginas getTablaPaginas() {
        return tablaPaginas;
    }

    
    public int traducir(int direccionLogica) {
        if (tablaPaginas != null) {
            return tablaPaginas.traducir(direccionLogica);
        }
        return posiciones.get(direccionLogica);
    }

    public int getReservado() {
        return reservado;
    }

    public int getBase() {
        return posiciones.isEmpty() ? -1 : posiciones.get(0);
    }

    public int getAlcance() {
        return posiciones.size();
    }

    public List<Integer> getPosiciones() {
        return new ArrayList<>(posiciones);
    }

    // Incluye el espacio sobrante de una partición o de la última página.
    public List<Integer> getPosicionesReservadas() {
        List<Integer> reservadas = new ArrayList<>();
        for (int i = 0; i < reservado; i++) {
            int posicion = tablaPaginas == null ? getBase() + i : tablaPaginas.traducir(i);
            reservadas.add(posicion);
        }
        return reservadas;
    }
}
