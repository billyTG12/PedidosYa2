package com.example.pedidosya2.entidades;
public class Restaurante {
    private String nombre;
    private String direccion;
    private String calificacion;
    private String imagen_url;

    // Constructor sin argumentos (necesario para Firebase)
    public Restaurante() {
    }

    // Constructor con argumentos
    public Restaurante(String nombre, String direccion, String calificacion, String imagen_url) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.calificacion = calificacion;
        this.imagen_url = imagen_url;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getImagenUrl() {
        return imagen_url;
    }

    public void setImagenUrl(String imagen_url) {
        this.imagen_url = imagen_url;
    }
}