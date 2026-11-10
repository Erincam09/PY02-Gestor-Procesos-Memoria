package auxiliares;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class FabricaAlgoritmos {
    private static final Map<String, Function<Map<String, Integer>, AlgoritmoPlanificacion>> creadores = new LinkedHashMap<>();
    static {
        // Una línea por algoritmo
        creadores.put("FCFS", parametros -> new FCFS());
        creadores.put("SPN", parametros -> new SPN());
        creadores.put("HRRN", parametros -> new HRRN());
        creadores.put("RR", parametros -> crearRR(parametros));
        creadores.put("SRT", parametros -> new SRT());
    }
    private static RR crearRR(Map<String, Integer> parametros) {
        int quantum = 2;
        if (parametros.containsKey("quantum")) {
            quantum = parametros.get("quantum");
        }
        return new RR(quantum);
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
