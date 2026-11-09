package com.chatup.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PATRÓN BUILDER
 * Los mensajes se construyen paso a paso con Mensaje.builder()...build(),
 * validando los campos obligatorios en un único punto.
 */
public class Mensaje {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final int id;
    private final int remitenteId;
    private final int destinatarioId;
    private final String contenido;
    private final TipoMensaje tipo;
    private final LocalDateTime fecha;

    private Mensaje(Builder b) {
        this.id = b.id;
        this.remitenteId = b.remitenteId;
        this.destinatarioId = b.destinatarioId;
        this.contenido = b.contenido;
        this.tipo = b.tipo;
        this.fecha = b.fecha;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Copia este mensaje con otro id (útil tras insertarlo en la BD). */
    public Mensaje conId(int nuevoId) {
        return builder().id(nuevoId).remitente(remitenteId).destinatario(destinatarioId)
                .contenido(contenido).tipo(tipo).fecha(fecha).build();
    }

    public int getId() { return id; }
    public int getRemitenteId() { return remitenteId; }
    public int getDestinatarioId() { return destinatarioId; }
    public String getContenido() { return contenido; }
    public TipoMensaje getTipo() { return tipo; }
    public LocalDateTime getFecha() { return fecha; }

    public String getHora() {
        return fecha.format(HORA);
    }

    /** ¿Este mensaje pertenece a la conversación entre a y b? */
    public boolean esEntre(int a, int b) {
        return (remitenteId == a && destinatarioId == b) || (remitenteId == b && destinatarioId == a);
    }

    public static class Builder {
        private int id;
        private int remitenteId = -1;
        private int destinatarioId = -1;
        private String contenido;
        private TipoMensaje tipo = TipoMensaje.TEXTO;
        private LocalDateTime fecha = LocalDateTime.now();

        public Builder id(int id) { this.id = id; return this; }
        public Builder remitente(int id) { this.remitenteId = id; return this; }
        public Builder destinatario(int id) { this.destinatarioId = id; return this; }
        public Builder contenido(String c) { this.contenido = c; return this; }
        public Builder tipo(TipoMensaje t) { this.tipo = t; return this; }
        public Builder fecha(LocalDateTime f) { this.fecha = f; return this; }

        public Mensaje build() {
            if (remitenteId < 0 || destinatarioId < 0) {
                throw new IllegalStateException("El mensaje necesita remitente y destinatario");
            }
            if (contenido == null || contenido.isBlank()) {
                throw new IllegalStateException("El mensaje no puede estar vacío");
            }
            return new Mensaje(this);
        }
    }
}
