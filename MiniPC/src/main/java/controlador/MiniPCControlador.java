package controlador;

import auxiliares.*;
import modelo.*;
import vista.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/** Conecta los eventos de Swing con el simulador y presenta sus datos. */
public class MiniPCControlador {
    private final VentanaPrincipal vista;
    private ConfiguracionSistema configuracion = new ConfiguracionSistema();
    private Simulador simulador;
    private Timer timerEjecucion;
    private Memoria memoria;
    private MemoriaVirtual memoriaVirtual;
    private AlmacenamientoSecundario disco;
    private ListaTrabajos listaTrabajos;
    private ListaProcesos listaProcesos;

    public MiniPCControlador(VentanaPrincipal vista) {
        this.vista = vista;
        inicializarSistema();
        vista.getBtnConfiguracion().addActionListener(e -> abrirConfiguracion());
        vista.getBtnCargarArchivos().addActionListener(e -> cargarArchivos());
        vista.getBtnSiguiente().addActionListener(e -> ejecutarUnSegundo());
        vista.getBtnEjecutarAutomatico().addActionListener(e -> iniciarAutomatico());
        vista.getBtnReiniciar().addActionListener(e -> reiniciarSistema());
        vista.getBtnLlegadas().addActionListener(e -> configurarLlegadas());
        vista.getComboNucleos().addActionListener(e -> actualizarVistaCompleta());
        actualizarVistaCompleta();
    }

    private void inicializarSistema() {
        detenerTimer();
        simulador = new Simulador(configuracion, vista::escribirPantalla);
        memoria = simulador.getMemoria();
        memoriaVirtual = simulador.getMemoriaVirtual();
        disco = simulador.getDisco();
        listaTrabajos = simulador.getListaTrabajos();
        listaProcesos = simulador.getListaProcesos();
        vista.configurarNucleos(configuracion.getCantidadNucleos());
        vista.mostrarConfiguracion(configuracion);
        vista.limpiarPantalla();
        vista.setEstadoSistema("Sin programas cargados");
    }

    private void abrirConfiguracion() {
        DialogoConfiguracion dialogo = new DialogoConfiguracion(vista, configuracion);
        dialogo.setVisible(true);
        if (!dialogo.isGuardado()) return;
        if (!listaTrabajos.estaVacia() && JOptionPane.showConfirmDialog(vista,
                "Cambiar la configuración reiniciará la simulación. ¿Continuar?",
                "Configuración", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        configuracion = dialogo.getConfiguracion();
        inicializarSistema();
        actualizarVistaCompleta();
    }

    private void cargarArchivos() {
        JFileChooser selector = new JFileChooser();
        selector.setMultiSelectionEnabled(true);
        selector.setFileFilter(new FileNameExtensionFilter("Archivos ASM", "asm"));
        if (selector.showOpenDialog(vista) != JFileChooser.APPROVE_OPTION) return;
        ManejadorArchivos archivos = new ManejadorArchivos();
        ParserInstrucciones parser = new ParserInstrucciones();
        StringBuilder errores = new StringBuilder();
        for (File archivo : selector.getSelectedFiles()) {
            try {
                simulador.cargarPrograma(parser.analizar(archivo.getName(), archivos.leer(archivo)));
            } catch (Exception error) {
                errores.append(archivo.getName()).append(": ").append(error.getMessage()).append("\n");
            }
        }
        if (errores.length() > 0) JOptionPane.showMessageDialog(vista, errores.toString());
        vista.setEstadoSistema("Confirme los tiempos de llegada");
        actualizarVistaCompleta();
    }

    private void configurarLlegadas() {
        DialogoLlegadas dialogo = new DialogoLlegadas(vista, listaTrabajos.getTrabajos());
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            simulador.confirmarLlegadas();
            simulador.inicializarVisualizacion();
            vista.setEstadoSistema("Listo para ejecutar");
        }
        actualizarVistaCompleta();
    }

    public void ejecutarUnSegundo() {
        try {
            simulador.tick();
            if (simulador.haTerminado()) {
                detenerTimer();
                vista.setEstadoSistema("Simulación finalizada");
            } else vista.setEstadoSistema("Simulación en curso");
        } catch (Exception error) {
            detenerTimer();
            vista.setEstadoSistema("Error de ejecución");
            JOptionPane.showMessageDialog(vista, error.getMessage());
        }
        actualizarVistaCompleta();
    }

    private void iniciarAutomatico() {
        if (timerEjecucion != null && timerEjecucion.isRunning()) return;
        timerEjecucion = new Timer(1000, e -> ejecutarUnSegundo());
        timerEjecucion.start();
        actualizarBotones();
    }
    private void detenerTimer() {
        if (timerEjecucion != null) timerEjecucion.stop();
    }
    private void reiniciarSistema() {
        if (JOptionPane.showConfirmDialog(vista, "¿Eliminar la simulación y reiniciar?",
                "Reiniciar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        inicializarSistema();
        actualizarVistaCompleta();
    }
    private Nucleo nucleoSeleccionado() {
        int indice = vista.getComboNucleos().getSelectedIndex();
        if (indice < 0 || indice >= simulador.getNucleos().size()) indice = 0;
        return simulador.getNucleos().get(indice);
    }

    private void actualizarVistaCompleta() {
        actualizarMemoria();
        actualizarDisco();
        actualizarMemoriaVirtual();
        actualizarTrabajos();
        actualizarProcesos();
        actualizarBCPActual();
        Nucleo nucleo = nucleoSeleccionado();
        DefaultTableModel cola = vista.getModeloListos();
        cola.setRowCount(0);
        for (Proceso proceso : nucleo.getProcesosPreparados()) {
            cola.addRow(new Object[]{proceso.getPid(), proceso.getPrograma().getNombreArchivo(), proceso.getRestante()});
        }
        Proceso actual = nucleo.getProcesoActual();
        vista.actualizarInfoEjecucion(actual == null ? -1 : actual.getPid(),
                actual == null ? null : nucleo.getCpu().getIR(), simulador.getReloj());
        vista.getTblMemoria().clearSelection();
        int direccion = nucleo.getDireccionInstruccionActual();
        if (direccion >= memoria.getEspacioKernel() && direccion < memoria.getTamanio() && actual != null) {
            int fila = vista.getTblMemoria().convertRowIndexToView(direccion);
            if (fila >= 0) {
                vista.getTblMemoria().setRowSelectionInterval(fila, fila);
                vista.getTblMemoria().scrollRectToVisible(vista.getTblMemoria().getCellRect(fila, 0, true));
            }
        }
        actualizarBotones();
    }

    private void actualizarMemoria() {
        DefaultTableModel modelo = vista.getModeloMemoria();
        modelo.setRowCount(0);

        for (int i = 0; i < memoria.getTamanio(); i++) {
            String contenido = memoria.leer(i);

            if (contenido == null) {
                if (i < memoria.getEspacioKernel()) {
                    contenido = "Libre / Kernel";
                } else {
                    contenido = "Libre";
                }
            }

            modelo.addRow(new Object[]{i, contenido});
        }
    }

    private void actualizarDisco() {
        DefaultTableModel modelo = vista.getModeloDisco();
        modelo.setRowCount(0);

        for (int i = 0; i < disco.getTamanio(); i++) {
            String zona;

            if (disco.esIndice(i)) {
                zona = "Índice";
            } else if (disco.esMemoriaVirtual(i)) {
                zona = "Virtual";
            } else {
                zona = "Datos";
            }

            String contenido = disco.leer(i);

            if (contenido == null) {
                contenido = "Libre";
            }

            modelo.addRow(new Object[]{i, zona, contenido});
        }
    }

    private void actualizarMemoriaVirtual() {
        DefaultTableModel modelo = vista.getModeloMemoriaVirtual();
        modelo.setRowCount(0);

        for (int i = 0; i < memoriaVirtual.getTamanio(); i++) {
            String contenido = memoriaVirtual.leer(i);

            if (contenido == null) {
                contenido = "Libre";
            }

            int pid = memoriaVirtual.getPid(i);

            modelo.addRow(new Object[]{
                i,
                memoriaVirtual.obtenerPosicionDisco(i),
                contenido,
                pid == -1 ? "-" : pid
            });
        }
    }

    private void actualizarTrabajos() {
        DefaultTableModel modelo = vista.getModeloTrabajos();
        modelo.setRowCount(0);

        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            modelo.addRow(new Object[]{
                trabajo.getId(),
                trabajo.getPrograma().getNombreArchivo(),
                trabajo.getEstado(),
                trabajo.getPrograma().getCantInstrucciones(),
                trabajo.getTiempoLlegada()
            });
        }
    }

    private void actualizarProcesos() {
        DefaultTableModel modelo = vista.getModeloProcesos();
        modelo.setRowCount(0);

        for (Proceso proceso : listaProcesos.getProcesos()) {
            BCP bcp = proceso.getBcp();

            modelo.addRow(new Object[]{
                bcp.getPid(),
                proceso.getPrograma().getNombreArchivo(),
                bcp.getEstado(),
                bcp.getBase(),
                bcp.getAlcance(),
                bcp.getPrioridad(),
                bcp.getNucleoAsignado()
            });
        }
    }

    private void actualizarBCPActual() {
        DefaultTableModel modelo = vista.getModeloBCP();
        modelo.setRowCount(0);

        Proceso proceso = nucleoSeleccionado().getProcesoActual();

        if (proceso == null) {
            modelo.addRow(new Object[]{"Estado CPU", "Sin proceso"});
            return;
        }

        BCP bcp = proceso.getBcp();

        agregarDatoBCP(modelo, "Núcleo", bcp.getNucleoAsignado());
        agregarDatoBCP(modelo, "PID", bcp.getPid());
        agregarDatoBCP(modelo, "Estado", bcp.getEstado());
        agregarDatoBCP(modelo, "PC", bcp.getPc());
        agregarDatoBCP(modelo, "IR", bcp.getIr());
        agregarDatoBCP(modelo, "AC", bcp.getAc());
        agregarDatoBCP(modelo, "AX", bcp.getAx());
        agregarDatoBCP(modelo, "AL", bcp.getAl());
        agregarDatoBCP(modelo, "AH", bcp.getAh());
        agregarDatoBCP(modelo, "BX", bcp.getBx());
        agregarDatoBCP(modelo, "CX", bcp.getCx());
        agregarDatoBCP(modelo, "DX", bcp.getDx());
        agregarDatoBCP(modelo, "CMP", bcp.getComparacionIgual());
        agregarDatoBCP(modelo, "Pila", bcp.getPila());
        agregarDatoBCP(modelo, "CPU", bcp.getCpu());
        agregarDatoBCP(modelo, "Tiempo inicio", proceso.getTiempoInicio());
        agregarDatoBCP(modelo, "Tiempo empleado", bcp.getTiempoEmpleado());
        agregarDatoBCP(modelo, "Archivos abiertos", bcp.getArchivosAbiertos());
        agregarDatoBCP(modelo, "Siguiente BCP", bcp.getSiguienteBCP());
        agregarDatoBCP(modelo, "Base", bcp.getBase());
        agregarDatoBCP(modelo, "Alcance", bcp.getAlcance());
        agregarDatoBCP(modelo, "Prioridad", bcp.getPrioridad());
    }

    private void agregarDatoBCP(DefaultTableModel modelo, String atributo, Object valor) {
        modelo.addRow(new Object[]{atributo, valor});
    }


    private void actualizarBotones() {
        boolean automatico = timerEjecucion != null && timerEjecucion.isRunning();
        boolean hayDatos = !listaTrabajos.estaVacia();
        boolean ejecutar = hayDatos && simulador.isLlegadasConfirmadas() && !simulador.haTerminado();
        vista.getBtnSiguiente().setEnabled(ejecutar && !automatico);
        vista.getBtnEjecutarAutomatico().setEnabled(ejecutar && !automatico);
        vista.getBtnLlegadas().setEnabled(hayDatos && !simulador.isIniciado());
        vista.getBtnCargarArchivos().setEnabled(!simulador.isIniciado());
        vista.getBtnConfiguracion().setEnabled(!automatico);
        vista.getBtnReiniciar().setEnabled(hayDatos);
        vista.getBtnEstadisticas().setEnabled(false);
    }
}
