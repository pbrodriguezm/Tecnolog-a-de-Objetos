package com.chatup.estrategia;

import java.util.LinkedHashMap;
import java.util.Map;

/** Convierte emoticonos escritos (":)", "<3"...) en símbolos. */
public class TextoEmoticonos implements ProcesadorTexto {

    private static final Map<String, String> EMOTICONOS = new LinkedHashMap<>();

    static {
        EMOTICONOS.put(":)", "☺");
        EMOTICONOS.put(":(", "☹");
        EMOTICONOS.put("<3", "♥");
        EMOTICONOS.put("(y)", "✔");
        EMOTICONOS.put("(*)", "★");
        EMOTICONOS.put("(sol)", "☀");
        EMOTICONOS.put("(nota)", "♪");
    }

    @Override
    public String procesar(String texto) {
        String r = texto;
        for (Map.Entry<String, String> e : EMOTICONOS.entrySet()) {
            r = r.replace(e.getKey(), e.getValue());
        }
        return r;
    }

    @Override
    public String getNombre() {
        return "Emoticonos ☺";
    }
}
