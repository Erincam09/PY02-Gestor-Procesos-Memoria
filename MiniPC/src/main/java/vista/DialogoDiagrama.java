package vista;

import modelo.Proceso;
import modelo.RegistroGantt;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Cuadrícula por proceso, consultable sin detener la simulación. */
public class DialogoDiagrama extends JDialog {
    private static final Color FONDO = new Color(8, 25, 34);
    private static final Color TEXTO = new Color(239, 245, 247);
    private static final Color PANEL = new Color(18, 46, 59);
    private static final Color PANEL_2 = new Color(13, 37, 49);
    private static final Color BORDE = new Color(42, 92, 108);
    private final JLabel resumen = new JLabel();
    private final PanelGantt grafico = new PanelGantt();

    public DialogoDiagrama(JFrame padre) {
        super(padre, "Diagrama de Gantt", false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel raiz = new JPanel(new BorderLayout(10, 15));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(raiz);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        resumen.setForeground(TEXTO);
        resumen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cabecera.add(resumen, BorderLayout.WEST);
        raiz.add(cabecera, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(grafico);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(FONDO);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(28);
        raiz.add(scroll, BorderLayout.CENTER);

        JLabel ayuda = new JLabel("<html>Turquesa = ejecución; gris = núcleo ocioso.</html>");
        ayuda.setForeground(new Color(169, 194, 202));
        raiz.add(ayuda, BorderLayout.SOUTH);
        setSize(1100, 600);
        setMinimumSize(new Dimension(850, 380));
        setLocationRelativeTo(padre);
    }

    public void actualizar(List<RegistroGantt> historial, List<Proceso> procesos,
            int reloj, int cantidadNucleos, String algoritmo) {
        resumen.setText(algoritmo + "   |   Reloj: " + reloj + " s");
        grafico.actualizar(historial, procesos, reloj, cantidadNucleos);
    }

    private static class FilaProceso {
        private final int nucleo;
        private final int pid;
        private final String programa;

        public FilaProceso(int nucleo, int pid, String programa) {
            this.nucleo = nucleo;
            this.pid = pid;
            this.programa = programa;
        }
    }

    private static class PanelGantt extends JPanel implements Scrollable {
        private static final int MARGEN = 16;
        private static final int ANCHO_NOMBRE = 90;
        private static final int ALTO_FILA = 28;
        private static final int SEPARACION = 30;
        private static final Color EJECUCION = new Color(54, 174, 179);
        private static final Color CABECERA = new Color(23, 55, 68);
        private static final Color OCIOSO = new Color(48, 65, 75);
        private List<RegistroGantt> historial = new ArrayList<>();
        private final ArrayList<FilaProceso> filas = new ArrayList<>();
        private int reloj;
        private int nucleos = 1;

        public PanelGantt() {
            setBackground(FONDO);
            setToolTipText("");
        }

        public void actualizar(List<RegistroGantt> datos, List<Proceso> procesos,
                int tiempo, int cantidad) {
            historial = new ArrayList<>(datos);
            reloj = tiempo;
            nucleos = cantidad;
            filas.clear();
            // Incluir procesos listos o en virtual, aunque todavía no hayan ejecutado.
            for (Proceso proceso : procesos) {
                agregarFila(proceso.getBcp().getNucleoAsignado(), proceso.getPid(),
                        proceso.getPrograma().getNombreArchivo());
            }
            // Mantener también las filas de los que ya finalizaron.
            for (RegistroGantt registro : historial) {
                agregarFila(registro.getNucleo(), registro.getPid(), registro.getPrograma());
            }
            actualizarTamanio();
        }

        private void agregarFila(int nucleo, int pid, String programa) {
            if (nucleo < 0 || nucleo >= nucleos) return;
            for (FilaProceso fila : filas) {
                if (fila.nucleo == nucleo && fila.pid == pid) return;
            }
            FilaProceso nueva = new FilaProceso(nucleo, pid, programa);
            int posicion = 0;
            // Orden de núcleo y PID; la fila ociosa queda al final de cada núcleo.
            while (posicion < filas.size()) {
                FilaProceso otra = filas.get(posicion);
                if (otra.nucleo > nucleo) break;
                if (otra.nucleo == nucleo && pid >= 0 && (otra.pid < 0 || otra.pid > pid)) break;
                posicion++;
            }
            filas.add(posicion, nueva);
        }

        private int cantidadFilas(int nucleo) {
            int cantidad = 0;
            for (FilaProceso fila : filas) {
                if (fila.nucleo == nucleo) cantidad++;
            }
            return Math.max(1, cantidad);
        }

        private int columnas() { return Math.max(20, reloj); }

        private void actualizarTamanio() {
            int alto = MARGEN;
            for (int nucleo = 0; nucleo < nucleos; nucleo++) {
                alto += (1 + cantidadFilas(nucleo)) * ALTO_FILA + SEPARACION;
            }
            setPreferredSize(new Dimension(850, Math.max(250, alto)));
            revalidate();
            repaint();
        }

        private int anchoDisponible() {
            return Math.max(1, getWidth() - MARGEN * 2 - ANCHO_NOMBRE);
        }

        private int inicioColumna(int columna) {
            return MARGEN + ANCHO_NOMBRE
                    + (int) ((long) columna * anchoDisponible() / columnas());
        }

        private int anchoColumna(int columna) {
            return inicioColumna(columna + 1) - inicioColumna(columna);
        }

        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        public boolean getScrollableTracksViewportWidth() { return true; }
        public boolean getScrollableTracksViewportHeight() { return false; }
        public int getScrollableUnitIncrement(Rectangle visible, int orientacion, int direccion) {
            return ALTO_FILA;
        }
        public int getScrollableBlockIncrement(Rectangle visible, int orientacion, int direccion) {
            return Math.max(ALTO_FILA, visible.height - ALTO_FILA);
        }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            int y = MARGEN;
            for (int nucleo = 0; nucleo < nucleos; nucleo++) {
                dibujarCelda(g, MARGEN, y, ANCHO_NOMBRE, CABECERA, "Núcleo " + nucleo);
                // Al comprimirse las columnas, espaciar los números para que se lean.
                int paso = Math.max(1, (int) Math.ceil(columnas() * 32.0 / anchoDisponible()));
                for (int segundo = 0; segundo < columnas(); segundo += paso) {
                    int fin = Math.min(columnas(), segundo + paso);
                    dibujarCelda(g, inicioColumna(segundo), y,
                            inicioColumna(fin) - inicioColumna(segundo), CABECERA,
                            paso == 1 ? String.valueOf(segundo + 1) : String.valueOf(fin));
                }
                y += ALTO_FILA;
                boolean hayFilas = false;
                for (FilaProceso fila : filas) {
                    if (fila.nucleo != nucleo) continue;
                    hayFilas = true;
                    String etiqueta = fila.pid < 0 ? "Ocioso" : "Pr" + fila.pid;
                    dibujarCelda(g, MARGEN, y, ANCHO_NOMBRE, PANEL_2, etiqueta);
                    for (int segundo = 0; segundo < columnas(); segundo++) {
                        dibujarCelda(g, inicioColumna(segundo),
                                y, anchoColumna(segundo), PANEL, "");
                    }
                    for (RegistroGantt registro : historial) {
                        if (registro.getNucleo() != nucleo || registro.getPid() != fila.pid) continue;
                        for (int segundo = registro.getInicio(); segundo < registro.getFin(); segundo++) {
                            dibujarCelda(g, inicioColumna(segundo),
                                    y, anchoColumna(segundo), fila.pid < 0 ? OCIOSO : EJECUCION, "");
                        }
                    }
                    y += ALTO_FILA;
                }
                if (!hayFilas) {
                    dibujarCelda(g, MARGEN, y, ANCHO_NOMBRE, PANEL_2, "Sin procesos");
                    for (int segundo = 0; segundo < columnas(); segundo++) {
                        dibujarCelda(g, inicioColumna(segundo),
                                y, anchoColumna(segundo), PANEL, "");
                    }
                    y += ALTO_FILA;
                }
                y += SEPARACION;
            }
            g.dispose();
        }

        private void dibujarCelda(Graphics2D g, int x, int y, int ancho, Color fondo, String texto) {
            g.setColor(fondo);
            g.fillRect(x, y, Math.max(1, ancho), ALTO_FILA);
            if (ancho >= 4) {
                g.setColor(BORDE);
                g.drawRect(x, y, ancho, ALTO_FILA);
            }
            if (texto.isEmpty() || ancho < 12) return;
            FontMetrics medidas = g.getFontMetrics();
            if (medidas.stringWidth(texto) > ancho - 8) {
                while (!texto.isEmpty() && medidas.stringWidth(texto + "…") > ancho - 8) {
                    texto = texto.substring(0, texto.length() - 1);
                }
                texto += "…";
            }
            g.setColor(TEXTO);
            g.drawString(texto, x + (ancho - medidas.stringWidth(texto)) / 2, y + 19);
        }

        public String getToolTipText(MouseEvent evento) {
            int desplazamiento = evento.getX() - MARGEN - ANCHO_NOMBRE;
            if (desplazamiento < 0 || desplazamiento >= anchoDisponible()) return null;
            int columna = (int) ((((long) desplazamiento + 1) * columnas() - 1) / anchoDisponible());
            int y = MARGEN;
            for (int nucleo = 0; nucleo < nucleos; nucleo++) {
                y += ALTO_FILA;
                boolean hayFilas = false;
                for (FilaProceso fila : filas) {
                    if (fila.nucleo != nucleo) continue;
                    hayFilas = true;
                    if (evento.getY() >= y && evento.getY() < y + ALTO_FILA) {
                        String nombre = fila.pid < 0 ? "Ocioso" : "Pr" + fila.pid + " · " + fila.programa;
                        boolean ejecutado = false;
                        for (RegistroGantt registro : historial) {
                            if (registro.getNucleo() == nucleo && registro.getPid() == fila.pid
                                    && registro.getInicio() <= columna && columna < registro.getFin()) {
                                ejecutado = true;
                                break;
                            }
                        }
                        String estado = columna >= reloj ? "sin transcurrir"
                                : ejecutado ? (fila.pid < 0 ? "ocioso" : "ejecutó") : "no ejecutó";
                        return "Núcleo " + nucleo + " | " + nombre + " | " + columna + "–"
                                + (columna + 1) + " s | " + estado;
                    }
                    y += ALTO_FILA;
                }
                if (!hayFilas) y += ALTO_FILA;
                y += SEPARACION;
            }
            return null;
        }
    }
}
