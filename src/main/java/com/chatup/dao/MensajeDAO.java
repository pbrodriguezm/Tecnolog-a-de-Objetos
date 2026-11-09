package com.chatup.dao;

import com.chatup.modelo.Mensaje;
import java.util.List;
import java.util.Optional;

/** PATRÓN DAO: contrato de acceso a datos de mensajes. */
public interface MensajeDAO {

    Mensaje insertar(Mensaje mensaje);

    List<Mensaje> conversacion(int usuarioA, int usuarioB);

    Optional<Mensaje> ultimoEntre(int usuarioA, int usuarioB);

    void eliminarConversacion(int usuarioA, int usuarioB);
}
