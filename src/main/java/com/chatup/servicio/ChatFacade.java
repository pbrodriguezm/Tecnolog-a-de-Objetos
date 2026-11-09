package com.chatup.servicio;

import com.chatup.dao.DAOFactory;
import com.chatup.dao.MensajeDAO;
import com.chatup.dao.RespuestaDAO;
import com.chatup.dao.UsuarioDAO;
import com.chatup.estrategia.ProcesadorTexto;
import com.chatup.factory.MensajeFactory;
import com.chatup.modelo.Mensaje;
import com.chatup.modelo.TipoMensaje;
import com.chatup.modelo.Usuario;
import com.chatup.observador.ChatObserver;
import com.chatup.observador.ChatSubject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PATRÓN FACADE
 * Punto de entrada único y simple para la GUI: oculta DAOs, fábricas,
 * estrategias y el hash de contraseñas.
 *
 * También actúa como SUJETO del patrón OBSERVER: al enviar un mensaje
 * notifica a todas las ventanas de chat suscritas.
 */
public class ChatFacade implements ChatSubject {

    private final UsuarioDAO usuarioDAO;
    private final MensajeDAO mensajeDAO;
    private final RespuestaDAO respuestaDAO;
    private final List<ChatObserver> observadores = new CopyOnWriteArrayList<>();

    public ChatFacade(DAOFactory factory) {
        this.usuarioDAO = factory.getUsuarioDAO();
        this.mensajeDAO = factory.getMensajeDAO();
        this.respuestaDAO = factory.getRespuestaDAO();
    }

    // ---------------------------------------------------------------- usuarios

    public Usuario login(String username, String password) {
        Usuario u = usuarioDAO.buscarPorUsername(normalizar(username))
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
        if (!u.getPasswordHash().equals(hash(password))) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        return u;
    }

    public Usuario registrar(String username, String nombre, String password) {
        String user = normalizar(username);
        if (!user.matches("[a-z0-9_]{3,20}")) {
            throw new IllegalArgumentException("Usuario: 3-20 caracteres (letras, números o _)");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Ingrese su nombre");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
        if (usuarioDAO.buscarPorUsername(user).isPresent()) {
            throw new IllegalArgumentException("Ese nombre de usuario ya está en uso");
        }
        return usuarioDAO.insertar(new Usuario(0, user, nombre.trim(), hash(password),
                "¡Hola! Estoy usando ChatUp."));
    }

    public List<Usuario> contactos(Usuario yo) {
        return usuarioDAO.listarTodosExcepto(yo.getId());
    }

    public void cambiarEstado(Usuario yo, String estado) {
        usuarioDAO.actualizarEstado(yo.getId(), estado);
        yo.setEstado(estado);
    }

    // ---------------------------------------------------------------- mensajes

    public List<Mensaje> conversacion(Usuario yo, Usuario otro) {
        return mensajeDAO.conversacion(yo.getId(), otro.getId());
    }

    public Optional<Mensaje> ultimoMensaje(Usuario yo, Usuario otro) {
        return mensajeDAO.ultimoEntre(yo.getId(), otro.getId());
    }

    public Mensaje enviarTexto(Usuario yo, Usuario otro, String texto, ProcesadorTexto estrategia) {
        String procesado = estrategia.procesar(texto);
        return guardarYNotificar(MensajeFactory.crear(TipoMensaje.TEXTO, yo.getId(), otro.getId(), procesado));
    }

    public Mensaje enviarZumbido(Usuario yo, Usuario otro) {
        return guardarYNotificar(MensajeFactory.crear(TipoMensaje.ZUMBIDO, yo.getId(), otro.getId(), null));
    }

    /**
     * Responde en nombre de {@code remitenteId} con una frase aleatoria
     * guardada en la tabla "respuestas" de SQLite.
     */
    public void responderAutomaticamente(int remitenteId, int destinatarioId) {
        respuestaDAO.obtenerAleatoria().ifPresent(texto -> guardarYNotificar(
                MensajeFactory.crear(TipoMensaje.RESPUESTA_AUTO, remitenteId, destinatarioId, texto)));
    }

    public void vaciarChat(Usuario yo, Usuario otro) {
        mensajeDAO.eliminarConversacion(yo.getId(), otro.getId());
        notificar(MensajeFactory.crear(TipoMensaje.SISTEMA, yo.getId(), otro.getId(),
                yo.getNombre() + " vació el chat"));
    }

    private Mensaje guardarYNotificar(Mensaje m) {
        Mensaje guardado = mensajeDAO.insertar(m);
        notificar(guardado);
        return guardado;
    }

    // ---------------------------------------------------------------- datos demo

    public void sembrarDatosIniciales() {
        sembrarRespuestas();
        if (usuarioDAO.contar() > 0) {
            return;
        }
        Usuario ana = registrar("ana", "Ana Torres", "1234");
        Usuario luis = registrar("luis", "Luis Quispe", "1234");
        registrar("maria", "María Flores", "1234");
        mensajeDAO.insertar(MensajeFactory.crear(TipoMensaje.TEXTO, luis.getId(), ana.getId(),
                "Hola Ana, ¿ya avanzaste el proyecto de Tecnología de Objetos?"));
        mensajeDAO.insertar(MensajeFactory.crear(TipoMensaje.TEXTO, ana.getId(), luis.getId(),
                "¡Sí! Ya tengo los patrones de diseño ☺"));
    }

    private void sembrarRespuestas() {
        if (respuestaDAO.contar() > 0) {
            return;
        }
        List.of(
                "Creo que es cierto",
                "De acuerdo",
                "Puedo analizar lo que dices",
                "Mmm, déjame pensarlo",
                "¡Totalmente!",
                "No estoy tan seguro de eso",
                "Jajaja, buenísimo",
                "Interesante, cuéntame más",
                "Ok, lo reviso y te aviso",
                "Me parece bien",
                "¿En serio? No lo sabía",
                "Tienes toda la razón",
                "Hablamos más tarde, estoy ocupado",
                "Claro que sí",
                "Eso suena genial",
                "No lo había visto de esa forma",
                "Dame un momento",
                "Perfecto, gracias",
                "¿Y tú qué opinas?",
                "Lo vemos mañana en clase",
                "Exacto, eso mismo pensaba",
                "Hmm, puede ser",
                "¡Qué buena idea!",
                "Te entiendo perfectamente",
                "Vale, anotado"
        ).forEach(respuestaDAO::insertar);
    }

    // ---------------------------------------------------------------- Observer

    @Override
    public void suscribir(ChatObserver o) {
        observadores.add(o);
    }

    @Override
    public void desuscribir(ChatObserver o) {
        observadores.remove(o);
    }

    @Override
    public void notificar(Mensaje m) {
        for (ChatObserver o : observadores) {
            o.onMensajeNuevo(m);
        }
    }

    // ---------------------------------------------------------------- utilidades

    private static String normalizar(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
