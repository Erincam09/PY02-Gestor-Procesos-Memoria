package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ConfiguracionSistema {

    private int memoriaPrincipal;
    private int almacenamientoSecundario;
    private String nombreAlgoritmo = "FCFS";
    private Map<String, Integer> parametrosAlgoritmo = new HashMap<>();
    private String nombreEstrategiaMemoria = "Dinámica First Fit";
    private Map<String, Integer> parametrosEstrategiaMemoria = new HashMap<>(Map.of("particiones", 4));
    private List<Integer> tamaniosParticiones = new ArrayList<>(List.of(64, 64, 128));
    private List<Integer> tamaniosParticionesVirtual = new ArrayList<>(List.of(16, 16, 32));
    public static final int PORCENTAJE_KERNEL = 75;
    private int cantidadNucleos = 1;

    public static final int maxNucleos = 5;
    public static final int maxProcesosXNucleo = 5;
    public static final int maxProcesosTotal = maxNucleos * maxProcesosXNucleo;

    public ConfiguracionSistema() {
        memoriaPrincipal = 1024;
        almacenamientoSecundario = 512;
    }

    public ConfiguracionSistema(int memoriaPrincipal, int almacenamientoSecundario) {
        this.memoriaPrincipal = memoriaPrincipal;
        if (almacenamientoSecundario < 512) {
            throw new IllegalArgumentException("El almacenamiento secundario debe ser al menos 512");
        }
        this.almacenamientoSecundario = almacenamientoSecundario;
    }

    public ConfiguracionSistema(ConfiguracionSistema otra) {
        this(otra.getMemoriaPrincipal(), otra.getAlmacenamientoSecundario());
        cantidadNucleos = otra.getCantidadNucleos();
        nombreAlgoritmo = otra.getNombreAlgoritmo();
        parametrosAlgoritmo.putAll(otra.getParametrosAlgoritmo());
        nombreEstrategiaMemoria = otra.getNombreEstrategiaMemoria();
        parametrosEstrategiaMemoria.putAll(otra.getParametrosEstrategiaMemoria());
        tamaniosParticiones = otra.getTamaniosParticiones();
        tamaniosParticionesVirtual = otra.getTamaniosParticionesVirtual();
    }

    public String getNombreAlgoritmo() { return nombreAlgoritmo; }
    public void setNombreAlgoritmo(String nombre) { nombreAlgoritmo = nombre; }
    public Map<String, Integer> getParametrosAlgoritmo() {
        return new HashMap<>(parametrosAlgoritmo);
    }
    public void setParametrosAlgoritmo(Map<String, Integer> parametros) {
        parametrosAlgoritmo = new HashMap<>(parametros);
    }
    public String getNombreEstrategiaMemoria() { return nombreEstrategiaMemoria; }
    public void setNombreEstrategiaMemoria(String nombre) { nombreEstrategiaMemoria = nombre; }
    public Map<String, Integer> getParametrosEstrategiaMemoria() {
        return new HashMap<>(parametrosEstrategiaMemoria);
    }
    public void setParametrosEstrategiaMemoria(Map<String, Integer> parametros) {
        parametrosEstrategiaMemoria = new HashMap<>(parametros);
    }
    public List<Integer> getTamaniosParticiones() {
        return new ArrayList<>(tamaniosParticiones);
    }
    public void setTamaniosParticiones(List<Integer> tamanios) {
        tamaniosParticiones = new ArrayList<>(tamanios);
    }
    public static int calcularEspacioKernel(int tamanio) {
        return tamanio * PORCENTAJE_KERNEL / 100;
    }

    public List<Integer> getTamaniosParticionesVirtual() {
        return new ArrayList<>(tamaniosParticionesVirtual);
    }

    public void setTamaniosParticionesVirtual(List<Integer> tamanios) {
        tamaniosParticionesVirtual = new ArrayList<>(tamanios);
    }

    public int getCantidadNucleos() {
        return cantidadNucleos;
    }

    public void setCantidadNucleos(int cantidadNucleos) {
        if (cantidadNucleos < 1 || cantidadNucleos > maxNucleos) {
            throw new IllegalArgumentException("La cantidad de núcleos debe estar entre 1 y " + maxNucleos);
        }
        this.cantidadNucleos = cantidadNucleos;
    }

    public int getCapacidadProcesos() {
        return cantidadNucleos * maxProcesosXNucleo;
    }

    public int getMemoriaPrincipal() {
        return memoriaPrincipal;
    }

    public void setMemoriaPrincipal(int memoriaPrincipal) {
        this.memoriaPrincipal = memoriaPrincipal;
    }

    public int getAlmacenamientoSecundario() {
        return almacenamientoSecundario;
    }

    public void setAlmacenamientoSecundario(int almacenamientoSecundario) {
        if (almacenamientoSecundario < 512) {
            throw new IllegalArgumentException("El almacenamiento secundario debe ser al menos 512");
        }
        this.almacenamientoSecundario = almacenamientoSecundario;
    }

    public int getMemoriaVirtual() {
        return almacenamientoSecundario / 8;
    }

    public int getEspacioKernel() {
        return calcularEspacioKernel(memoriaPrincipal);
    }

    public int getEspacioUsuario() {
        return memoriaPrincipal - getEspacioKernel();
    }
}
