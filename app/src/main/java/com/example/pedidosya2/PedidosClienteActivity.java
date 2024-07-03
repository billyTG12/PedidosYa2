package com.example.pedidosya2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.entidades.Pedido;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.PedidosAdapter;

public class PedidosClienteActivity extends AppCompatActivity {

    private static final String TAG = "PedidosClienteActivity";
    private RecyclerView recyclerViewPedidos;
    private PedidosAdapter pedidosAdapter;
    private List<Pedido> pedidosList;
    private DatabaseReference mDatabase;
    private String userId;
    private String nameUser, emailUser, usernameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_cliente);

        // Obtener datos del usuario
        SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("name", "");

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");

        // Configurar RecyclerView y adaptador
        recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(this));

        // Agregar separador vertical entre elementos del RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewPedidos.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerViewPedidos.addItemDecoration(dividerItemDecoration);

        pedidosList = new ArrayList<>();
        pedidosAdapter = new PedidosAdapter(pedidosList);
        recyclerViewPedidos.setAdapter(pedidosAdapter);

        // Obtener userId desde SharedPreferences o Intent
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            userId = getSharedPreferences("userPrefs", MODE_PRIVATE).getString("userId", "");
        }

        // Inicializar Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference("Pedidos");

        // Configurar BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Acciones según la opción seleccionada en el BottomNavigationView
                if (itemId == R.id.navigation_categoria) {
                    Intent categoriaIntent = new Intent(PedidosClienteActivity.this, CategoriaActivity.class);
                    startActivity(categoriaIntent);
                    return true;
                } else if (itemId == R.id.navigation_pedidos) {
                    // Redirigir a esta misma actividad con el userId
                    SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                    String userId = sharedPreferences.getString("userID", "");
                    Intent pedidosIntent = new Intent(PedidosClienteActivity.this, PedidosClienteActivity.class);
                    pedidosIntent.putExtra("userId", userId);
                    startActivity(pedidosIntent);
                    return true;
                } else if (itemId == R.id.navigation_perfil) {
                    // Abrir actividad de perfil con datos del usuario
                    Intent perfilIntent = new Intent(PedidosClienteActivity.this, ProfileActivity.class);
                    perfilIntent.putExtra("name", nameUser);
                    perfilIntent.putExtra("email", emailUser);
                    perfilIntent.putExtra("username", usernameUser);
                    startActivity(perfilIntent);
                    return true;
                } else if (itemId == R.id.navigation_fav) {
                    // Acción para favoritos
                    Intent favoritosIntent = new Intent(PedidosClienteActivity.this, FavoritosActivity.class);
                    startActivity(favoritosIntent);
                    return true;
                } else if (itemId == R.id.navigation_car) {
                    // Acción para carrito
                    Intent carritoIntent = new Intent(PedidosClienteActivity.this, CarritoActivity.class);
                    startActivity(carritoIntent);
                    return true;
                }

                return false;
            }
        });

        // Cargar los pedidos no atendidos al iniciar la actividad
        cargarPedidosNoAtendidosEnTiempoReal();

        // Configurar el botón para abrir el historial de pedidos atendidos
        Button btnHistorialPedidos = findViewById(R.id.btnHistorialPedidos);
        btnHistorialPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirHistorialPedidos();
            }
        });
    }

    // Método para cargar los pedidos no atendidos del usuario en tiempo real
    private void cargarPedidosNoAtendidosEnTiempoReal() {
        mDatabase.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                pedidosList.clear();
                for (DataSnapshot pedidoSnapshot : snapshot.getChildren()) {
                    Pedido pedido = pedidoSnapshot.getValue(Pedido.class);
                    if (pedido != null && !pedido.isAtendido()) {
                        pedidosList.add(pedido);
                    }
                }
                pedidosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejar error de Firebase
                Log.e(TAG, "Error al cargar los pedidos no atendidos: " + error.getMessage());
            }
        });
    }

    // Método para abrir la actividad de historial de pedidos atendidos
    private void abrirHistorialPedidos() {
        Intent intent = new Intent(PedidosClienteActivity.this, HistorialPedidosClienteActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}