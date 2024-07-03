package com.example.pedidosya2.entidades;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pedido {
    private String id;
    private String ubicacion;

    private String metodoPago;
    private String direccion;
    private double total;
    private String nombreUsuario;
    private String emailUsuario;
    private boolean atendido;
    private Map<String, Integer> platos;

    private String userId;

    // Constructor vacío requerido por Firebase
    public Pedido() {
    }
    public Pedido(String id, String direccion, String metodoPago, double total, String nombreUsuario, String emailUsuario, boolean atendido, Map<String, Integer> platos, String userId) {
        this.id = id;
        this.direccion = direccion;
        this.metodoPago = metodoPago;
        this.total = total;
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.atendido = atendido;
        this.platos = platos;
        this.userId = userId;
    }


    public Pedido(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    /*
    public Pedido(String id, String nombreUsuario, String emailUsuario, String direccion,
                  String metodoPago, int total, boolean atendido) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.direccion = direccion;
        this.metodoPago = metodoPago;
        this.total = total;
        this.atendido = atendido;
    }
    public Pedido(String id, String nombreUsuario, String emailUsuario, String direccion, String metodoPago, boolean atendido, double total, String userId) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.direccion = direccion;
        this.metodoPago = metodoPago;
        this.atendido = atendido;
        this.total = total;
        this.userId = userId;
    }
    public Pedido(String ubicacion, String metodoPago, double total, String nombreUsuario, String emailUsuario,boolean atendido ) {
        this.ubicacion = ubicacion;
        this.metodoPago = metodoPago;
        this.total = total;
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.atendido = atendido;
    }

     */
    public Map<String, Integer> getPlatos() {
        return platos;
    }

    public void setPlatos(Map<String, Integer> platos) {
        this.platos = platos;
    }
    public Pedido(String id, String nombreUsuario, String emailUsuario, String direccion,
                  String metodoPago, int total, boolean atendido) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.direccion = direccion;
        this.metodoPago = metodoPago;
        this.total = total;
        this.atendido = atendido;
    }

    public String getPedidoId() {
        return id;
    }

    public void setPedidoId(String pedidoId) {
        this.id = pedidoId;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isAtendido() {
        return atendido;
    }

    public void setAtendido(boolean atendido) {
        this.atendido = atendido;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getters y setters
    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
}