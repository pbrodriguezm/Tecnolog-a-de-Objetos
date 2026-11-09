package com.chatup.servicio;

import com.chatup.modelo.Mensaje;
import com.chatup.modelo.TipoMensaje;
import com.chatup.observador.ChatObserver;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * PATRÓN OBSERVER (otro observador)
 * "Bot" que escucha los mensajes enviados y, tras una pequeña pausa
 * (simulando que el contacto escribe), responde con una frase aleatoria de la BD.
 * Sólo reacciona a mensajes TEXTO escritos por una persona, así nunca se
 * responde a sí mismo.
 */
public class RespuestaAutomatica implements ChatObserver {

    private final ChatFacade facade;
    private final ScheduledExecutorService planificador = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "respuesta-automatica");
        t.setDaemon(true);
        return t;
    });

    public RespuestaAutomatica(ChatFacade facade) {
        this.facade = facade;
    }

    @Override
    public void onMensajeNuevo(Mensaje m) {
        if (m.getTipo() != TipoMensaje.TEXTO) {
            return;
        }
        long demora = ThreadLocalRandom.current().nextLong(1000, 2500);
        planificador.schedule(
                () -> facade.responderAutomaticamente(m.getDestinatarioId(), m.getRemitenteId()),
                demora, TimeUnit.MILLISECONDS);
    }
}
