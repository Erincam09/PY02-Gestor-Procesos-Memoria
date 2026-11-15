package modelo;

// Operaciones que comparten la RAM y la zona virtual del disco.
public interface EspacioMemoria {
    int getTamanio();
    int getEspacioKernel();
    String leer(int posicion);
    void escribir(int posicion, String contenido);
    void liberar(int posicion);
    boolean estaLibre(int posicion);
}
