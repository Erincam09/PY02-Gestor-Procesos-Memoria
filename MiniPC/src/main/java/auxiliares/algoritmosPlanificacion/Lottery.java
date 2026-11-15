package auxiliares;

import java.util.List;
import java.util.ArrayList;
import modelo.Proceso;

public class Lottery implements AlgoritmoPlanificacion {

    private final int quantum;

    public Lottery(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum debe ser mayor que cero");
        }
        this.quantum = quantum;
    }

    
    public String getNombre() {
        return "Lottery";
    }

    
    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        
        if (listos.isEmpty() && actual == null) {
            return null;
        }

        
        if (actual != null) {
            if (actual.getQuantumUsado() < quantum) {
                
                return actual;
            } else {
                
                actual.setQuantumUsado(0);
            }
        }

        
        List<Proceso> competidores = new ArrayList<>(listos);
        if (actual != null) {
            competidores.add(actual);
        }

       
        int totalTickets = 0;
        for (Proceso p : competidores) {
            totalTickets += p.getTickets();
        }

        int ticketGanador = (int) (Math.random() * totalTickets);
        int ticketAcumulado = 0;

        for (Proceso p : competidores) {
            ticketAcumulado += p.getTickets();
            if (ticketGanador < ticketAcumulado) {
                return p;
            }
        }

        
        return competidores.get(0);
    }

  
    public void alLlegar(Proceso proceso, int tiempo) { }

    
    public void alTerminarSegundo(Proceso proceso, int tiempo) { }
}