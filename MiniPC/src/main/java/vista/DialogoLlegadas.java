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

    public DialogoLlegadas(JFrame padre, List<Trabajo> trabajos) {
        super(padre, "Tiempos de llegada", true);
        this.trabajos = trabajos;
        modelo = new DefaultTableModel(new Object[]{"Programa", "Ráfaga", "Llegada"}, 0) {
            public boolean isCellEditable(int fila, int columna) { return columna == 2; }
        };
        for (Trabajo trabajo : trabajos) {
            modelo.addRow(new Object[]{trabajo.getPrograma().getNombreArchivo(),
                trabajo.getRafaga(), trabajo.getTiempoLlegada()});
        }
        tabla = new JTable(modelo);
        setLayout(new BorderLayout(8, 8));
        add(new JLabel("Confirme las llegadas; puede aceptar todos los valores en cero."), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        JButton guardar = new JButton("Confirmar llegadas");
        guardar.addActionListener(e -> guardar());
        add(guardar, BorderLayout.SOUTH);
        setSize(620, 360);
        setLocationRelativeTo(padre);
    }

    private void guardar() {
        if (tabla.isEditing() && !tabla.getCellEditor().stopCellEditing()) return;
        int[] llegadas = new int[trabajos.size()];
        try {
            for (int i = 0; i < trabajos.size(); i++) {
                llegadas[i] = Integer.parseInt(String.valueOf(modelo.getValueAt(i, 2)).trim());
                if (llegadas[i] < 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "Las llegadas deben ser enteros mayores o iguales a cero");
            return;
        }
        for (int i = 0; i < trabajos.size(); i++) trabajos.get(i).setTiempoLlegada(llegadas[i]);
        guardado = true;
        dispose();
    }
    public boolean isGuardado() { return guardado; }
}
