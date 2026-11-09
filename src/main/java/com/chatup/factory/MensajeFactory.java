package com.chatup.factory;

import com.chatup.modelo.Mensaje;
import com.chatup.modelo.TipoMensaje;

/**
 * PATRÓN FACTORY (Factory Method)
 * Centraliza la creación de los distintos tipos de mensaje. El cliente sólo
 * indica el tipo y la fábrica decide cómo construirlo.
 */
public final class MensajeFactory {

    private MensajeFactory() {
    }

    public static Mensaje crear(TipoMensaje tipo, int remitenteId, int destinatarioId, String texto) {
        return switch (tipo) {
            case TEXTO -> Mensaje.builder()
                    .tipo(TipoMensaje.TEXTO)
                    .remitente(remitenteId).destinatario(destinatarioId)
                    .contenido(texto.trim())
                    .build();
            case RESPUESTA_AUTO -> Mensaje.builder()
                    .tipo(TipoMensaje.RESPUESTA_AUTO)
                    .remitente(remitenteId).destinatario(destinatarioId)
                    .contenido(texto)
                    .build();
            case ZUMBIDO -> Mensaje.builder()
                    .tipo(TipoMensaje.ZUMBIDO)
                    .remitente(remitenteId).destinatario(destinatarioId)
                    .contenido("¡ZUMBIDO!")
                    .build();
            case SISTEMA -> Mensaje.builder()
                    .tipo(TipoMensaje.SISTEMA)
                    .remitente(remitenteId).destinatario(destinatarioId)
                    .contenido("» " + texto)
                    .build();
        };
    }
}
