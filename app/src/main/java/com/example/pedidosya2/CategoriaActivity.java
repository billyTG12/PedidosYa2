package com.example.pedidosya2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.pedidosya2.entidades.Categoria;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdaptadorGridView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaActivity extends AppCompatActivity {
    private GridView mGridView;
    private AdaptadorGridView mAdaptador;
    private List<Categoria> mCategorias;
    private DatabaseReference mDatabase;
    private String nameUser, emailUser, usernameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");

        // Configurar el clic del botón para abrir ProfileActivity
        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(CategoriaActivity.this, ProfileActivity.class);
                profileIntent.putExtra("name", nameUser);
                profileIntent.putExtra("email", emailUser);
                profileIntent.putExtra("username", usernameUser);
                startActivity(profileIntent);
            }
        });

        TextView idTvUser = findViewById(R.id.idTvUser);
        idTvUser.setText(nameUser);

        mGridView = findViewById(R.id.gridView);

        // Inicializar Firebase Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference("categorias");

        cargarDatos();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCategorias != null && position >= 0 && position < mCategorias.size()) {
                    Categoria categoriaSeleccionada = mCategorias.get(position);
                    String nombreCategoria = categoriaSeleccionada.getNombre();

                    Intent intent = new Intent(CategoriaActivity.this, RestaurantesActivity.class);
                    intent.putExtra("nombre_categoria_seleccionada", nombreCategoria);
                    intent.putExtra("name", nameUser);
                    startActivity(intent);
                } else {
                    Log.e("CategoriaActivity", "Error: La lista de categorías es nula o la posición está fuera de los límites.");
                }
            }
        });

        // Configurar BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_categoria) {
                    return true;
                } else if (itemId == R.id.navigation_pedidos) {
                    SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                    String userId = sharedPreferences.getString("userID", "");
                    Intent pedidosIntent = new Intent(CategoriaActivity.this, PedidosClienteActivity.class);
                    pedidosIntent.putExtra("userId", userId); // Asegúrate de obtener el userId correctamente
                    startActivity(pedidosIntent);
                    return true;
                } else if (itemId == R.id.navigation_perfil) {
                    Intent perfilIntent = new Intent(CategoriaActivity.this, ProfileActivity.class);
                    perfilIntent.putExtra("name", nameUser);
                    perfilIntent.putExtra("email", emailUser);
                    perfilIntent.putExtra("username", usernameUser);
                    startActivity(perfilIntent);
                    return true;
                } else if (itemId == R.id.navigation_fav) {
                    // Acción para favoritos
                    Intent favoritosIntent = new Intent(CategoriaActivity.this, FavoritosActivity.class);
                    startActivity(favoritosIntent);
                    return true;
                }else if (itemId == R.id.navigation_car) {
                    // Acción para favoritos
                    Intent favoritosIntent = new Intent(CategoriaActivity.this, CarritoActivity.class);
                    startActivity(favoritosIntent);
                    return true;
                }


                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        moveTaskToBack(true);
    }

    private void cargarDatos() {

        if (disponibilidadDeRed()) {
           cargarDatosDesdeRetrofit();
        } else {
            cargarDatosDesdeFirebase();
        }
    }

    private boolean disponibilidadDeRed() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void almacenarDatosEnFirebase(List<Categoria> categorias) {
        for (Categoria categoria : categorias) {
            mDatabase.child(categoria.getNombre()).setValue(categoria);
        }
        Toast.makeText(this, "Datos almacenados en Firebase", Toast.LENGTH_SHORT).show();
    }

    private void cargarDatosDesdeRetrofit() {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<Categoria>> call = service.getCategorias();
        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful()) {
                    mCategorias = response.body();
                    mAdaptador = new AdaptadorGridView(CategoriaActivity.this, mCategorias);
                    mGridView.setAdapter(mAdaptador);
                    almacenarDatosEnFirebase(mCategorias);  // Almacenar datos en Firebase
                } else {
                    Log.e("CategoriaActivity", "Error: Respuesta no exitosa - Código " + response.code());
                    cargarDatosDesdeFirebase();  // Cargar datos desde Firebase
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e("CategoriaActivity", "Error: " + t.getMessage());
                cargarDatosDesdeFirebase();  // Cargar datos desde Firebase
            }
        });
    }

    private void cargarDatosDesdeFirebase() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Categoria> categorias = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Categoria categoria = snapshot.getValue(Categoria.class);
                        categorias.add(categoria);
                    }
                    mCategorias = categorias;
                    mAdaptador = new AdaptadorGridView(CategoriaActivity.this, mCategorias);
                    mGridView.setAdapter(mAdaptador);
                } else {
                    Log.d("Firebase", "No hay datos almacenados");
                    Toast.makeText(CategoriaActivity.this, "No hay datos almacenados en Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CategoriaActivity", "Error al cargar datos de Firebase: " + databaseError.getMessage());
                Toast.makeText(CategoriaActivity.this, "Error al cargar datos de Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}