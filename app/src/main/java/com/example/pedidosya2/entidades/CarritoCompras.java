package com.example.pedidosya2.entidades;

import java.util.ArrayList;
import java.util.List;

public class CarritoCompras {
    private static CarritoCompras instance;
    private List<Producto> productos;

    private CarritoCompras() {
        productos = new ArrayList<>();
    }

    public static CarritoCompras getInstance() {
        if (instance == null) {
            instance = new CarritoCompras();
        }
        return instance;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void agregarProducto(Producto producto) {
        boolean existe = false;
        for (Producto p : productos) {
            if (p.getId().equals(producto.getId())) {
                p.setCantidad(p.getCantidad() + producto.getCantidad());
                existe = true;
                break;
            }
        }
        if (!existe) {
            productos.add(producto);
        }
    }

    public void vaciarCarrito() {
        productos.clear();
    }


    public double obtenerTotal() {
        double total = 0;
        for (Producto producto : productos) {
            total += producto.getPrecio() * producto.getCantidad(); // Multiplica por la cantidad
        }
        return total;
    }
}
