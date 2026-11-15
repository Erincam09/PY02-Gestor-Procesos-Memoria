package auxiliares.algoritmosPlanificacion;

import java.util.List;
import java.util.Map;

import auxiliares.AlgoritmoPlanificacion;

import java.util.ArrayList;
import java.util.HashMap;
import modelo.Proceso;

public class SelfishRR implements AlgoritmoPlanificacion {

    private int quantum;
    private int a; // Tasa cola de NUEVOS
    private int b; // Tasa cola de ACEPTADOS (b <= a)

    // para las prioridades de cada proceso en el algoritmo
    private final Map<Integer, Double> prioridades = new HashMap<>();
    
    // para ver los que ya fueron aceptados
    private final List<Integer> procesosAceptados = new ArrayList<>();

    public SelfishRR(int quantum, int a, int b) {
        this.quantum = quantum;
        System.out.println("Quantum: " + quantum + ", a: " + a + ", b: " + b);
        this.a = a;
        this.b = b;
    }
    
    public String getNombre() {
        return "Selfish Round Robin";
    }

    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        
        if (listos.isEmpty() && actual == null) {
            return null;
        }

        // Si el proceso actual no ha terminado su ráfaga, le queda tiempo de CPU y no ha terminado su quantum, debe seguir.
        if (actual != null) {
            if (actual.getQuantumUsado() < quantum) {
                return actual;
            } else {
                actual.setQuantumUsado(0);
            }
        }

        if (listos.isEmpty()) {
            return actual;
        }

       
        List<Proceso> aceptadosListos = new ArrayList<>();
        for (Proceso p : listos) {
            if (procesosAceptados.contains(p.getPid())) {
                aceptadosListos.add(p);
            }
        }

        if (!aceptadosListos.isEmpty()) {
            return aceptadosListos.get(0);
        } else {
            Proceso mejorNuevo = listos.get(0);
            double maxPrioridad = prioridades.getOrDefault(mejorNuevo.getPid(), 0.0);

            for (Proceso p : listos) {
                double prio = prioridades.getOrDefault(p.getPid(), 0.0);
                if (prio > maxPrioridad) {
                    maxPrioridad = prio;
                    mejorNuevo = p;
                }
            }
            
            if (!procesosAceptados.contains(mejorNuevo.getPid())) {
                procesosAceptados.add(mejorNuevo.getPid());
            }
            return mejorNuevo;
        }
    }


    public void alLlegar(Proceso proceso, int tiempo) { 
        prioridades.put(proceso.getPid(), 0.0);
        
        // Si la cola de aceptados está completamente vacía, este proceso se acepta inmediatamente.
        if (procesosAceptados.isEmpty()) {
            procesosAceptados.add(proceso.getPid());
        }
        // Si ya hay procesos en el sistema, entra como nuevo.
    }

    public void alTerminarSegundo(Proceso proceso, int tiempo) {
        
        for (Map.Entry<Integer, Double> entry : prioridades.entrySet()) {
            int pid = entry.getKey();
            double prioActual = entry.getValue();
            
            if (procesosAceptados.contains(pid)) {
                prioActual += b; // Los aceptados crecen a ritmo 'b'
            } else {
                prioActual += a; // Los nuevos crecen a ritmo 'a'
            }
            prioridades.put(pid, prioActual);
        }

        // ver si algún proceso nuevo ya alcanzó a los aceptados. ver cuál es la prioridad más baja actual entre los procesos aceptados
        double minPrioridadAceptados = Double.MAX_VALUE;
        boolean hayAceptadosActivos = false;

        for (int pid : procesosAceptados) {
            if (prioridades.containsKey(pid)) { // Validar que el proceso aún exista 
                double prio = prioridades.get(pid);
                if (prio < minPrioridadAceptados) {
                    minPrioridadAceptados = prio;
                }
                hayAceptadosActivos = true;
            }
        }

        // Si hay procesos aceptados, se aceptan a los nuevos que igualen o superen esa prioridad mínima
        if (hayAceptadosActivos) {
            List<Integer> nuevosAPromover = new ArrayList<>();
            for (Map.Entry<Integer, Double> entry : prioridades.entrySet()) {
                int pid = entry.getKey();
                double prio = entry.getValue();

                if (!procesosAceptados.contains(pid) && prio >= minPrioridadAceptados) {
                    nuevosAPromover.add(pid);
                }
            }
            procesosAceptados.addAll(nuevosAPromover);
        }
    }
}
