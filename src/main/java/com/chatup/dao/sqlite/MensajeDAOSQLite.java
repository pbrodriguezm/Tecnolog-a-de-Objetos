package com.chatup.dao.sqlite;

import com.chatup.config.ConexionSQLite;
import com.chatup.dao.MensajeDAO;
import com.chatup.modelo.Mensaje;
import com.chatup.modelo.TipoMensaje;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MensajeDAOSQLite implements MensajeDAO {

    private static final String WHERE_CONVERSACION =
            " WHERE (remitente_id = ? AND destinatario_id = ?) OR (remitente_id = ? AND destinatario_id = ?)";

    private Connection cn() {
        return ConexionSQLite.getInstancia().getConexion();
    }

    @Override
    public Mensaje insertar(Mensaje m) {
        String sql = "INSERT INTO mensajes(remitente_id, destinatario_id, contenido, tipo, fecha) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getRemitenteId());
            ps.setInt(2, m.getDestinatarioId());
            ps.setString(3, m.getContenido());
            ps.setString(4, m.getTipo().name());
            ps.setString(5, m.getFecha().toString());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? m.conId(rs.getInt(1)) : m;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar mensaje: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Mensaje> conversacion(int a, int b) {
        String sql = "SELECT * FROM mensajes" + WHERE_CONVERSACION + " ORDER BY id";
        List<Mensaje> lista = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            parametros(ps, a, b);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer conversación: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Optional<Mensaje> ultimoEntre(int a, int b) {
        String sql = "SELECT * FROM mensajes" + WHERE_CONVERSACION + " ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            parametros(ps, a, b);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer último mensaje: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarConversacion(int a, int b) {
        try (PreparedStatement ps = cn().prepareStatement("DELETE FROM mensajes" + WHERE_CONVERSACION)) {
            parametros(ps, a, b);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar conversación: " + e.getMessage(), e);
        }
    }

    private void parametros(PreparedStatement ps, int a, int b) throws SQLException {
        ps.setInt(1, a);
        ps.setInt(2, b);
        ps.setInt(3, b);
        ps.setInt(4, a);
    }

    private Mensaje mapear(ResultSet rs) throws SQLException {
        return Mensaje.builder()
                .id(rs.getInt("id"))
                .remitente(rs.getInt("remitente_id"))
                .destinatario(rs.getInt("destinatario_id"))
                .contenido(rs.getString("contenido"))
                .tipo(TipoMensaje.valueOf(rs.getString("tipo")))
                .fecha(LocalDateTime.parse(rs.getString("fecha")))
                .build();
    }
}
