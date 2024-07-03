package com.example.pedidosya2.entidades;

import java.io.Serializable;

public class Categoria  implements Serializable {
    private String nombre;
    private String detalle;
    private String imagen;
    public Categoria(String nombre, String detalle, String imagen) {
        this.nombre = nombre;
        this.detalle = detalle;
        this.imagen = imagen;
    }

    public Categoria() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}