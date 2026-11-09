package com.chatup.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/** Paleta de colores y componentes reutilizables de la interfaz. */
public final class Estilo {

    public static final Color VERDE = new Color(0x00A884);
    public static final Color VERDE_OSCURO = new Color(0x008069);
    public static final Color FONDO_CHAT = new Color(0xEFEAE2);
    public static final Color BURBUJA_MIA = new Color(0xD9FDD3);
    public static final Color BURBUJA_OTRO = Color.WHITE;
    public static final Color GRIS_TEXTO = new Color(0x667781);
    public static final Color GRIS_CLARO = new Color(0xF0F2F5);
    public static final Color SELECCION = new Color(0xE9EDEF);

    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font NEGRITA = new Font("SansSerif", Font.BOLD, 14);
    public static final Font PEQUENA = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 26);

    private static final Color[] AVATARES = {
        new Color(0x6A5ACD), new Color(0xE67E22), new Color(0x2E86C1),
        new Color(0xC0392B), new Color(0x16A085), new Color(0x8E44AD)
    };

    private Estilo() {
    }

    public static Color colorAvatar(int id) {
        return AVATARES[Math.floorMod(id, AVATARES.length)];
    }

    public static JButton boton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setFont(NEGRITA);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
