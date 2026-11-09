package auxiliares;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class FabricaAlgoritmos {
    private static final Map<String, Function<Map<String, Integer>, AlgoritmoPlanificacion>> creadores = new LinkedHashMap<>();
    static {
        // Una línea por algoritmo. Cada llamada crea una instancia para un núcleo.
        creadores.put("FCFS", parametros -> new FCFS());
    }
    public static AlgoritmoPlanificacion crear(String nombre, Map<String, Integer> parametros) {
        if (!creadores.containsKey(nombre)) {
            throw new IllegalArgumentException("Algoritmo desconocido: " + nombre);
        }
        return creadores.get(nombre).apply(parametros);
    }
    public static String[] getNombres() {
        return creadores.keySet().toArray(new String[0]);
    }
}
