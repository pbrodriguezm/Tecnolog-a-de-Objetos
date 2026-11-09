package com.chatup.observador;

import com.chatup.modelo.Mensaje;

/** PATRÓN OBSERVER (sujeto observable). */
public interface ChatSubject {

    void suscribir(ChatObserver observador);

    void desuscribir(ChatObserver observador);

    void notificar(Mensaje mensaje);
}
