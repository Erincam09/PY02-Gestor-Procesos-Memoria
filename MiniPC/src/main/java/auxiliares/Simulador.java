package auxiliares;

import modelo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Simulador {
    private final ConfiguracionSistema configuracion;
    private final Consumer<String> salida;
    private int reloj;
    private ArrayList<EstadisticaProceso> resultadosProcesos;
    private EstadisticaSimulacion estadisticaFinal;
    private ArrayList<RegistroGantt> historialGantt;
    private int siguientePid;
    private boolean llegadasConfirmadas;
    private boolean iniciado;
    private boolean fetchInicialHecho;
    private ArrayList<Nucleo> nucleos;
    private ListaTrabajos listaTrabajos;
    private ListaProcesos listaProcesos;
    private Memoria memoria;
    private MemoriaVirtual memoriaVirtual;
    private AlmacenamientoSecundario disco;
    private ManejoBCP manejoBCP;
    private ManejadorAlmacenamiento almacenamiento;
    private GestorAdmisionTrabajos admision;
    private final AsignadorNucleos asignador = new AsignadorNucleos();
    private CargarPrograma cargador;
    private EstrategiaMemoria estrategiaMemoria;

    public Simulador(ConfiguracionSistema configuracion, Consumer<String> salida) {
        this.configuracion = new ConfiguracionSistema(configuracion);
        this.salida = salida;
        reiniciar();
    }

    public void reiniciar() {
        reloj = 0;
        resultadosProcesos = new ArrayList<>();
        estadisticaFinal = null;
        historialGantt = new ArrayList<>();
        siguientePid = 1;
        iniciado = false;
        llegadasConfirmadas = false;
        fetchInicialHecho = false;
        memoria = new Memoria(configuracion.getMemoriaPrincipal());
        disco = new AlmacenamientoSecundario(configuracion.getAlmacenamientoSecundario(), configuracion.getMemoriaVirtual());
        memoriaVirtual = new MemoriaVirtual(disco);
        listaTrabajos = new ListaTrabajos();
        listaProcesos = new ListaProcesos();
        manejoBCP = new ManejoBCP();
        almacenamiento = new ManejadorAlmacenamiento(disco);
        admision = new GestorAdmisionTrabajos(listaTrabajos);
        estrategiaMemoria = FabricaEstrategiasMemoria.crear(configuracion.getNombreEstrategiaMemoria(),
                configuracion.getParametrosEstrategiaMemoria(), configuracion.getTamaniosParticiones());
        cargador = new CargarPrograma(estrategiaMemoria);
        estrategiaMemoria.iniciar(memoria);
        nucleos = new ArrayList<>();
        for (int i = 0; i < configuracion.getCantidadNucleos(); i++) {
            AlgoritmoPlanificacion algoritmo = FabricaAlgoritmos.crear(
                    configuracion.getNombreAlgoritmo(), configuracion.getParametrosAlgoritmo());
            nucleos.add(new Nucleo(i, algoritmo, almacenamiento, salida));
        }
    }

    public void cargarPrograma(Programa programa) {
        if (iniciado) {
            throw new IllegalStateException("Reinicie antes de cargar más programas");
        }
        // Una posición del kernel por trabajo, sin ocupar el área reservada para BCP.
        if (listaTrabajos.cantidadTrabajos() + 1 + manejoBCP.getTamanioBCP() > memoria.getEspacioKernel()) {
            throw new IllegalStateException("No hay espacio en el kernel para otro trabajo");
        }
        for (Instruccion instruccion : programa.getInstrucciones()) {
            if (instruccion.getTipoOp().equals("INT") && instruccion.getOperando1().equalsIgnoreCase("09H")) {
                throw new IllegalArgumentException("INT 09H ya no está disponible");
            }
        }
        almacenamiento.guardarPrograma(programa);
        listaTrabajos.agregarTrabajo(programa);
        llegadasConfirmadas = false;
        sincronizarTrabajos();
    }

    public void confirmarLlegadas() { 
        llegadasConfirmadas = true; 
    }


    public void inicializarVisualizacion() {
        if (!llegadasConfirmadas || fetchInicialHecho) {
            return;
        }
        iniciado = true;
        fetchInicialHecho = true;
        prepararInstante();
    }

    private void prepararInstante() {
        admitirTrabajos();
        for (Nucleo nucleo : nucleos) {
            nucleo.prepararSegundo(reloj);
        }
        for (Proceso proceso : listaProcesos.getProcesos()) {
            manejoBCP.sincronizarBCP(memoria, proceso.getBcp());
        }
        sincronizarTrabajos();
    }

    public void tick() {
        if (!llegadasConfirmadas) {
            throw new IllegalStateException("Confirme los tiempos de llegada");
        }
        if (haTerminado() || listaTrabajos.estaVacia()) {
            return;
        }
        inicializarVisualizacion();
        if (haTerminado()) return;
        ArrayList<ResultadoSegundo> terminados = new ArrayList<>();
        for (Nucleo nucleo : nucleos) {
            // Capturar al que ejecuta este segundo, antes de preparar el siguiente.
            Proceso ejecutado = nucleo.getProcesoActual();
            ResultadoSegundo resultado = nucleo.ejecutarSegundo(reloj);
            int pid = ejecutado == null ? -1 : ejecutado.getPid();
            String programa = ejecutado == null ? "Ocioso" : ejecutado.getPrograma().getNombreArchivo();
            historialGantt.add(new RegistroGantt(nucleo.getId(), pid, programa, reloj, reloj + 1));
            if (resultado.isTerminado()) {
                terminados.add(resultado);
            }
        }
        for (ResultadoSegundo resultado : terminados) {
            finalizarProceso(resultado);
        }
        reloj++;
        if (haTerminado()) {
            for (Proceso proceso : listaProcesos.getProcesos()) {
                manejoBCP.sincronizarBCP(memoria, proceso.getBcp());
            }
            sincronizarTrabajos();
        } else {
            prepararInstante();
        }
    }

    private void admitirTrabajos() {
        for (Trabajo trabajo : admision.obtenerPendientes(reloj)) {
            Nucleo nucleo = asignador.elegirNucleo(nucleos);
            if (nucleo == null || !manejoBCP.hayEspacioParaBCP(memoria)) {
                continue;
            }
            Programa programa = trabajo.getPrograma();
            int alcance = programa.getCantInstrucciones();
            if (!estrategiaMemoria.puedeAlojar(memoria, alcance)) {
                trabajo.setEstado("RECHAZADO");
                resultadosProcesos.add(new EstadisticaProceso(trabajo, reloj));
                salida.accept("Programa rechazado por tamaño: " + programa.getNombreArchivo() + "\n");
                continue;
            }
            AsignacionMemoria asignacion = cargador.cargar(programa, memoria);
            int base = asignacion == null ? -1 : asignacion.getBase();
            int virtual = -1;
            if (base == -1) {
                virtual = memoriaVirtual.cargarPrograma(programa, siguientePid);
                if (virtual == -1) {
                    continue;
                }
            }
            CPU inicial = new CPU();
            inicial.setPC(base);
            BCP bcp = manejoBCP.crearBCP(memoria, siguientePid, inicial, base, alcance,
                    trabajo.getPrioridad(), programa.getNombreArchivo(), base == -1 ? "VIRTUAL" : "RAM");
            bcp.setDireccionVirtual(virtual);
            bcp.setEstado(base == -1 ? "EN ESPERA" : "PREPARADO");
            Proceso proceso = new Proceso(programa, bcp);
            proceso.setAsignacionMemoria(asignacion);
            proceso.setLlegada(trabajo.getTiempoLlegada());
            proceso.setTickets(trabajo.getTickets());
            nucleo.agregarProceso(proceso, reloj);
            listaProcesos.agregarProceso(proceso);
            trabajo.setEstado(base == -1 ? "EN MEMORIA VIRTUAL" : "EN MEMORIA");
            manejoBCP.sincronizarBCP(memoria, bcp);
            siguientePid++;
        }
    }

    private void finalizarProceso(ResultadoSegundo resultado) {
        Proceso proceso = resultado.getProceso();
        BCP bcp = proceso.getBcp();
        // Conservar el resultado antes de liberar el BCP y eliminar el proceso.
        resultadosProcesos.add(new EstadisticaProceso(proceso));
        if (proceso.getAsignacionMemoria() != null) {
            estrategiaMemoria.liberar(memoria, proceso.getAsignacionMemoria());
        }
        if (bcp.getDireccionVirtual() != -1) {
            memoriaVirtual.liberarRango(bcp.getDireccionVirtual(), bcp.getAlcance());
        }
        Trabajo trabajo = buscarTrabajo(proceso.getPrograma());
        trabajo.setEstado(resultado.getError() == null ? "FINALIZADO" : "ERROR");
        if (resultado.getError() != null) {
            salida.accept("Error PID " + proceso.getPid() + ": " + resultado.getError() + "\n");
        }
        manejoBCP.liberarBCP(memoria, proceso.getPid());
        listaProcesos.eliminarProceso(proceso);
        intentarPasarVirtualARAM();
    }

    private void intentarPasarVirtualARAM() {
        for (Proceso proceso : listaProcesos.getProcesos()) {
            BCP bcp = proceso.getBcp();
            if (!bcp.getEstado().equals("EN ESPERA") || bcp.getDireccionVirtual() == -1) {
                continue;
            }  
            AsignacionMemoria asignacion = cargador.cargar(proceso.getPrograma(), memoria);
            if (asignacion == null) {
                continue;
            }
            memoriaVirtual.liberarRango(bcp.getDireccionVirtual(), bcp.getAlcance());
            proceso.setAsignacionMemoria(asignacion);
            bcp.setDireccionVirtual(-1);
            bcp.setBase(asignacion.getBase());
            bcp.setPc(asignacion.getBase());
            bcp.setEstado("PREPARADO");
            bcp.setUbicacionPrograma("RAM");
            nucleos.get(bcp.getNucleoAsignado()).agregarListo(proceso, reloj + 1);
            buscarTrabajo(proceso.getPrograma()).setEstado("EN MEMORIA");
            manejoBCP.sincronizarBCP(memoria, bcp);
        }
    }

    private Trabajo buscarTrabajo(Programa programa) {
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            if (trabajo.getPrograma() == programa) return trabajo;
        }
        throw new IllegalStateException("Trabajo no encontrado");
    }

    private void sincronizarTrabajos() {
        int posicion = 0;
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            memoria.escribir(posicion, "TRABAJO " + trabajo.getId() + " | "
                    + trabajo.getPrograma().getNombreArchivo() + " | " + trabajo.getEstado());
            posicion++;
        }
    }

    public boolean haTerminado() {
        if (listaTrabajos.estaVacia()) return false;
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            String estado = trabajo.getEstado();
            if (!estado.equals("FINALIZADO") && !estado.equals("RECHAZADO") && !estado.equals("ERROR")) {
                return false;
            }
        }
        return true;
    }
    public EstadisticaSimulacion getEstadisticaFinal() {
        if (!haTerminado()) return null;
        if (estadisticaFinal == null) {
            int[] ociosidad = new int[nucleos.size()];
            for (Nucleo nucleo : nucleos) ociosidad[nucleo.getId()] = nucleo.getTiempoOcioso();
            estadisticaFinal = new EstadisticaSimulacion(configuracion, reloj, resultadosProcesos, ociosidad);
        }
        return estadisticaFinal;
    }

    public List<RegistroGantt> getHistorialGantt() {
        return new ArrayList<>(historialGantt);
    }
    public int getReloj() { return reloj; }
    public boolean isIniciado() { return iniciado; }
    public boolean isLlegadasConfirmadas() { return llegadasConfirmadas; }
    public List<Nucleo> getNucleos() { return new ArrayList<>(nucleos); }
    public ListaTrabajos getListaTrabajos() { return listaTrabajos; }
    public ListaProcesos getListaProcesos() { return listaProcesos; }
    public Memoria getMemoria() { return memoria; }
    public EstrategiaMemoria getEstrategiaMemoria() { return estrategiaMemoria; }
    public MemoriaVirtual getMemoriaVirtual() { return memoriaVirtual; }
    public AlmacenamientoSecundario getDisco() { return disco; }
}
