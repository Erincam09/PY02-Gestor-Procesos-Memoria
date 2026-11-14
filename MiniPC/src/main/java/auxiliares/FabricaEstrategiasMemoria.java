package auxiliares;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class FabricaEstrategiasMemoria {
    private static final Map<String, BiFunction<Map<String, Integer>, List<Integer>, EstrategiaMemoria>> creadores = new LinkedHashMap<>();
    static {
        // Una línea por estrategia
        creadores.put("Paginación", (parametros, tamanios) -> new Paginacion(parametros.getOrDefault("pagina", 8)));
        creadores.put("Partición Fija Igual", (parametros, tamanios) -> new ParticionFijaIgual(parametros.getOrDefault("particiones", 4)));
        creadores.put("Partición Fija Diferente", (parametros, tamanios) -> new ParticionFijaDiferente(tamanios));
        creadores.put("Dinámica First Fit", (parametros, tamanios) -> new DinamicaFirstFit());
        creadores.put("Dinámica Next Fit", (parametros, tamanios) -> new DinamicaNextFit());
        creadores.put("Dinámica Best Fit", (parametros, tamanios) -> new DinamicaBestFit());
    }

    public static EstrategiaMemoria crear(String nombre, Map<String, Integer> parametros, List<Integer> tamanios) {
        if (!creadores.containsKey(nombre)) {
            throw new IllegalArgumentException("Estrategia de memoria desconocida: " + nombre);
        }
        return creadores.get(nombre).apply(parametros, tamanios);
    }

    public static String[] getNombres() {
        return creadores.keySet().toArray(new String[0]);
    }
}
