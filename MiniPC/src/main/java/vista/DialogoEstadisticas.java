package vista;

import auxiliares.GestorEstadisticas;
import modelo.EstadisticaProceso;
import modelo.EstadisticaSimulacion;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/** Resultados completos y comparaciones conservados durante la sesión. */
public class DialogoEstadisticas extends JDialog {
    private static final Color FONDO = new Color(8, 25, 34);
    private static final Color PANEL = new Color(18, 46, 59);
    private static final Color BORDE = new Color(42, 92, 108);
    private static final Color TEXTO = new Color(239, 245, 247);
    private static final DecimalFormat DECIMALES = new DecimalFormat("0.00");
    private final PanelResultado actual = new PanelResultado(true);
    private final PanelResultado seleccionada = new PanelResultado(false);
    private final DefaultTableModel modeloAcumuladas = modelo(new Object[]{
        "Ejecución", "Algoritmo", "Inicio (s)", "Final (s)", "Tiempo ocioso (s)",
        "Estancia prom. (s)", "Tr/Ts prom."
    });
    private final JTable tablaAcumuladas = tabla(modeloAcumuladas);
    private List<EstadisticaSimulacion> ejecuciones = new ArrayList<>();

    public DialogoEstadisticas(JFrame padre) {
        super(padre, "Estadísticas de ejecución", false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(raiz);
        JLabel titulo = new JLabel("ESTADÍSTICAS DE EJECUCIÓN");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 21));
        raiz.add(titulo, BorderLayout.NORTH);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.setBackground(PANEL);
        pestanias.setForeground(TEXTO);
        pestanias.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pestanias.setUI(new BasicTabbedPaneUI() {
            protected void paintTabBackground(Graphics g, int ubicacion, int indice,
                    int x, int y, int ancho, int alto, boolean elegida) {
                g.setColor(elegida ? new Color(32, 126, 132) : PANEL);
                g.fillRect(x, y, ancho, alto);
            }
            protected void paintTabBorder(Graphics g, int ubicacion, int indice,
                    int x, int y, int ancho, int alto, boolean elegida) {
                g.setColor(BORDE);
                g.drawRect(x, y, ancho, alto);
            }
            protected void paintText(Graphics g, int ubicacion, Font fuente,
                    FontMetrics medidas, int indice, String titulo, Rectangle area, boolean elegida) {
                g.setFont(fuente);
                g.setColor(TEXTO);
                g.drawString(titulo, area.x, area.y + medidas.getAscent());
            }
            protected void paintContentBorder(Graphics g, int ubicacion, int elegida) {
                int y = calculateTabAreaHeight(ubicacion, runCount, maxTabHeight);
                g.setColor(BORDE);
                g.drawRect(0, y, tabPane.getWidth() - 1, tabPane.getHeight() - y - 1);
            }
        });
        pestanias.addTab("Ejecución actual", actual);
        JPanel acumuladas = new JPanel(new BorderLayout(0, 12));
        acumuladas.setBackground(FONDO);
        acumuladas.setBorder(new EmptyBorder(10, 10, 10, 10));
        tablaAcumuladas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAcumuladas.getColumnModel().getColumn(1).setPreferredWidth(180);
        JScrollPane resumen = scroll(tablaAcumuladas);
        resumen.setMinimumSize(new Dimension(0, 110));
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resumen, seleccionada);
        division.setBorder(null);
        division.setBackground(FONDO);
        division.setResizeWeight(0.35);
        acumuladas.add(division, BorderLayout.CENTER);
        pestanias.addTab("Acumuladas", acumuladas);
        raiz.add(pestanias, BorderLayout.CENTER);

        tablaAcumuladas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarSeleccionada();
        });
        setSize(1120, 600);
        setMinimumSize(new Dimension(900, 450));
        setLocationRelativeTo(padre);
    }

    public void actualizar(EstadisticaSimulacion resultado, GestorEstadisticas gestor) {
        actual.actualizar(resultado);
        int fila = tablaAcumuladas.getSelectedRow();
        List<EstadisticaSimulacion> nuevas = gestor.getEjecuciones();
        if (nuevas.size() != ejecuciones.size()) {
            ejecuciones = nuevas;
            modeloAcumuladas.setRowCount(0);
            int numero = 1;
            for (EstadisticaSimulacion e : ejecuciones) {
                modeloAcumuladas.addRow(new Object[]{numero++, e.getAlgoritmo(),
                    e.getInicio(), e.getFin(), e.getTiempoOciosoTotal(),
                    promedio(e.getPromedioEstancia(), e.getFinalizados()),
                    promedio(e.getPromedioRelacion(), e.getFinalizados())});
            }
            if (!ejecuciones.isEmpty()) {
                modeloAcumuladas.addRow(new Object[]{"Acumulado", "Todos", "—", "—",
                    gestor.getTiempoOciosoTotal(),
                    promedio(gestor.getPromedioEstancia(), gestor.getFinalizados()),
                    promedio(gestor.getPromedioRelacion(), gestor.getFinalizados())});
            }
            if (!ejecuciones.isEmpty()) {
                if (fila < 0 || fila >= ejecuciones.size()) fila = ejecuciones.size() - 1;
                tablaAcumuladas.setRowSelectionInterval(fila, fila);
            }
        }
        mostrarSeleccionada();
    }

    private void mostrarSeleccionada() {
        int fila = tablaAcumuladas.getSelectedRow();
        seleccionada.actualizar(fila >= 0 && fila < ejecuciones.size() ? ejecuciones.get(fila) : null);
    }

    private static String promedio(double valor, int cantidad) {
        return cantidad == 0 ? "—" : DECIMALES.format(valor);
    }
    private static Object tiempo(int valor) { return valor < 0 ? "—" : valor; }

    private static DefaultTableModel modelo(Object[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
    }
    private static JTable tabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setBackground(PANEL);
        tabla.setForeground(TEXTO);
        tabla.setGridColor(BORDE);
        tabla.setSelectionBackground(new Color(32, 126, 132));
        tabla.setSelectionForeground(TEXTO);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getTableHeader().setBackground(new Color(23, 55, 68));
        tabla.getTableHeader().setForeground(TEXTO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setFillsViewportHeight(true);
        for (int i = 0; i < tabla.getColumnCount(); i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(85);
        return tabla;
    }
    private static JScrollPane scroll(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        return scroll;
    }

    private static class PanelResultado extends JPanel {
        private final DefaultTableModel modeloResumen = modelo(new Object[]{
            "Algoritmo", "Inicio (s)", "Final (s)", "Tiempo ocioso (s)",
            "Estancia prom. (s)", "Tr/Ts prom."
        });
        private final DefaultTableModel modeloProcesos = modelo(new Object[]{
            "Proceso", "Inicio (s)", "Final (s)", "Estancia (s)", "Tr/Ts"
        });

        public PanelResultado(boolean mostrarResumen) {
            setLayout(new BorderLayout(0, 12));
            setBackground(FONDO);
            setBorder(new EmptyBorder(12, 12, 12, 12));
            if (mostrarResumen) {
                JTable resumen = tabla(modeloResumen);
                resumen.getColumnModel().getColumn(0).setPreferredWidth(190);
                JScrollPane encabezado = scroll(resumen);
                encabezado.setPreferredSize(new Dimension(0, 65));
                add(encabezado, BorderLayout.NORTH);
            }
            JTable procesos = tabla(modeloProcesos);
            procesos.getColumnModel().getColumn(0).setPreferredWidth(260);
            add(scroll(procesos), BorderLayout.CENTER);
        }

        public void actualizar(EstadisticaSimulacion resultado) {
            modeloResumen.setRowCount(0);
            modeloProcesos.setRowCount(0);
            if (resultado == null) return;
            modeloResumen.addRow(new Object[]{resultado.getAlgoritmo(), resultado.getInicio(),
                resultado.getFin(), resultado.getTiempoOciosoTotal(),
                promedio(resultado.getPromedioEstancia(), resultado.getFinalizados()),
                promedio(resultado.getPromedioRelacion(), resultado.getFinalizados())});
            for (EstadisticaProceso p : resultado.getProcesos()) {
                if (!p.isFinalizado()) continue;
                modeloProcesos.addRow(new Object[]{"Pr" + p.getPid() + " · " + p.getPrograma(),
                    tiempo(p.getInicio()), p.getFin(), p.getEstancia(),
                    p.getRelacion() == null ? "—" : DECIMALES.format(p.getRelacion())});
            }
        }
    }
}
