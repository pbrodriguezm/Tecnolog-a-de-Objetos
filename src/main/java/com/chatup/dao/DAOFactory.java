package com.chatup.dao;

import com.chatup.dao.sqlite.SQLiteDAOFactory;

/**
 * PATRÓN ABSTRACT FACTORY
 * Crea la familia de DAOs sin que el resto del sistema conozca la
 * implementación concreta (SQLite). Cambiar de motor = otra fábrica concreta.
 */
public abstract class DAOFactory {

    public enum Motor { SQLITE }

    public abstract UsuarioDAO getUsuarioDAO();

    public abstract MensajeDAO getMensajeDAO();

    public abstract RespuestaDAO getRespuestaDAO();

    public static DAOFactory getFactory(Motor motor) {
        return switch (motor) {
            case SQLITE -> new SQLiteDAOFactory();
        };
    }
}
