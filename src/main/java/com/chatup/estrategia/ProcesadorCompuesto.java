package com.chatup.estrategia;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PATRÓN STRATEGY (combinación de estrategias)
 * Es a su vez un ProcesadorTexto que aplica en cadena las estrategias
 * activas. Así el contexto (chat) sigue recibiendo UNA sola estrategia,
 * aunque el usuario tenga varios botones activados a la vez.
 * para mejroar necestio luego agregar emojis en lugar de simbolos
 */
public class ProcesadorCompuesto implements ProcesadorTexto {

    private final List<ProcesadorTexto> estrategias;

    public ProcesadorCompuesto(List<ProcesadorTexto> estrategias) {
        this.estrategias = List.copyOf(estrategias);
    }

    @Override
    public String procesar(String texto) {
        String r = texto;
        for (ProcesadorTexto e : estrategias) {
            r = e.procesar(r);
        }
        return r;
    }

    @Override
    public String getNombre() {
        return estrategias.stream().map(ProcesadorTexto::getNombre).collect(Collectors.joining(" + "));
    }
}
