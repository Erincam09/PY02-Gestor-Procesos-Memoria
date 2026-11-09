package modelo;

public class Trabajo {
    private int id;
    private Programa programa;
    private String estado;
    private int tiempoLlegada;
    private int prioridad = 1;
    private int tickets = 1;

    public Trabajo(int id, Programa programa) {
        this.id = id;
        this.programa = programa;
        this.estado = "PENDIENTE";
    }

    public int getTiempoLlegada() { return tiempoLlegada; }
    public void setTiempoLlegada(int tiempo) {
        if (tiempo < 0) throw new IllegalArgumentException("La llegada no puede ser negativa");
        tiempoLlegada = tiempo;
    }
    public int getRafaga() { return programa.getCantInstrucciones(); }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int valor) { prioridad = valor; }
    public int getTickets() { return tickets; }
    public void setTickets(int valor) {
        if (valor < 1) throw new IllegalArgumentException("Debe tener al menos un ticket");
        tickets = valor;
    }

    public int getId() {
        return id;
    }

    public Programa getPrograma() {
        return programa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
