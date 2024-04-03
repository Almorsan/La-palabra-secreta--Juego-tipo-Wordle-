package com.example.otraprueba;

public class User {
    private String nick;
    private int puntuacion;

    public User(String nick, int puntuacion) {
        this.nick = nick;
        this.puntuacion = puntuacion;
    }
    // Constructor sin argumentos requerido para la deserialización de Firebase
    public User() {
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
