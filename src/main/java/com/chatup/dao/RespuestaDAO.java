package com.chatup.dao;

import java.util.Optional;

/** PATRÓN DAO: frases de respuesta automática guardadas en la BD. */
public interface RespuestaDAO {

    void insertar(String texto);

    Optional<String> obtenerAleatoria();

    int contar();
}
