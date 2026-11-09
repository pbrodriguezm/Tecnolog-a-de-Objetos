package com.chatup.controlador;

import com.chatup.modelo.Usuario;
import com.chatup.servicio.ChatFacade;
import com.chatup.vista.ChatFrame;
import com.chatup.vista.LoginFrame;

/** PATRÓN MVC: controlador de la pantalla de inicio de sesión. */
public class LoginControlador {

    private final ChatFacade facade;
    private final LoginFrame vista;

    public LoginControlador(ChatFacade facade, LoginFrame vista) {
        this.facade = facade;
        this.vista = vista;
    }

    public void ingresar(String username, String password) {
        try {
            abrirChat(facade.login(username, password));
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void registrar(String username, String nombre, String password) {
        try {
            Usuario nuevo = facade.registrar(username, nombre, password);
            vista.mostrarInfo("Cuenta creada. ¡Bienvenido/a, " + nuevo.getNombre() + "!");
            abrirChat(nuevo);
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void abrirChat(Usuario usuario) {
        ChatFrame chat = new ChatFrame(facade, usuario);
        chat.setLocation(vista.getX(), vista.getY());
        chat.setVisible(true);
        vista.dispose();
    }
}
