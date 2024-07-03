package com.example.pedidosya2;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.entidades.CarritoCompras;
import com.example.pedidosya2.entidades.Producto;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdaptadorProductos;

public class CarritoActivity extends AppCompatActivity {

    private String nameUser, emailUser, usernameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Recibir los datos del intent
        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");

        List<Producto> carrito = CarritoCompras.getInstance().getProductos();

        RecyclerView recyclerView = findViewById(R.id.idRVcarrito);
        AdaptadorProductos adaptador = new AdaptadorProductos(this, carrito, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        double total = CarritoCompras.getInstance().obtenerTotal();
        TextView totalTextView = findViewById(R.id.totalTextView);
        totalTextView.setText("Total: $" + String.format("%.2f", total));

        adaptador.notifyDataSetChanged();

        Button btnIrAPagar = findViewById(R.id.btnIrAPagar);
        btnIrAPagar.setOnClickListener(v -> {
            Intent intent1 = new Intent(CarritoActivity.this, ConfirmarPedidoActivity.class);

            ArrayList<String> productos = new ArrayList<>();
            for (Producto producto : carrito) {
                productos.add(producto.getNombre() + " - Cantidad: " + producto.getCantidad());
            }
            intent1.putStringArrayListExtra("productos", productos);
            intent1.putExtra("total", total);

            startActivity(intent1);
        });

        // Configurar BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_categoria) {
                Intent categoriaIntent = new Intent(CarritoActivity.this, CategoriaActivity.class);
                categoriaIntent.putExtra("name", nameUser);
                categoriaIntent.putExtra("email", emailUser);
                categoriaIntent.putExtra("username", usernameUser);
                startActivity(categoriaIntent);
                return true;
            } else if (itemId == R.id.navigation_pedidos) {
                Intent pedidosIntent = new Intent(CarritoActivity.this, PedidosClienteActivity.class);
                pedidosIntent.putExtra("name", nameUser);
                pedidosIntent.putExtra("email", emailUser);
                pedidosIntent.putExtra("username", usernameUser);
                startActivity(pedidosIntent);
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                Intent perfilIntent = new Intent(CarritoActivity.this, ProfileActivity.class);
                perfilIntent.putExtra("name", nameUser);
                perfilIntent.putExtra("email", emailUser);
                perfilIntent.putExtra("username", usernameUser);
                startActivity(perfilIntent);
                return true;
            } else if (itemId == R.id.navigation_fav) {
                // Acción para favoritos
                Intent favoritosIntent = new Intent(CarritoActivity.this, FavoritosActivity.class);
                startActivity(favoritosIntent);
                return true;
            }else if (itemId == R.id.navigation_car) {
                // Acción para favoritos

                return true;
            }

            return false;
        });
    }
}