package com.chatup.vista;

import com.chatup.estrategia.ProcesadorCompuesto;
import com.chatup.estrategia.ProcesadorTexto;
import com.chatup.estrategia.TextoCensurado;
import com.chatup.estrategia.TextoEmoticonos;
import com.chatup.estrategia.TextoMayusculas;
import com.chatup.estrategia.TextoNormal;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Barra tipo editor de texto: cada botón activa/desactiva una estrategia
 * (patrón STRATEGY). getEstrategia() devuelve la estrategia resultante:
 * ninguna activa → TextoNormal, una → esa misma, varias → ProcesadorCompuesto.
 */
public class BarraEstrategias extends JPanel {

    private static final Color BORDE = new Color(0xD1D7DB);

    /** Orden de aplicación = orden de los botones (emoticonos antes que mayúsculas). */
    private final Map<JToggleButton, ProcesadorTexto> botones = new LinkedHashMap<>();
    private final JLabel lblActiva = new JLabel();

    public BarraEstrategias() {
        super(new FlowLayout(FlowLayout.LEFT, 4, 0));
        setOpaque(false);

        agregar("☺", "Emoticonos:  :)  :(  <3  (y)  (*)  (sol)  (nota)", new TextoEmoticonos(), Font.PLAIN);
        agregar("***", "Censurar palabras ofensivas", new TextoCensurado(), Font.BOLD);
        agregar("AA", "Enviar en MAYÚSCULAS", new TextoMayusculas(), Font.BOLD);

        lblActiva.setFont(Estilo.PEQUENA);
        lblActiva.setForeground(Estilo.GRIS_TEXTO);
        lblActiva.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        add(lblActiva);
        actualizarEtiqueta();
    }

    private void agregar(String texto, String tip, ProcesadorTexto estrategia, int estiloFuente) {
        JToggleButton b = new JToggleButton(texto);
        b.setToolTipText(tip + " (Strategy: " + estrategia.getClass().getSimpleName() + ")");
        b.setFont(new Font("SansSerif", estiloFuente, 13));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addItemListener(e -> {
            pintar(b);
            actualizarEtiqueta();
        });
        pintar(b);
        botones.put(b, estrategia);
        add(b);
    }

    private void pintar(JToggleButton b) {
        boolean on = b.isSelected();
        b.setContentAreaFilled(on);
        b.setBackground(on ? Estilo.BURBUJA_MIA : getBackground());
        b.setForeground(on ? Estilo.VERDE_OSCURO : Estilo.GRIS_TEXTO);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(on ? Estilo.VERDE : BORDE, 1, true),
                BorderFactory.createEmptyBorder(3, 9, 3, 9)));
    }

    private void actualizarEtiqueta() {
        lblActiva.setText("Estrategia: " + getEstrategia().getNombre());
    }

    /** Estrategia a aplicar según los botones activos. */
    public ProcesadorTexto getEstrategia() {
        List<ProcesadorTexto> activas = new ArrayList<>();
        botones.forEach((boton, estrategia) -> {
            if (boton.isSelected()) {
                activas.add(estrategia);
            }
        });
        return switch (activas.size()) {
            case 0 -> new TextoNormal();
            case 1 -> activas.get(0);
            default -> new ProcesadorCompuesto(activas);
        };
    }
}
