package auxiliares;

import java.util.List;
import modelo.Proceso;

public class Lottery implements AlgoritmoPlanificacion {

    private int quantum;
    private int tiempoEnQuantum; // Controla cuánto tiempo lleva el proceso actual

    public Lottery(int quantum) {
        this.quantum = quantum;
        this.tiempoEnQuantum = 0;
    }

    public String getNombre() {
        return "Lottery";
    }

    public Proceso elegir(List<Proceso> listos, Proceso actual, int tiempo) {
        if (listos.isEmpty() && actual == null) {
            return null;
        }

       
        if (actual != null) {
            
            if (actual.getRestante() == 0) { 
                tiempoEnQuantum = 0; 
            } 
            
            else if (tiempoEnQuantum < quantum) {
                return actual; 
            } 
            
            else if (listos.isEmpty()) {
                tiempoEnQuantum = 0; 
                return actual;
            }
           
            else {
                tiempoEnQuantum = 0; 
            }
        }

       
        if (listos.isEmpty()) {
            return null;
        }

        int total = 0;
        for (Proceso p : listos) {
            total += p.getTickets();
        }

        int ticketGanador = (int) (Math.random() * total);
        int ticketActual = 0;

        for (Proceso p : listos) {
            ticketActual += p.getTickets();
            if (ticketActual > ticketGanador) {
                tiempoEnQuantum = 0; 
                return p;
            }
        }

        return null;
    }

    
    public void alTerminarSegundo(Proceso proceso, int tiempo) {
        if (proceso != null) {
            tiempoEnQuantum++;
        }
    }

    public void alLlegar(Proceso proceso, int tiempo) {
    }
}
