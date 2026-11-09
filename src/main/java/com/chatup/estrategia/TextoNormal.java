package com.chatup.estrategia;

public class TextoNormal implements ProcesadorTexto {

    @Override
    public String procesar(String texto) {
        return texto;
    }

    @Override
    public String getNombre() {
        return "Normal";
    }
}
