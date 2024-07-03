package com.example.pedidosya2.entidades;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRestaurante {
    private String categoria;
    private List<Restaurante> restaurantes;

    public CategoriaRestaurante() {
        // Constructor vacío requerido para Firebase
    }

    public CategoriaRestaurante(String categoria) {
        this.categoria = categoria;
        this.restaurantes = new ArrayList<>();
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<Restaurante> getRestaurantes() {
        return restaurantes;
    }

    public void setRestaurantes(List<Restaurante> restaurantes) {
        this.restaurantes = restaurantes;
    }
}