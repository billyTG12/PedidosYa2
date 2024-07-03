package com.example.pedidosya2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.entidades.Producto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdaptadorProductos;

public class FavoritosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdaptadorProductos adaptadorProductos;
    private List<Producto> productosFavoritos;
    private String userId;

    private DatabaseReference favoritosDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", "");

        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        productosFavoritos = new ArrayList<>();
        adaptadorProductos = new AdaptadorProductos(this, productosFavoritos, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptadorProductos);

        favoritosDatabaseReference = FirebaseDatabase.getInstance().getReference("favoritos").child(userId);

        obtenerProductosFavoritos();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_categoria) {
                startActivity(new Intent(FavoritosActivity.this, CategoriaActivity.class));
                return true;
            } else if (itemId == R.id.navigation_pedidos) {
                startActivity(new Intent(FavoritosActivity.this, PedidosClienteActivity.class));
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                startActivity(new Intent(FavoritosActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.navigation_fav) {
                // Already in FavoritosActivity
                return true;
            } else if (itemId == R.id.navigation_car) {
                startActivity(new Intent(FavoritosActivity.this, CarritoActivity.class));
                return true;
            }

            return false;
        });
    }

    private void obtenerProductosFavoritos() {
        favoritosDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productosFavoritos.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Producto producto = snapshot.getValue(Producto.class);
                    if (producto != null) {
                        productosFavoritos.add(producto);
                    }
                }
                adaptadorProductos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FavoritosActivity.this, "Error al obtener los productos favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}