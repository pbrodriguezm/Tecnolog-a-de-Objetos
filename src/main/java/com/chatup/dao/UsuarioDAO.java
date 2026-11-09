package com.chatup.dao;

import com.chatup.modelo.Usuario;
import java.util.List;
import java.util.Optional;

/** PATRÓN DAO: contrato de acceso a datos de usuarios. */
public interface UsuarioDAO {

    Usuario insertar(Usuario usuario);

    Optional<Usuario> buscarPorUsername(String username);

    List<Usuario> listarTodosExcepto(int idUsuario);

    void actualizarEstado(int idUsuario, String estado);

    int contar();
}
