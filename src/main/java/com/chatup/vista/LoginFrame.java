package com.chatup.vista;

import com.chatup.controlador.LoginControlador;
import com.chatup.servicio.ChatFacade;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/** PATRÓN MVC: vista de inicio de sesión / registro. */
public class LoginFrame extends JFrame {

    private final LoginControlador controlador;
    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtClave = new JPasswordField(18);

    public LoginFrame(ChatFacade facade) {
        super("ChatUp - Iniciar sesión");
        this.controlador = new LoginControlador(facade, this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        construir();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construir() {
        JPanel cabecera = new JPanel(new GridLayout(2, 1));
        cabecera.setBackground(Estilo.VERDE_OSCURO);
        cabecera.setBorder(BorderFactory.createEmptyBorder(22, 20, 18, 20));
        JLabel titulo = new JLabel("ChatUp", SwingConstants.CENTER);
        titulo.setFont(Estilo.TITULO);
        titulo.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Mensajería local con Java + SQLite", SwingConstants.CENTER);
        sub.setForeground(new Color(0xD9FDD3));
        sub.setFont(Estilo.NORMAL);
        cabecera.add(titulo);
        cabecera.add(sub);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Usuario:"), c);
        c.gridx = 1;
        form.add(txtUsuario, c);
        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        form.add(txtClave, c);

        JButton btnIngresar = Estilo.boton("Ingresar", Estilo.VERDE);
        JButton btnRegistrar = Estilo.boton("Crear cuenta", Estilo.GRIS_TEXTO);
        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.setOpaque(false);
        botones.add(btnRegistrar);
        botones.add(btnIngresar);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        c.insets = new Insets(15, 5, 5, 5);
        form.add(botones, c);

        JLabel ayuda = new JLabel("<html><center>Usuarios demo: <b>ana</b>, <b>luis</b>, <b>maria</b> "
                + "(clave <b>1234</b>)<br>Abre dos sesiones para chatear entre ellas.</center></html>",
                SwingConstants.CENTER);
        ayuda.setFont(Estilo.PEQUENA);
        ayuda.setForeground(Estilo.GRIS_TEXTO);
        c.gridy = 3;
        c.insets = new Insets(12, 5, 5, 5);
        form.add(ayuda, c);

        btnIngresar.addActionListener(e -> ingresar());
        txtClave.addActionListener(e -> ingresar());
        txtUsuario.addActionListener(e -> txtClave.requestFocusInWindow());
        btnRegistrar.addActionListener(e -> registrar());
        getRootPane().setDefaultButton(btnIngresar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cabecera, BorderLayout.NORTH);
        getContentPane().add(form, BorderLayout.CENTER);
    }

    private void ingresar() {
        controlador.ingresar(txtUsuario.getText(), new String(txtClave.getPassword()));
    }

    private void registrar() {
        JTextField user = new JTextField(txtUsuario.getText());
        JTextField nombre = new JTextField();
        JPasswordField clave = new JPasswordField();
        Object[] campos = {"Usuario:", user, "Nombre completo:", nombre, "Contraseña:", clave};
        int r = JOptionPane.showConfirmDialog(this, campos, "Crear cuenta",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            controlador.registrar(user.getText(), nombre.getText(), new String(clave.getPassword()));
        }
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "ChatUp", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "ChatUp", JOptionPane.INFORMATION_MESSAGE);
    }
}
