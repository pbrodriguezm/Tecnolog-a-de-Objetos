package com.chatup.vista;

import com.chatup.controlador.ChatControlador;
import com.chatup.modelo.Mensaje;
import com.chatup.modelo.TipoMensaje;
import com.chatup.modelo.Usuario;
import com.chatup.observador.ChatObserver;
import com.chatup.servicio.ChatFacade;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * PATRÓN MVC: vista principal del chat.
 * PATRÓN OBSERVER: implementa ChatObserver para recibir mensajes en vivo.
 */
public class ChatFrame extends JFrame implements ChatObserver {

    private final ChatFacade facade;
    private final Usuario yo;
    private final ChatControlador controlador;

    private final DefaultListModel<Usuario> modeloContactos = new DefaultListModel<>();
    private final JList<Usuario> listaContactos = new JList<>(modeloContactos);
    private final Map<Integer, Integer> noLeidos = new HashMap<>();
    private final Map<Integer, String> vistaPrevia = new HashMap<>();
    /** ids de los mensajes ya dibujados en la conversación abierta (evita duplicados). */
    private final Set<Integer> mostrados = new HashSet<>();

    private final JPanel panelMensajes = new JPanel();
    private final JScrollPane scrollMensajes;
    private final JTextField txtMensaje = new JTextField();
    private final BarraEstrategias barraEstrategias = new BarraEstrategias();

    private final JLabel lblMiEstado = new JLabel();
    private final Avatar avatarContacto = new Avatar(null, 38);
    private final JLabel lblContacto = new JLabel("Selecciona un chat");
    private final JLabel lblEstadoContacto = new JLabel(" ");
    private final JPanel panelEntrada = new JPanel(new BorderLayout(0, 8));
    private final JButton btnVaciar = new JButton("Vaciar");

    public ChatFrame(ChatFacade facade, Usuario yo) {
        super("ChatUp - " + yo.getNombre());
        this.facade = facade;
        this.yo = yo;
        this.controlador = new ChatControlador(facade, this, yo);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Estilo.FONDO_CHAT);
        panelMensajes.setLayout(new BoxLayout(panelMensajes, BoxLayout.Y_AXIS));
        panelMensajes.setOpaque(false);
        contenedor.add(panelMensajes, BorderLayout.NORTH);
        scrollMensajes = new JScrollPane(contenedor);
        scrollMensajes.setBorder(null);
        scrollMensajes.getVerticalScrollBar().setUnitIncrement(16);

        construir();
        cargarContactos();
        actualizarCabecera();
        mostrarConversacion();

        facade.suscribir(this);
        facade.conectar(yo);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                facade.desuscribir(ChatFrame.this);
                facade.desconectar(yo);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                cargarContactos(); // detecta usuarios registrados desde otra sesión
            }
        });
        setSize(900, 620);
        setMinimumSize(new Dimension(720, 480));
    }

    // ------------------------------------------------------------------ armado

    private void construir() {
        setLayout(new BorderLayout());
        add(construirLateral(), BorderLayout.WEST);
        add(construirChat(), BorderLayout.CENTER);
    }

    private JPanel construirLateral() {
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setPreferredSize(new Dimension(290, 0));
        lateral.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0xD1D7DB)));

        JPanel perfil = new JPanel(new BorderLayout(10, 0));
        perfil.setBackground(Estilo.GRIS_CLARO);
        perfil.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        perfil.add(new Avatar(yo, 40), BorderLayout.WEST);
        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        JLabel nombre = new JLabel(yo.getNombre());
        nombre.setFont(Estilo.NEGRITA);
        lblMiEstado.setFont(Estilo.PEQUENA);
        lblMiEstado.setForeground(Estilo.GRIS_TEXTO);
        textos.add(nombre);
        textos.add(lblMiEstado);
        perfil.add(textos, BorderLayout.CENTER);

        JButton btnEstado = botonIcono("✎", "Cambiar mi estado");
        btnEstado.addActionListener(e -> controlador.cambiarEstado(
                JOptionPane.showInputDialog(this, "Nuevo estado:", yo.getEstado())));
        JButton btnSesion = botonIcono("+", "Abrir otra sesión (otro usuario)");
        btnSesion.addActionListener(e -> new LoginFrame(facade).setVisible(true));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        acciones.setOpaque(false);
        acciones.add(btnEstado);
        acciones.add(btnSesion);
        perfil.add(acciones, BorderLayout.EAST);

        listaContactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaContactos.setCellRenderer(new ContactoRenderer());
        listaContactos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Usuario c = listaContactos.getSelectedValue();
                if (c != null) {
                    noLeidos.remove(c.getId());
                }
                mostrarConversacion();
            }
        });

        lateral.add(perfil, BorderLayout.NORTH);
        JScrollPane sc = new JScrollPane(listaContactos);
        sc.setBorder(null);
        lateral.add(sc, BorderLayout.CENTER);
        return lateral;
    }

    private JPanel construirChat() {
        JPanel chat = new JPanel(new BorderLayout());

        JPanel cabecera = new JPanel(new BorderLayout(10, 0));
        cabecera.setBackground(Estilo.GRIS_CLARO);
        cabecera.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        cabecera.add(avatarContacto, BorderLayout.WEST);
        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        lblContacto.setFont(Estilo.NEGRITA);
        lblEstadoContacto.setFont(Estilo.PEQUENA);
        lblEstadoContacto.setForeground(Estilo.GRIS_TEXTO);
        textos.add(lblContacto);
        textos.add(lblEstadoContacto);
        cabecera.add(textos, BorderLayout.CENTER);
        btnVaciar.setToolTipText("Eliminar la conversación");
        btnVaciar.setFocusPainted(false);
        btnVaciar.setContentAreaFilled(false);
        btnVaciar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        btnVaciar.setForeground(Estilo.VERDE_OSCURO);
        btnVaciar.setFont(Estilo.PEQUENA);
        btnVaciar.addActionListener(e -> controlador.vaciar(listaContactos.getSelectedValue()));
        cabecera.add(btnVaciar, BorderLayout.EAST);

        panelEntrada.setBackground(Estilo.GRIS_CLARO);
        panelEntrada.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtMensaje.setFont(Estilo.NORMAL);
        txtMensaje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JButton btnZumbido = Estilo.boton("Zumbido", new Color(0xE67E22));
        btnZumbido.setToolTipText("Hace temblar la ventana del contacto");
        JButton btnEnviar = Estilo.boton("Enviar ➤", Estilo.VERDE);

        txtMensaje.addActionListener(e -> enviar());
        btnEnviar.addActionListener(e -> enviar());
        btnZumbido.addActionListener(e -> controlador.zumbido(listaContactos.getSelectedValue()));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derecha.setOpaque(false);
        derecha.add(btnZumbido);
        derecha.add(btnEnviar);
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        fila.add(txtMensaje, BorderLayout.CENTER);
        fila.add(derecha, BorderLayout.EAST);
        panelEntrada.add(barraEstrategias, BorderLayout.NORTH);
        panelEntrada.add(fila, BorderLayout.CENTER);

        chat.add(cabecera, BorderLayout.NORTH);
        chat.add(scrollMensajes, BorderLayout.CENTER);
        chat.add(panelEntrada, BorderLayout.SOUTH);
        return chat;
    }

    private JButton botonIcono(String texto, String tip) {
        JButton b = new JButton(texto);
        b.setToolTipText(tip);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        b.setForeground(Estilo.VERDE_OSCURO);
        b.setFont(Estilo.NEGRITA);
        return b;
    }

    // ------------------------------------------------------------------ lógica de vista

    private void enviar() {
        controlador.enviar(listaContactos.getSelectedValue(), txtMensaje.getText(),
                barraEstrategias.getEstrategia());
    }

    private void cargarContactos() {
        List<Usuario> contactos = facade.contactos(yo);
        if (contactos.equals(java.util.Collections.list(modeloContactos.elements()))) {
            return; // sin cambios: no se redibuja la conversación
        }
        Usuario seleccionado = listaContactos.getSelectedValue();
        modeloContactos.clear();
        for (Usuario u : contactos) {
            modeloContactos.addElement(u);
            actualizarVistaPrevia(u);
        }
        if (seleccionado != null) {
            listaContactos.setSelectedValue(seleccionado, true);
        }
    }

    private void actualizarVistaPrevia(Usuario contacto) {
        vistaPrevia.put(contacto.getId(), facade.ultimoMensaje(yo, contacto)
                .map(m -> (m.getTipo() == TipoMensaje.RESPUESTA_AUTO ? "Bot: "
                        : m.getRemitenteId() == yo.getId() ? "Tú: " : "") + m.getContenido())
                .orElse(contacto.getEstado()));
    }

    private void mostrarConversacion() {
        Usuario c = listaContactos.getSelectedValue();
        panelMensajes.removeAll();
        mostrados.clear();
        boolean hay = c != null;
        panelEntrada.setVisible(hay);
        btnVaciar.setVisible(hay);
        avatarContacto.setUsuario(c);
        lblContacto.setText(hay ? c.getNombre() : "Selecciona un chat");
        if (hay) {
            mostrarPresencia(c);
        } else {
            lblEstadoContacto.setText("Tus mensajes se guardan en chatup.db (SQLite)");
            lblEstadoContacto.setForeground(Estilo.GRIS_TEXTO);
        }
        if (hay) {
            for (Mensaje m : facade.conversacion(yo, c)) {
                mostrados.add(m.getId());
                panelMensajes.add(burbuja(m, c));
            }
            txtMensaje.requestFocusInWindow();
        }
        panelMensajes.revalidate();
        panelMensajes.repaint();
        bajarScroll();
    }

    /** Crea la burbuja con la firma: "Ana dice:" o "Bot dice:". */
    private Burbuja burbuja(Mensaje m, Usuario contacto) {
        boolean esMio = m.getRemitenteId() == yo.getId();
        if (m.getTipo() == TipoMensaje.RESPUESTA_AUTO) {
            return new Burbuja(m, esMio, "Bot", Estilo.BOT);
        }
        Usuario autor = esMio ? yo : contacto;
        return new Burbuja(m, esMio, primerNombre(autor), Estilo.colorAvatar(autor.getId()));
    }

    private static String primerNombre(Usuario u) {
        return u.getNombre().trim().split("\\s+")[0];
    }

    private void mostrarPresencia(Usuario c) {
        boolean enLinea = facade.estaConectado(c.getId());
        lblEstadoContacto.setText(enLinea ? "● en línea" : "desconectado · responde el bot");
        lblEstadoContacto.setForeground(enLinea ? Estilo.VERDE : Estilo.GRIS_TEXTO);
    }

    @Override
    public void onPresenciaCambiada(int usuarioId, boolean conectado) {
        SwingUtilities.invokeLater(() -> {
            Usuario actual = listaContactos.getSelectedValue();
            if (actual != null && actual.getId() == usuarioId) {
                mostrarPresencia(actual);
            }
            listaContactos.repaint();
        });
    }

    private void bajarScroll() {
        SwingUtilities.invokeLater(() -> {
            var barra = scrollMensajes.getVerticalScrollBar();
            barra.setValue(barra.getMaximum());
        });
    }

    private Usuario buscarContacto(int id) {
        for (int i = 0; i < modeloContactos.size(); i++) {
            if (modeloContactos.get(i).getId() == id) {
                return modeloContactos.get(i);
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ Observer

    @Override
    public void onMensajeNuevo(Mensaje m) {
        if (m.getRemitenteId() != yo.getId() && m.getDestinatarioId() != yo.getId()) {
            return; // no es para esta sesión
        }
        SwingUtilities.invokeLater(() -> procesarMensaje(m));
    }

    private void procesarMensaje(Mensaje m) {
        int otroId = m.getRemitenteId() == yo.getId() ? m.getDestinatarioId() : m.getRemitenteId();
        Usuario otro = buscarContacto(otroId);
        if (otro == null) {
            cargarContactos();
            otro = buscarContacto(otroId);
            if (otro == null) {
                return;
            }
        }
        Usuario actual = listaContactos.getSelectedValue();
        boolean abierto = actual != null && actual.getId() == otroId;

        if (m.getTipo() == TipoMensaje.SISTEMA) {
            if (abierto) {
                mostrarConversacion();
                panelMensajes.add(new Burbuja(m, false, "", Color.GRAY));
                panelMensajes.revalidate();
            }
        } else if (abierto) {
            if (!mostrados.add(m.getId())) {
                return; // ya estaba en pantalla
            }
            panelMensajes.add(burbuja(m, otro));
            panelMensajes.revalidate();
            panelMensajes.repaint();
            bajarScroll();
        } else if (m.getRemitenteId() != yo.getId()) {
            noLeidos.merge(otroId, 1, Integer::sum);
        }

        if (abierto) {
            boolean botVaAResponder = m.getRemitenteId() == yo.getId()
                    && m.getTipo() == TipoMensaje.TEXTO && !facade.estaConectado(otroId);
            if (botVaAResponder) {
                lblEstadoContacto.setText("Bot escribiendo…");
                lblEstadoContacto.setForeground(Estilo.BOT);
            } else {
                mostrarPresencia(otro);
            }
        }

        actualizarVistaPrevia(otro);
        listaContactos.repaint();

        if (m.getTipo() == TipoMensaje.ZUMBIDO && m.getDestinatarioId() == yo.getId()) {
            temblar();
        }
    }
 // esto no funciona por ahroa creo qiue la mac necesita mas permisos arreglarlo pronto
    private void temblar() {
        toFront();
        Point origen = getLocation();
        int[] paso = {0};
        Timer t = new Timer(25, null);
        t.addActionListener(e -> {
            if (paso[0]++ >= 16) {
                setLocation(origen);
                t.stop();
            } else {
                int d = (paso[0] % 2 == 0) ? 10 : -10;
                setLocation(origen.x + d, origen.y + (paso[0] % 3 - 1) * 4);
            }
        });
        t.start();
    }

    // ------------------------------------------------------------------ métodos usados por el controlador

    public void limpiarEntrada() {
        txtMensaje.setText("");
        txtMensaje.requestFocusInWindow();
    }

    public void actualizarCabecera() {
        lblMiEstado.setText(yo.getEstado());
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "ChatUp", JOptionPane.WARNING_MESSAGE);
    }

    public boolean confirmar(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "ChatUp", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ------------------------------------------------------------------ renderer de contactos

    private class ContactoRenderer implements ListCellRenderer<Usuario> {

        private final JPanel celda = new JPanel(new BorderLayout(10, 0));
        private final Avatar avatar = new Avatar(null, 44);
        private final JLabel nombre = new JLabel();
        private final JLabel previa = new JLabel();
        private final JLabel badge = new JLabel();

        ContactoRenderer() {
            celda.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE9EDEF)),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)));
            nombre.setFont(Estilo.NEGRITA);
            previa.setFont(Estilo.PEQUENA);
            previa.setForeground(Estilo.GRIS_TEXTO);
            badge.setFont(Estilo.PEQUENA);
            badge.setForeground(Color.WHITE);
            badge.setBackground(Estilo.VERDE);
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
            JPanel textos = new JPanel(new GridLayout(2, 1));
            textos.setOpaque(false);
            textos.add(nombre);
            textos.add(previa);
            JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
            derecha.setOpaque(false);
            derecha.add(badge);
            celda.add(avatar, BorderLayout.WEST);
            celda.add(textos, BorderLayout.CENTER);
            celda.add(derecha, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Usuario> list, Usuario u,
                                                      int index, boolean sel, boolean foco) {
            avatar.setUsuario(u);
            nombre.setText(facade.estaConectado(u.getId())
                    ? "<html>" + u.getNombre() + " <font color='#00A884'>●</font></html>"
                    : u.getNombre());
            String p = vistaPrevia.getOrDefault(u.getId(), "");
            previa.setText(p.length() > 34 ? p.substring(0, 34) + "…" : p);
            int n = noLeidos.getOrDefault(u.getId(), 0);
            badge.setText(String.valueOf(n));
            badge.setVisible(n > 0);
            celda.setBackground(sel ? Estilo.SELECCION : Color.WHITE);
            return celda;
        }
    }
}
