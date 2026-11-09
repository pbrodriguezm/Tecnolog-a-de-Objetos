package com.chatup.estrategia;

public class TextoMayusculas implements ProcesadorTexto {

    @Override
    public String procesar(String texto) {
        return texto.toUpperCase();
    }

    @Override
    public String getNombre() {
        return "MAYÚSCULAS";
    }
}
