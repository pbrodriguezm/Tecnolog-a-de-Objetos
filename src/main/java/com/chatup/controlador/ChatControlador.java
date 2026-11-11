package com.chatup.controlador;

import com.chatup.estrategia.ProcesadorTexto;
import java.util.List;
import com.chatup.modelo.Usuario;
import com.chatup.servicio.ChatFacade;
import com.chatup.vista.ChatFrame;

/** PATRÓN MVC: controlador de la ventana de chat (atiende acciones de la vista). */
public class ChatControlador {

    private final ChatFacade facade;
    private final ChatFrame vista;
    private final Usuario yo;

    public ChatControlador(ChatFacade facade, ChatFrame vista, Usuario yo) {
        this.facade = facade;
        this.vista = vista;
        this.yo = yo;
    }

    public void enviar(Usuario contacto, String texto, ProcesadorTexto estrategia) {
        if (contacto == null) {
            vista.mostrarError("Selecciona un contacto primero");
            return;
        }
        if (texto == null || texto.isBlank()) {
            return;
        }
        
        List<String> prohibidas = estrategia.detectarProhibidas(texto);
        if (!prohibidas.isEmpty()) {
            vista.mostrarError("EsTe. emensaje contiene palabras prohibidas: " + String.join(", ", prohibidas)
                    + "\nSe enviará censurado.");
        }
        
        facade.enviarTexto(yo, contacto, texto, estrategia);
        vista.limpiarEntrada();
    }

    public void zumbido(Usuario contacto) {
        if (contacto != null) {
            facade.enviarZumbido(yo, contacto);
        }
    }

    public void vaciar(Usuario contacto) {
        if (contacto != null && vista.confirmar("¿Eliminar toda la conversación con " + contacto.getNombre() + "?")) {
            facade.vaciarChat(yo, contacto);
        }
    }

    public void cambiarEstado(String estado) {
        if (estado != null && !estado.isBlank()) {
            facade.cambiarEstado(yo, estado.trim());
            vista.actualizarCabecera();
        }
    }
}
