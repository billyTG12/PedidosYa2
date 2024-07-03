package com.example.pedidosya2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.ApiService;
import com.example.pedidosya2.CarritoActivity;
import com.example.pedidosya2.R;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductosActivity extends AppCompatActivity {

    private AdaptadorProductos adaptadorProductos;
    private DatabaseReference mDatabase;
    private String nameUser, emailUser, usernameUser;
    private String restauranteNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        restauranteNombre = getIntent().getStringExtra("restaurante_nombre");

        TextView restauranteNombreTextView = findViewById(R.id.textView4);
        restauranteNombreTextView.setText(restauranteNombre);

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");

        RecyclerView recyclerView = findViewById(R.id.idRvProductos);
        adaptadorProductos = new AdaptadorProductos(this, new ArrayList<>(), true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptadorProductos);

        Button btnVerCarrito = findViewById(R.id.btnVerCarrito);
        btnVerCarrito.setOnClickListener(v -> {
            Intent intent1 = new Intent(ProductosActivity.this, CarritoActivity.class);
            startActivity(intent1);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_categoria) {
                Intent categoriaIntent = new Intent(ProductosActivity.this, CategoriaActivity.class);
                startActivity(categoriaIntent);
                return true;
            } else if (itemId == R.id.navigation_pedidos) {
                SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userID", "");
                Intent pedidosIntent = new Intent(ProductosActivity.this, PedidosClienteActivity.class);
                pedidosIntent.putExtra("userId", userId); // Asegúrate de obtener el userId correctamente
                startActivity(pedidosIntent);
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                Intent perfilIntent = new Intent(ProductosActivity.this, ProfileActivity.class);
                perfilIntent.putExtra("name", nameUser);
                perfilIntent.putExtra("email", emailUser);
                perfilIntent.putExtra("username", usernameUser);
                startActivity(perfilIntent);
                return true;
            } else if (itemId == R.id.navigation_fav) {
                // Acción para favoritos
                Intent favoritosIntent = new Intent(ProductosActivity.this, FavoritosActivity.class);
                startActivity(favoritosIntent);
                return true;
            }else if (itemId == R.id.navigation_car) {
                // Acción para favoritos
                Intent favoritosIntent = new Intent(ProductosActivity.this, CarritoActivity.class);
                startActivity(favoritosIntent);
                return true;
            }


            return false;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("productos");

        // Cargar productos al iniciar la actividad
        cargarProductos();
    }

    private void cargarProductos() {
        if (isNetworkAvailable()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://661d250fe7b95ad7fa6c456d.mockapi.io/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);
            Call<List<Producto>> call = apiService.getProductos();
            call.enqueue(new Callback<List<Producto>>() {
                @Override
                public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Producto> productosFiltrados = new ArrayList<>();
                        for (Producto producto : response.body()) {
                            if (producto.getRestaurante().equals(restauranteNombre)) {
                                productosFiltrados.add(producto);
                                mDatabase.child(producto.getId()).setValue(producto);
                            }
                        }
                        adaptadorProductos.actualizarProductos(productosFiltrados);
                    } else {
                        Toast.makeText(ProductosActivity.this, "Error al obtener los productos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Producto>> call, Throwable t) {
                    Toast.makeText(ProductosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Producto> productos = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Producto producto = snapshot.getValue(Producto.class);
                        if (producto.getRestaurante().equals(restauranteNombre)) {
                            productos.add(producto);
                        }
                    }
                    adaptadorProductos.actualizarProductos(productos);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ProductosActivity.this, "Error al obtener los productos de Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}