package auxiliares;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Function;

import auxiliares.algoritmosPlanificacion.FCFS;
import auxiliares.algoritmosPlanificacion.GuaranteedScheduling;
import auxiliares.algoritmosPlanificacion.HRRN;
import auxiliares.algoritmosPlanificacion.Lottery;
import auxiliares.algoritmosPlanificacion.MultilevelFQueue;
import auxiliares.algoritmosPlanificacion.PorPrioridad;
import auxiliares.algoritmosPlanificacion.RR;
import auxiliares.algoritmosPlanificacion.SPN;
import auxiliares.algoritmosPlanificacion.SRT;
import auxiliares.algoritmosPlanificacion.SelfishRR;

public class FabricaAlgoritmos {
    private static final Map<String, Function<Map<String, Integer>, AlgoritmoPlanificacion>> creadores = new LinkedHashMap<>();
    static {
        // Una línea por algoritmo
        creadores.put("FCFS", parametros -> new FCFS());
        creadores.put("SPN", parametros -> new SPN());
        creadores.put("HRRN", parametros -> new HRRN());
        creadores.put("RR", parametros ->  new RR(parametros.getOrDefault("quantum", 2)));
        creadores.put("SRT", parametros -> new SRT());
        creadores.put("Por Prioridad", parametros -> {boolean ap = parametros.getOrDefault("apropiativo", 0) == 1; return new PorPrioridad(ap); });
        creadores.put("SelfishRR", parametros -> {
            int quantum = parametros.getOrDefault("quantum", 2);
            int a = parametros.getOrDefault("a", 2);
            int b = parametros.getOrDefault("b", 1);
            return new SelfishRR(quantum, a, b);
        });
        creadores.put("Lottery", parametros -> {
            int quantum = parametros.getOrDefault("quantum", 2);
            return new Lottery(quantum);
        });
        creadores.put("Guaranteed Scheduling", parametros -> {
            int quantum = parametros.getOrDefault("quantum", 2);
            return new GuaranteedScheduling(quantum);
        });
        creadores.put("Multilevel Feedback Queue", parametros -> {
            int quantum = parametros.getOrDefault("quantum", 2);
            return new MultilevelFQueue(quantum);
        });

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
