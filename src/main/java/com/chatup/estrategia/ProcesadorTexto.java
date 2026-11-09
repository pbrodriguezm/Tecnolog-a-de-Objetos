package com.chatup.estrategia;

/**
 * PATRÓN STRATEGY
 * Familia de algoritmos intercambiables para transformar el texto antes de
 * enviarlo. El usuario elige la estrategia en tiempo de ejecución desde la GUI.
 */
public interface ProcesadorTexto {

    String procesar(String texto);

    String getNombre();
}
