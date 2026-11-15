package auxiliares;
import modelo.CPU;
import modelo.Proceso;
import modelo.BCP;

public class Despachador {
    private final CPU cpu;
    private final int idNucleo;
    private Proceso procesoActual;

    public Despachador(CPU cpu, int idNucleo) {
        if (cpu == null || idNucleo < 0 || idNucleo >= modelo.ConfiguracionSistema.maxNucleos) {
            throw new IllegalArgumentException("CPU o identificador de núcleo inválido");
        }
        this.cpu = cpu;
        this.idNucleo = idNucleo;
        this.procesoActual = null;
    }

    public void despachar(Proceso proceso) {
        if (proceso == null) {
            throw new IllegalArgumentException("El proceso no puede ser nulo");
        }
        if (hayProcesoActual()) {
            throw new IllegalStateException("El núcleo ya tiene un proceso en ejecución");
        }
        if (proceso.getBcp().getCpu() != -1
                || (proceso.getBcp().getNucleoAsignado() != -1
                && proceso.getBcp().getNucleoAsignado() != idNucleo)) {
            throw new IllegalStateException("El proceso pertenece a otro núcleo o ya está ejecutándose");
        }
        this.procesoActual = proceso;
        BCP bcp = proceso.getBcp();
        cpu.setPC(bcp.getPc());
        cpu.setAC(bcp.getAc());
        cpu.setRegistro("AX", bcp.getAx());
        cpu.setRegistro("AL", bcp.getAl());
        cpu.setAH(bcp.getAh());
        cpu.setRegistro("BX", bcp.getBx()); 
        cpu.setRegistro("CX", bcp.getCx());
        cpu.setDX(bcp.getDx());
        cpu.setIR(bcp.getIr());
        bcp.setEstado("EJECUCION");
        bcp.setCpu(idNucleo);
    }

    public void guardarContexto() {
        if (procesoActual == null) {
            throw new IllegalStateException("No hay proceso en ejecución");
        }
        BCP bcp = procesoActual.getBcp();
        bcp.setPc(cpu.getPC());
        bcp.setAc(cpu.getAC());
        bcp.setAx(cpu.getRegistro("AX"));
        bcp.setAl(cpu.getRegistro("AL"));
        bcp.setAh(cpu.getAH());
        bcp.setBx(cpu.getRegistro("BX"));
        bcp.setCx(cpu.getRegistro("CX"));
        bcp.setDx(cpu.getDX());
        bcp.setIr(cpu.getIR());
    }

    public void retirarProceso(String nuevoEstado) {
        if (procesoActual == null) {
            throw new IllegalStateException("No hay proceso en ejecución");
        }
        guardarContexto();
        BCP bcp = procesoActual.getBcp();
        bcp.setEstado(nuevoEstado);
        bcp.setCpu(-1);
        procesoActual = null;
    }

    public Proceso getProcesoActual() {
        return procesoActual;
    }

    public boolean hayProcesoActual() {
        return procesoActual != null;
    }
}
