package com.chatup;

import com.chatup.config.ConexionSQLite;
import com.chatup.dao.DAOFactory;
import com.chatup.servicio.ChatFacade;
import com.chatup.vista.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * ChatUp - mensajería local de escritorio.
 * Java Swing + SQLite + patrones de diseño.
 */
public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ConexionSQLite.getInstancia().cerrar()));

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // se usa el look & feel por defecto
            }
            ChatFacade facade = new ChatFacade(DAOFactory.getFactory(DAOFactory.Motor.SQLITE));
            facade.sembrarDatosIniciales();
            new LoginFrame(facade).setVisible(true);
        });
    }
}
