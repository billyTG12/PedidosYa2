package com.example.pedidosya2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.entidades.CategoriaRestaurante;
import com.example.pedidosya2.entidades.Restaurante;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Adapters.AdaptadorRestaurantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantesActivity extends AppCompatActivity implements AdaptadorRestaurantes.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private AdaptadorRestaurantes mAdaptador;
    private List<Restaurante> mRestaurantesList;
    private DatabaseReference mDatabase;
    private String nombreCategoria;
    private String nameUser, emailUser, usernameUser;
    private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes);

        SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("name", "");


        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");

        nombreCategoria = getIntent().getStringExtra("nombre_categoria_seleccionada");

        TextView textView = findViewById(R.id.idejemplo);
        textView.setText("Restaurantes de " + nombreCategoria);

        //mostrando al ususario
        usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText("Hola, " + username);


        // Configurar el TextView para mostrar el nombre de usuario
        usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(username);


        mRecyclerView = findViewById(R.id.idRvRestaurantes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference("restaurantes").child(nombreCategoria);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_categoria) {
                    Intent categoriaIntent = new Intent(RestaurantesActivity.this, CategoriaActivity.class);
                    startActivity(categoriaIntent);
                    return true;
                } else if (itemId == R.id.navigation_pedidos) {
                    SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                    String userId = sharedPreferences.getString("userID", "");
                    Intent pedidosIntent = new Intent(RestaurantesActivity.this, PedidosClienteActivity.class);
                    pedidosIntent.putExtra("userId", userId); // Asegúrate de obtener el userId correctamente
                    startActivity(pedidosIntent);
                    return true;
                } else if (itemId == R.id.navigation_perfil) {
                    Intent perfilIntent = new Intent(RestaurantesActivity.this, ProfileActivity.class);
                    perfilIntent.putExtra("name", nameUser);
                    perfilIntent.putExtra("email", emailUser);
                    perfilIntent.putExtra("username", usernameUser);
                    startActivity(perfilIntent);
                    return true;
                } else if (itemId == R.id.navigation_fav) {
                    // Acción para favoritos
                    Intent favoritosIntent = new Intent(RestaurantesActivity.this, FavoritosActivity.class);
                    startActivity(favoritosIntent);
                    return true;
                }else if (itemId == R.id.navigation_car) {
                    // Acción para favoritos
                    Intent favoritosIntent = new Intent(RestaurantesActivity.this, CarritoActivity.class);
                    startActivity(favoritosIntent);
                    return true;
                }

                return false;
            }
        });

        cargarDatos(nombreCategoria);

    }

    private void cargarDatos(String nombreCategoria) {
        if (isNetworkAvailable()) {
            loadDataFromRetrofit(nombreCategoria);
        } else {
            loadDataFromFirebase(nombreCategoria);
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

    private void storeDataInFirebase(List<Restaurante> restaurantes) {
        for (Restaurante restaurante : restaurantes) {
            mDatabase.child(restaurante.getNombre()).setValue(restaurante);
        }
    }

    private void loadDataFromRetrofit(String nombreCategoria) {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<CategoriaRestaurante>> call = service.getRestaurantesPorCategoria(nombreCategoria);
        call.enqueue(new Callback<List<CategoriaRestaurante>>() {
            @Override
            public void onResponse(Call<List<CategoriaRestaurante>> call, Response<List<CategoriaRestaurante>> response) {
                if (response.isSuccessful()) {
                    Log.i("MAIN_APP", new Gson().toJson(response.body()));
                    mRestaurantesList = response.body().stream()
                            .filter(x -> x.getCategoria().equals(nombreCategoria))
                            .flatMap(x -> x.getRestaurantes().stream())
                            .collect(Collectors.toList());

                    mAdaptador = new AdaptadorRestaurantes(RestaurantesActivity.this, mRestaurantesList);
                    mAdaptador.setOnItemClickListener(RestaurantesActivity.this);
                    mRecyclerView.setAdapter(mAdaptador);

                    storeDataInFirebase(mRestaurantesList); // Almacenar datos en Firebase
                } else {
                    Log.e("MAIN_APP", "Error: Respuesta no exitosa - Código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CategoriaRestaurante>> call, Throwable t) {
                Log.e("MAIN_APP", "Error: " + t.getMessage());
                loadDataFromFirebase(nombreCategoria); // Cargar datos desde Firebase en caso de falla
            }
        });
    }

    private void loadDataFromFirebase(String nombreCategoria) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Restaurante> restaurantes = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Restaurante restaurante = snapshot.getValue(Restaurante.class);
                        restaurantes.add(restaurante);
                    }
                    mRestaurantesList = restaurantes;
                    mAdaptador = new AdaptadorRestaurantes(RestaurantesActivity.this, mRestaurantesList);
                    mAdaptador.setOnItemClickListener(RestaurantesActivity.this);
                    mRecyclerView.setAdapter(mAdaptador);
                } else {
                    Log.d("Firebase", "No hay datos almacenados");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RestaurantesActivity", "Error al cargar datos de Firebase: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position, String restauranteNombre) {
        Intent intent = new Intent(this, ProductosActivity.class);
        intent.putExtra("restaurante_nombre", restauranteNombre);
        startActivity(intent);
    }
}
