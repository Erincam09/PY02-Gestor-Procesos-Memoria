package vista;

import modelo.Trabajo;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DialogoLlegadas extends JDialog {
    private final List<Trabajo> trabajos;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private boolean guardado;
    private final boolean esPorPrioridad;

    public DialogoLlegadas(JFrame padre, List<Trabajo> trabajos, String nombreAlgoritmo) {
        super(padre, "Configuración de Trabajos", true);
        this.trabajos = trabajos;
        this.esPorPrioridad = "Por Prioridad".equalsIgnoreCase(nombreAlgoritmo);

        Object[] columnas = esPorPrioridad ? 
            new Object[]{"Programa", "Ráfaga", "Llegada", "Prioridad"} :
            new Object[]{"Programa", "Ráfaga", "Llegada"};

        modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int fila, int columna) { 
                return esPorPrioridad ? (columna == 2 || columna == 3) : (columna == 2); 
            }
        };

        for (Trabajo trabajo : trabajos) {
            if (esPorPrioridad) {
                modelo.addRow(new Object[]{trabajo.getPrograma().getNombreArchivo(),
                    trabajo.getRafaga(), trabajo.getTiempoLlegada(), trabajo.getPrioridad()});
            } else {
                modelo.addRow(new Object[]{trabajo.getPrograma().getNombreArchivo(),
                    trabajo.getRafaga(), trabajo.getTiempoLlegada()});
            }
        }
        
        tabla = new JTable(modelo);
        setLayout(new BorderLayout(8, 8));
        add(new JLabel("  Configure los tiempos de llegada y prioridades de los programas."), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        
        JButton guardar = new JButton("Confirmar valores");
        guardar.addActionListener(e -> guardar());
        add(guardar, BorderLayout.SOUTH);
        
        setSize(640, 360);
        setLocationRelativeTo(padre);
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