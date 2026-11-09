package com.chatup.observador;

import com.chatup.modelo.Mensaje;

/**
 * PATRÓN OBSERVER (observador)
 * Cada ventana de chat abierta se suscribe y es notificada cuando se envía
 * un mensaje, sin que el emisor conozca a los receptores.
 */
public interface ChatObserver {

    void onMensajeNuevo(Mensaje mensaje);

    /** Un usuario abrió o cerró su sesión (opcional para los observadores). */
    default void onPresenciaCambiada(int usuarioId, boolean conectado) {
    }
}
