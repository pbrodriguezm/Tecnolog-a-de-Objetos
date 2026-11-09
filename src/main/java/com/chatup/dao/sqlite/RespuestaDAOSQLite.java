package com.chatup.dao.sqlite;

import com.chatup.config.ConexionSQLite;
import com.chatup.dao.RespuestaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class RespuestaDAOSQLite implements RespuestaDAO {

    private Connection cn() {
        return ConexionSQLite.getInstancia().getConexion();
    }

    @Override
    public void insertar(String texto) {
        try (PreparedStatement ps = cn().prepareStatement("INSERT OR IGNORE INTO respuestas(texto) VALUES (?)")) {
            ps.setString(1, texto);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar respuesta: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<String> obtenerAleatoria() {
        try (Statement st = cn().createStatement();
             ResultSet rs = st.executeQuery("SELECT texto FROM respuestas ORDER BY RANDOM() LIMIT 1")) {
            return rs.next() ? Optional.of(rs.getString(1)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer respuesta: " + e.getMessage(), e);
        }
    }

    @Override
    public int contar() {
        try (Statement st = cn().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM respuestas")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar respuestas: " + e.getMessage(), e);
        }
    }
}
