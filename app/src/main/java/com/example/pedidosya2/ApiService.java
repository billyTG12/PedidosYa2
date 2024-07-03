package com.example.pedidosya2;

import com.example.pedidosya2.entidades.Categoria;
import com.example.pedidosya2.entidades.CategoriaRestaurante;
import com.example.pedidosya2.entidades.Producto;
import com.example.pedidosya2.entidades.Restaurante;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("https://raw.githubusercontent.com/Billyt1217/Archivos/main/Categorias.json")
    Call<List<Categoria>> getCategorias();

    @GET("/Billyt1217/Archivos/main/Cevicherias.json")
    Call<List<Restaurante>> getRestaurantes();


    @GET("https://raw.githubusercontent.com/Billyt1217/Archivos/main/Restaurantes.json")
    Call<List<CategoriaRestaurante>> getRestaurantesPorCategoria(@Query("categoria") String nombreCategoria);

    @GET("https://661d250fe7b95ad7fa6c456d.mockapi.io/Producto")
    Call<List<Producto>> getProductos();

}

