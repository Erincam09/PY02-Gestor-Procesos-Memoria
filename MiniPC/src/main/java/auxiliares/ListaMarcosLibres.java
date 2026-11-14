package auxiliares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListaMarcosLibres {
    private final List<Integer> libres = new ArrayList<>();

    public ListaMarcosLibres(int cantidadMarcos) {
        for (int marco = 0; marco < cantidadMarcos; marco++) {
            libres.add(marco);
        }
    }

    public int cantidad() {
        return libres.size();
    }

    
    public int tomar() {
        return libres.remove(0);
    }

    public void devolver(int marco) {
        libres.add(marco);
        
        Collections.sort(libres);
    }
}
