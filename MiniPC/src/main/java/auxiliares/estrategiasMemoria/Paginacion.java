package auxiliares.estrategiasMemoria;

import java.util.ArrayList;
import java.util.List;

import auxiliares.EstrategiaMemoria;
import auxiliares.ListaMarcosLibres;
import modelo.AsignacionMemoria;
import modelo.EspacioMemoria;
import modelo.TablaPaginas;

public class Paginacion implements EstrategiaMemoria {
    private final int tamanioPagina;
    private int inicioUsuario;
    private int cantidadMarcos;
    private ListaMarcosLibres marcosLibres;

    public Paginacion(int tamanioPagina) {
        if (tamanioPagina <= 0) {
            throw new IllegalArgumentException("El tamaño de página debe ser mayor que cero");
        }
        this.tamanioPagina = tamanioPagina;
    }

    
    public String getNombre() {
        return "Paginación";
    }

   
    private void crearMarcos(EspacioMemoria memoria) {
        if (marcosLibres != null) {
            return;
        }
        inicioUsuario = memoria.getEspacioKernel();
        cantidadMarcos = (memoria.getTamanio() - inicioUsuario) / tamanioPagina;
        if (cantidadMarcos == 0) {
            throw new IllegalArgumentException("No cabe un marco en la memoria indicada");
        }
        marcosLibres = new ListaMarcosLibres(cantidadMarcos);
    }

    
    public void iniciar(EspacioMemoria memoria) {
        crearMarcos(memoria);
    }

    
    public int getParticion(int posicion) {
        if (marcosLibres == null || posicion < inicioUsuario) {
            return -1;
        }
        int marco = (posicion - inicioUsuario) / tamanioPagina;
        return marco < cantidadMarcos ? marco : -1;
    }

   
    
    public boolean puedeAlojar(EspacioMemoria memoria, int cantidad) {
        crearMarcos(memoria);
        return paginasNecesarias(cantidad) <= cantidadMarcos;
    }

    private int paginasNecesarias(int cantidad) {
        return (cantidad + tamanioPagina - 1) / tamanioPagina;
    }

   
    
    public AsignacionMemoria asignar(EspacioMemoria memoria, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de posiciones debe ser mayor que cero");
        }
        crearMarcos(memoria);
        int paginas = paginasNecesarias(cantidad);
        if (marcosLibres.cantidad() < paginas) {
            return null;
        }
        List<Integer> marcos = new ArrayList<>();
        for (int pagina = 0; pagina < paginas; pagina++) {
            marcos.add(marcosLibres.tomar());
        }
        TablaPaginas tabla = new TablaPaginas(tamanioPagina, inicioUsuario, marcos);
        
        List<Integer> posiciones = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            posiciones.add(tabla.traducir(i));
        }
        return new AsignacionMemoria(posiciones, paginas * tamanioPagina, tabla);
    }

    
    public void liberar(EspacioMemoria memoria, AsignacionMemoria asignacion) {
        for (int posicion : asignacion.getPosicionesReservadas()) {
            memoria.liberar(posicion);
        }
        for (int marco : asignacion.getTablaPaginas().getMarcos()) {
            marcosLibres.devolver(marco);
        }
    }
}
