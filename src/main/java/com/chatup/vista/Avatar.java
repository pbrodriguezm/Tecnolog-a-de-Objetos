package com.chatup.vista;

import com.chatup.modelo.Usuario;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/** Círculo de color con la inicial del usuario. */
public class Avatar extends JComponent {

    private Usuario usuario;
    private final int tam;

    public Avatar(Usuario usuario, int tam) {
        this.usuario = usuario;
        this.tam = tam;
        setPreferredSize(new Dimension(tam, tam));
        setMinimumSize(new Dimension(tam, tam));
        setMaximumSize(new Dimension(tam, tam));
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (usuario == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Estilo.colorAvatar(usuario.getId()));
        g2.fillOval(0, 0, tam - 1, tam - 1);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, tam / 2));
        FontMetrics fm = g2.getFontMetrics();
        String ini = usuario.getInicial();
        g2.drawString(ini, (tam - fm.stringWidth(ini)) / 2, (tam - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
