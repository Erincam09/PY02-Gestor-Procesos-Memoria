package auxiliares;

import java.util.ArrayList;
import java.util.List;
import modelo.AsignacionMemoria;
import modelo.Memoria;
import modelo.TablaPaginas;

public class Paginacion implements EstrategiaMemoria {
    private final int tamanioPagina;
    private int inicioUsuario;
    private int cantidadMarcos;
    private ListaMarcosLibres marcosLibres;

    public Paginacion(int tamanioPagina) {
        this.tamanioPagina = tamanioPagina;
    }

    
    public String getNombre() {
        return "Paginación";
    }

   
    private void crearMarcos(Memoria memoria) {
        if (marcosLibres != null) {
            return;
        }
        inicioUsuario = memoria.getEspacioKernel();
        cantidadMarcos = (memoria.getTamanio() - inicioUsuario) / tamanioPagina;
        marcosLibres = new ListaMarcosLibres(cantidadMarcos);
    }

    
    public void iniciar(Memoria memoria) {
        crearMarcos(memoria);
    }

    
    public int getParticion(int posicion) {
        if (marcosLibres == null || posicion < inicioUsuario) {
            return -1;
        }
        int marco = (posicion - inicioUsuario) / tamanioPagina;
        return marco < cantidadMarcos ? marco : -1;
    }

   
    
    public boolean puedeAlojar(Memoria memoria, int cantidad) {
        crearMarcos(memoria);
        return paginasNecesarias(cantidad) <= cantidadMarcos;
    }

    private int paginasNecesarias(int cantidad) {
        return (cantidad + tamanioPagina - 1) / tamanioPagina;
    }

   
    
    public AsignacionMemoria asignar(Memoria memoria, int cantidad) {
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

    
    public void liberar(Memoria memoria, AsignacionMemoria asignacion) {
        for (int posicion : asignacion.getPosiciones()) {
            memoria.liberar(posicion);
        }
        for (int marco : asignacion.getTablaPaginas().getMarcos()) {
            marcosLibres.devolver(marco);
        }
    }
}
