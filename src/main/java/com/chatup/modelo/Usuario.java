package com.chatup.modelo;

import java.util.Objects;

public class Usuario {

    private int id;
    private String username;
    private String nombre;
    private String passwordHash;
    private String estado;

    public Usuario() {
    }

    public Usuario(int id, String username, String nombre, String passwordHash, String estado) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getInicial() {
        return nombre == null || nombre.isBlank() ? "?" : nombre.substring(0, 1).toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Usuario u && u.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
