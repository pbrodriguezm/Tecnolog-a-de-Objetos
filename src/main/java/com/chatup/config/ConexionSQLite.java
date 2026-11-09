package com.chatup.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * PATRÓN SINGLETON
 * Garantiza una única conexión a la base de datos SQLite local (chatup.db)
 * compartida por toda la aplicación.
 */
public final class ConexionSQLite {

    private static final String URL = "jdbc:sqlite:chatup.db";
    private static ConexionSQLite instancia;

    private Connection conexion;

    private ConexionSQLite() {
        try {
            conexion = DriverManager.getConnection(URL);
            try (Statement st = conexion.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            crearTablas();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo conectar a SQLite: " + e.getMessage(), e);
        }
    }

    public static synchronized ConexionSQLite getInstancia() {
        if (instancia == null) {
            instancia = new ConexionSQLite();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    private void crearTablas() throws SQLException {
        try (Statement st = conexion.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    username      TEXT NOT NULL UNIQUE,
                    nombre        TEXT NOT NULL,
                    password_hash TEXT NOT NULL,
                    estado        TEXT DEFAULT '¡Hola! Estoy usando ChatUp.'
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS mensajes (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    remitente_id    INTEGER NOT NULL REFERENCES usuarios(id),
                    destinatario_id INTEGER NOT NULL REFERENCES usuarios(id),
                    contenido       TEXT NOT NULL,
                    tipo            TEXT NOT NULL,
                    fecha           TEXT NOT NULL
                )""");
        }
    }

    public synchronized void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException ignored) {
            // cierre silencioso al salir
        }
    }
}
