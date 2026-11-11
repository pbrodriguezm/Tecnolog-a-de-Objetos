package com.chatup.estrategia;

import java.util.List;

/**
 * PATRÓN STRATEGY
 * Familia de algoritmos intercambiables para transformar el texto antes de
 * enviarlo. El usuario elige la estrategia en tiempo de ejecución desde la GUI.
 */
public interface ProcesadorTexto {

    String procesar(String texto);

    String getNombre();

    /** Palabras prohibidas encontradas en el texto (por defecto ninguna). */
    default List<String> detectarProhibidas(String texto) {
        return List.of();
    }
}
