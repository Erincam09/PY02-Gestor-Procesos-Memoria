package auxiliares;
import java.util.List;

public class AsignadorNucleos {
    public Nucleo elegirNucleo(List<Nucleo> nucleos) {
        Nucleo elegido = null;
        for (Nucleo nucleo : nucleos) {
            if (!nucleo.hayEspacio()) continue;
            if (elegido == null || nucleo.cantidadProcesos() < elegido.cantidadProcesos()
                    || (nucleo.cantidadProcesos() == elegido.cantidadProcesos()
                    && nucleo.getId() < elegido.getId())) {
                elegido = nucleo;
            }
        }
        return elegido;
    }
}
