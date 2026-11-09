package com.chatup.vista;

import com.chatup.modelo.Mensaje;
import com.chatup.modelo.TipoMensaje;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Fila del chat con la burbuja de un mensaje (alineada según quién lo envió). */
public class Burbuja extends JPanel {

    public Burbuja(Mensaje m, boolean esMio, String firma, Color colorFirma) {
        super(new FlowLayout(esMio ? FlowLayout.RIGHT : FlowLayout.LEFT, 12, 3));
        setOpaque(false);

        if (m.getTipo() == TipoMensaje.SISTEMA) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 6));
            add(new Globo(new Color(0xFFF3C4), etiqueta(m.getContenido(), Estilo.PEQUENA, Estilo.GRIS_TEXTO), null));
            return;
        }

        JLabel texto;
        if (m.getTipo() == TipoMensaje.ZUMBIDO) {
            texto = etiqueta("〰 " + m.getContenido() + " 〰", Estilo.NEGRITA, new Color(0xC0392B));
        } else {
            texto = etiqueta(m.getContenido(), Estilo.NORMAL, Color.BLACK);
        }
        JLabel hora = new JLabel(m.getHora() + (esMio ? "  ✓✓" : ""));
        hora.setFont(Estilo.PEQUENA);
        hora.setForeground(esMio ? new Color(0x53BDEB) : Estilo.GRIS_TEXTO);
        hora.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblFirma = new JLabel(firma + " dice:");
        lblFirma.setFont(Estilo.PEQUENA.deriveFont(java.awt.Font.BOLD));
        lblFirma.setForeground(colorFirma);

        Globo globo = new Globo(esMio ? Estilo.BURBUJA_MIA : Estilo.BURBUJA_OTRO, texto, hora);
        globo.add(lblFirma, BorderLayout.NORTH);
        add(globo);
    }

    private static JLabel etiqueta(String texto, java.awt.Font fuente, Color color) {
        String html = escapar(texto).replace("\n", "<br>");
        String ancho = texto.length() > 40 ? " style='width:260px'" : "";
        JLabel l = new JLabel("<html><div" + ancho + ">" + html + "</div></html>");
        l.setFont(fuente);
        l.setForeground(color);
        return l;
    }

    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Panel con fondo redondeado. */
    private static class Globo extends JPanel {
        private final Color fondo;

        Globo(Color fondo, JLabel contenido, JLabel pie) {
            super(new BorderLayout(0, 2));
            this.fondo = fondo;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(6, 10, 5, 10));
            add(contenido, BorderLayout.CENTER);
            if (pie != null) {
                add(pie, BorderLayout.SOUTH);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(0, 1, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 2, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
