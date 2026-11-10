package auxiliares;

import modelo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Un núcleo mantiene su contexto y decide a través del algoritmo recibido. */
public class Nucleo {
    private final int id;
    private final CPU cpu = new CPU();
    private final Despachador despachador;
    private final ManejadorInstrucciones instrucciones = new ManejadorInstrucciones(cpu);
    private final AlgoritmoPlanificacion algoritmo;
    private final ManejadorAlmacenamiento manejadorAlmacenamiento;
    private final Consumer<String> salida;
    private final ArrayList<Proceso> procesos = new ArrayList<>();
    private final ArrayList<Proceso> listos = new ArrayList<>();
    private int tiempoOcioso;
    private int direccionInstruccionActual = -1;

    public Nucleo(int id, AlgoritmoPlanificacion algoritmo,
            ManejadorAlmacenamiento almacenamiento, Consumer<String> salida) {
        if (id < 0 || id >= ConfiguracionSistema.maxNucleos || algoritmo == null) {
            throw new IllegalArgumentException("Núcleo o algoritmo inválido");
        }
        this.id = id;
        this.algoritmo = algoritmo;
        manejadorAlmacenamiento = almacenamiento;
        this.salida = salida;
        despachador = new Despachador(cpu, id);
    }

    public void agregarProceso(Proceso proceso, int reloj) {
        if (proceso == null || !hayEspacio()) {
            throw new IllegalStateException("Núcleo sin cupo");
        }
        if (proceso.getBcp().getNucleoAsignado() != -1) {
            throw new IllegalStateException("El proceso ya tiene núcleo");
        }
        for (Proceso p : procesos) {
            if (p.getPid() == proceso.getPid()) {
                throw new IllegalArgumentException("PID duplicado");
            }
        }
        proceso.getBcp().setNucleoAsignado(id);
        procesos.add(proceso);
        if ("PREPARADO".equals(proceso.getEstado())) {
            agregarListo(proceso, reloj);
        }
    }

    public void agregarListo(Proceso proceso, int reloj) {
        if (!procesos.contains(proceso) || !"PREPARADO".equals(proceso.getEstado())) {
            throw new IllegalArgumentException("El proceso debe pertenecer al núcleo y estar preparado");
        }
        if (listos.contains(proceso)) {
            throw new IllegalArgumentException("Proceso ya listo");
        }
        listos.add(proceso);
        algoritmo.alLlegar(proceso, reloj);
    }

   
    public void prepararSegundo(int reloj) {
        Proceso actual = despachador.getProcesoActual();
        // Copia para que el algoritmo no modifique la cola del núcleo.
        Proceso elegido = algoritmo.elegir(new ArrayList<>(listos), actual, reloj);
        if (elegido == null && actual != null) {
            throw new IllegalStateException("El algoritmo no puede omitir al proceso activo");
        }
        if (elegido != actual) {
            if (!listos.contains(elegido)) {
                throw new IllegalStateException("Proceso elegido fuera de listos");
            }
            if (actual != null) {
                despachador.retirarProceso("PREPARADO");
                actual.setQuantumUsado(0);
                listos.add(actual);
            }
            listos.remove(elegido);
            despachador.despachar(elegido);
            elegido.setQuantumUsado(0);
            if (elegido.getTiempoInicio() == -1) {
                elegido.setTiempoInicio(reloj);
                elegido.getBcp().setTiempoInicio(String.valueOf(reloj));
            }
        }
        actual = despachador.getProcesoActual();
        direccionInstruccionActual = -1;
        if (actual == null) {
            return;
        }
        int indice = cpu.getPC() - actual.getBcp().getBase();
        if (indice >= 0 && indice < actual.getPrograma().getCantInstrucciones()) {
            direccionInstruccionActual = cpu.getPC();
            cpu.setIR(actual.getPrograma().getInstrucciones().get(indice).getEntradaTexto());
            despachador.guardarContexto();
        }
    }

    
    public ResultadoSegundo ejecutarSegundo(int reloj) {
        Proceso actual = despachador.getProcesoActual();
        if (actual == null) {
            tiempoOcioso++;
            return new ResultadoSegundo(null, false, null);
        }
        try {
            int indice = cpu.getPC() - actual.getBcp().getBase();
            if (indice < 0) {
                throw new IllegalStateException("PC fuera del proceso");
            }
            if (indice >= actual.getPrograma().getCantInstrucciones()) {
                return terminar(actual, reloj, null);
            }
            
            Instruccion instruccion = actual.getPrograma().getInstrucciones().get(indice);
            if (instruccion.getTipoOp().equals("INT")) {
                ejecutarInterrupcion(actual, instruccion);
            } else {
                instrucciones.ejecutar(instruccion, actual.getBcp());
            }
            
            actual.registrarSegundo();
            despachador.guardarContexto();
            algoritmo.alTerminarSegundo(actual, reloj + 1);
            if ("FINALIZADO".equals(actual.getEstado()) || cpu.getPC() - actual.getBcp().getBase() >= actual.getPrograma().getCantInstrucciones()) {
                return terminar(actual, reloj + 1, null);
            }
            return new ResultadoSegundo(actual, false, null);
        } catch (Exception error) {
            actual = despachador.getProcesoActual();
            if (actual != null) {
                return terminar(actual, reloj + 1, error.getMessage());
            }
            throw new IllegalStateException(error.getMessage(), error);
        }
    }

    private ResultadoSegundo terminar(Proceso proceso, int tiempo, String error) {
        despachador.retirarProceso(error == null ? "FINALIZADO" : "ERROR");
        proceso.setTiempoFin(tiempo);
        procesos.remove(proceso);
        listos.remove(proceso);
        
        return new ResultadoSegundo(proceso, true, error);
    }

    private void ejecutarInterrupcion(Proceso proceso, Instruccion instruccion) {
        switch (instruccion.getOperando1().toUpperCase()) {
            case "20H":
                instrucciones.ejecutar(instruccion, proceso.getBcp());
                break;
            case "10H":
                salida.accept(cpu.getDX());
                cpu.setPC(cpu.getPC() + 1);
                break;
            case "21H":
                ejecutarInterrupcionArchivos(proceso);
                break;
            default:
                throw new IllegalArgumentException("Interrupción no soportada");
        }
    }

    private void ejecutarInterrupcionArchivos(Proceso proceso) {
        BCP bcp = proceso.getBcp();
        String funcion = cpu.getAH();
        String nombreArchivo = cpu.getDX();

        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new IllegalArgumentException("DX no contiene el nombre de un archivo.");
        }
        if (funcion.equals("3CH")) {
            manejadorAlmacenamiento.crearArchivo(nombreArchivo);
            salida.accept("[INT 21H] Archivo creado: " + nombreArchivo + "\n");
        } else if (funcion.equals("3DH")) {
            manejadorAlmacenamiento.abrirArchivo(nombreArchivo);
            bcp.agregarArchivoAbierto(nombreArchivo);
            salida.accept("[INT 21H] Archivo abierto: " + nombreArchivo + "\n");
        } else if (funcion.equals("4DH")) {
            if (!bcp.estaArchivoAbierto(nombreArchivo)) {
                throw new IllegalStateException("El archivo " + nombreArchivo + " no está abierto por el proceso.");
            }
            String contenido = manejadorAlmacenamiento.leerArchivo(nombreArchivo);
            if (contenido == null || contenido.isBlank()) {
                throw new IllegalStateException("El archivo " + nombreArchivo + " está vacío.");
            }
            int valor;
            try {
                valor = Integer.parseInt(contenido.trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("El contenido del archivo no es numérico.");
            }

            cpu.setRegistro("AL", valor);
            salida.accept("[INT 21H] Lectura de " + nombreArchivo + ": " + valor + " guardado en AL\n");
        } else if (funcion.equals("40H")) {
            if (!bcp.estaArchivoAbierto(nombreArchivo)) {
                throw new IllegalStateException(
                        "El archivo " + nombreArchivo
                        + " no está abierto por el proceso."
                );
            }
            int valor = cpu.getRegistro("AL");
            manejadorAlmacenamiento.escribirArchivo( nombreArchivo, String.valueOf(valor));
            salida.accept("[INT 21H] Se escribió " + valor + " en " + nombreArchivo + "\n");
        } else if (funcion.equals("41H")) {
            manejadorAlmacenamiento.eliminarArchivo(nombreArchivo);
            bcp.eliminarArchivoAbierto(nombreArchivo);
            salida.accept("[INT 21H] Archivo eliminado: " + nombreArchivo + "\n");
        } else {
            throw new IllegalArgumentException("Función de INT 21H inválida: " + funcion);
        }
        cpu.setPC(cpu.getPC() + 1);
    }


    public int getId() { 
        return id; 
    }
    public CPU getCpu() { 
        return cpu; 
    }
    public Despachador getDespachador() { 
        return despachador; 
    }
    public Proceso getProcesoActual() { 
        return despachador.getProcesoActual(); 
    }
    public List<Proceso> getProcesos() { 
        return new ArrayList<>(procesos); 
    }
    public List<Proceso> getProcesosPreparados() { 
        return new ArrayList<>(listos); 
    }
    public int cantidadProcesos() { 
        return procesos.size(); 
    }
    public boolean hayEspacio() { 
        return procesos.size() < ConfiguracionSistema.maxProcesosXNucleo; 
    }
    public int getTiempoOcioso() { 
        return tiempoOcioso; 
    }
    public int getDireccionInstruccionActual() { 
        return direccionInstruccionActual; 
    }
}
