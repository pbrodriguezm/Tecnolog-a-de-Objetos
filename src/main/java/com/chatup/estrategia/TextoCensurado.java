package com.chatup.estrategia;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Reemplaza palabras no permitidas por asteriscos. */
public class TextoCensurado implements ProcesadorTexto {

    private static final List<String> PROHIBIDAS = List.of("tonto", "feo", "idiota", "estupido", "estúpido");

    @Override
    public String procesar(String texto) {
        String r = texto;
        for (String p : PROHIBIDAS) {
            r = Pattern.compile("(?iu)\\b" + Pattern.quote(p) + "\\b")
                    .matcher(r)
                    .replaceAll("*".repeat(p.length()));
        }
        return r;
    }

    @Override
    public List<String> detectarProhibidas(String texto) {
        List<String> encontradas = new ArrayList<>();
        for (String p : PROHIBIDAS) {
            if (Pattern.compile("(?iu)\\b" + Pattern.quote(p) + "\\b").matcher(texto).find()) {
                encontradas.add(p);
            }
        }
        return encontradas;
    }

    @Override
    public String getNombre() {
        return "Censura";
    }
}
