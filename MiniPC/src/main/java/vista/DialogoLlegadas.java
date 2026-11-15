package vista;

import modelo.Trabajo;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;

public class DialogoLlegadas extends JDialog {
    private static final Color FONDO = new Color(8, 25, 34);
    private static final Color PANEL = new Color(18, 46, 59);
    private static final Color PANEL_2 = new Color(13, 37, 49);
    private static final Color PANEL_3 = new Color(23, 55, 68);
    private static final Color BORDE = new Color(42, 92, 108);
    private static final Color ACENTO = new Color(54, 174, 179);
    private static final Color TEXTO = new Color(239, 245, 247);
    private static final Color TEXTO_SEC = new Color(169, 194, 202);
    private final List<Trabajo> trabajos;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private boolean guardado;
    private final boolean esPorPrioridad;
    private final boolean esLottery; 

    public DialogoLlegadas(JFrame padre, List<Trabajo> trabajos, String nombreAlgoritmo) {
        super(padre, "Configuración de Trabajos", true);
        this.trabajos = trabajos;
        this.esPorPrioridad = "Por Prioridad".equalsIgnoreCase(nombreAlgoritmo);
        this.esLottery = "Lottery".equalsIgnoreCase(nombreAlgoritmo);

        Object[] columnas = null;
        if (esPorPrioridad) {
            columnas = new Object[]{"Programa", "Ráfaga (s)", "Llegada (s)", "Prioridad"};
        } else if (esLottery) {
            columnas = new Object[]{"Programa", "Ráfaga (s)", "Llegada (s)", "Tickets"};
        } else {
            columnas = new Object[]{"Programa", "Ráfaga (s)", "Llegada (s)"};
        }

        modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int fila, int columna) { 
                return esPorPrioridad ? (columna == 2 || columna == 3) : (esLottery ? (columna == 2 || columna == 3) : (columna == 2));
            }
        };

        for (Trabajo trabajo : trabajos) {
            if (esPorPrioridad) {
                modelo.addRow(new Object[]{trabajo.getPrograma().getNombreArchivo(),
                    trabajo.getRafaga(), trabajo.getTiempoLlegada(), trabajo.getPrioridad()});
            } else if (esLottery) {
                modelo.addRow(new Object[]{trabajo.getPrograma().getNombreArchivo(),
                    trabajo.getRafaga(), trabajo.getTiempoLlegada(), trabajo.getTickets()});
            } else {
                modelo.addRow(new Object[]{trabajo.getPrograma().getNombreArchivo(),
                    trabajo.getRafaga(), trabajo.getTiempoLlegada()});
            }
        }
        
        tabla = new JTable(modelo);
        crearInterfaz(nombreAlgoritmo);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 440);
        setMinimumSize(new Dimension(640, 340));
        setLocationRelativeTo(padre);
    }

    private void crearInterfaz(String nombreAlgoritmo) {
        JPanel raiz = new JPanel(new BorderLayout(0, 18));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(20, 20, 18, 20));
        setContentPane(raiz);

        JPanel encabezado = new JPanel(new BorderLayout(12, 0));
        encabezado.setOpaque(false);
        JLabel titulo = new JLabel("Tiempos de llegada");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        JLabel algoritmo = new JLabel(nombreAlgoritmo);
        algoritmo.setForeground(ACENTO);
        algoritmo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(algoritmo, BorderLayout.EAST);
        raiz.add(encabezado, BorderLayout.NORTH);

        configurarTabla();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(PANEL);
        scroll.getVerticalScrollBar().setUnitIncrement(34);
        raiz.add(scroll, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(12, 0));
        inferior.setOpaque(false);
        JLabel ayuda = new JLabel("Celdas resaltadas: valores editables.");
        ayuda.setForeground(TEXTO_SEC);
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inferior.add(ayuda, BorderLayout.WEST);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton cancelar = crearBoton("Cancelar", false);
        cancelar.addActionListener(e -> dispose());
        JButton guardar = crearBoton("Confirmar valores", true);
        guardar.addActionListener(e -> guardar());
        botones.add(cancelar);
        botones.add(guardar);
        inferior.add(botones, BorderLayout.EAST);
        raiz.add(inferior, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(guardar);
    }

    private void configurarTabla() {
        tabla.setBackground(PANEL);
        tabla.setForeground(TEXTO);
        tabla.setGridColor(BORDE);
        tabla.setRowHeight(34);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setFillsViewportHeight(true);
        tabla.setCellSelectionEnabled(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.putClientProperty("terminateEditOnFocusLost", true);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(350);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tabla, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
                setOpaque(true);
                setBackground(PANEL_2);
                setForeground(TEXTO);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setHorizontalAlignment(columna == 0 ? LEFT : CENTER);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, BORDE),
                        new EmptyBorder(0, 10, 0, 10)));
                return this;
            }
        });
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tabla, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
                boolean editable = tabla.isCellEditable(fila, columna);
                setBackground(seleccionada ? new Color(32, 126, 132) : editable ? PANEL_3 : PANEL);
                setForeground(seleccionada || columna == 0 ? TEXTO : editable ? ACENTO : TEXTO_SEC);
                setFont(new Font("Segoe UI", editable ? Font.BOLD : Font.PLAIN, 13));
                setHorizontalAlignment(columna == 0 ? LEFT : CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        JTextField campo = new JTextField();
        campo.setBackground(PANEL_2);
        campo.setForeground(TEXTO);
        campo.setCaretColor(ACENTO);
        campo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setBorder(BorderFactory.createLineBorder(ACENTO));
        DefaultCellEditor editor = new DefaultCellEditor(campo);
        editor.setClickCountToStart(1);
        tabla.setDefaultEditor(Object.class, editor);
    }

    private JButton crearBoton(String texto, boolean principal) {
        JButton boton = new JButton(texto);
        boton.setBackground(principal ? ACENTO : PANEL_3);
        boton.setForeground(principal ? FONDO : TEXTO);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(principal ? ACENTO : BORDE),
                new EmptyBorder(9, 15, 9, 15)));
        return boton;
    }

    private void guardar() {
        if (tabla.isEditing() && !tabla.getCellEditor().stopCellEditing()) return;
        try {
            for (int i = 0; i < trabajos.size(); i++) {
                int llegada = Integer.parseInt(String.valueOf(modelo.getValueAt(i, 2)).trim());
                if (llegada < 0) throw new NumberFormatException();
                trabajos.get(i).setTiempoLlegada(llegada);

                if (esPorPrioridad) {
                    int prioridad = Integer.parseInt(String.valueOf(modelo.getValueAt(i, 3)).trim());
                    if (prioridad < 0) throw new NumberFormatException();
                    trabajos.get(i).setPrioridad(prioridad);
                } else if (esLottery) {
                    int tickets = Integer.parseInt(String.valueOf(modelo.getValueAt(i, 3)).trim());
                    if (tickets < 0) throw new NumberFormatException();
                    trabajos.get(i).setTickets(tickets);
                }
            }
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "Los valores numéricos deben ser enteros mayores o iguales a cero.");
            return;
        }
        guardado = true;
        dispose();
    }
    public boolean isGuardado() { return guardado; }
}