package modelo;

import java.util.ArrayList;
import java.util.List;


public class TablaPaginas {
    private final int tamanioPagina;
    private final int inicioMarcos;
    private final List<Integer> marcos;

    public TablaPaginas(int tamanioPagina, int inicioMarcos, List<Integer> marcos) {
        this.tamanioPagina = tamanioPagina;
        this.inicioMarcos = inicioMarcos;
        this.marcos = new ArrayList<>(marcos);
    }

    public int getTamanioPagina() {
        return tamanioPagina;
    }

    public int getCantidadPaginas() {
        return marcos.size();
    }

    public int getMarco(int pagina) {
        return marcos.get(pagina);
    }

    public List<Integer> getMarcos() {
        return new ArrayList<>(marcos);
    }

    
    public int traducir(int direccionLogica) {
        int pagina = direccionLogica / tamanioPagina;
        int desplazamiento = direccionLogica % tamanioPagina;
        if (direccionLogica < 0 || pagina >= marcos.size()) {
            throw new IllegalArgumentException("Dirección lógica fuera de la tabla de páginas: " + direccionLogica);
        }
        return inicioMarcos + marcos.get(pagina) * tamanioPagina + desplazamiento;
    }

   
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int pagina = 0; pagina < marcos.size(); pagina++) {
            if (pagina > 0) {
                texto.append(", ");
            }
            texto.append(pagina).append("→").append(marcos.get(pagina));
        }
        return texto.toString();
    }
}
