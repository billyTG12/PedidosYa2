package com.example.pedidosya2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class HistorialPedidosClienteActivity extends AppCompatActivity {

    private static final String TAG = "HistorialPedidosClienteActivity";
    private RecyclerView recyclerViewHistorial;
    private PedidosAdapter historialAdapter;
    private List<Pedido> historialPedidosList;
    private DatabaseReference mDatabase;
    private String nameUser, emailUser, usernameUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pedidos_cliente);


        SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("name", "");


        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");

        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorialPedidos);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));
        // Agregar separador vertical entre elementos del RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewHistorial.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerViewHistorial.addItemDecoration(dividerItemDecoration);
        historialPedidosList = new ArrayList<>();
        historialAdapter = new PedidosAdapter(historialPedidosList);
        recyclerViewHistorial.setAdapter(historialAdapter);

        // Obtener el userId desde el Intent o SharedPreferences
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            userId = getSharedPreferences("userPrefs", MODE_PRIVATE).getString("userId", "");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("Pedidos");

        mDatabase.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historialPedidosList.clear();
                for (DataSnapshot pedidoSnapshot : snapshot.getChildren()) {
                    Pedido pedido = pedidoSnapshot.getValue(Pedido.class);
                    if ((pedido != null) && pedido.isAtendido()) {
                        historialPedidosList.add(pedido);
                    }
                }
                historialAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al consultar los pedidos atendidos: " + error.getMessage());
                Toast.makeText(HistorialPedidosClienteActivity.this, "Error al consultar los pedidos atendidos", Toast.LENGTH_SHORT).show();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_categoria) {
                Intent categoriaIntent = new Intent(HistorialPedidosClienteActivity.this, CategoriaActivity.class);
                categoriaIntent.putExtra("name", nameUser);
                categoriaIntent.putExtra("email", emailUser);
                categoriaIntent.putExtra("username", usernameUser);
                startActivity(categoriaIntent);
                return true;
            } else if (itemId == R.id.navigation_pedidos) {
                Intent pedidosIntent = new Intent(HistorialPedidosClienteActivity.this, PedidosClienteActivity.class);
                pedidosIntent.putExtra("name", nameUser);
                pedidosIntent.putExtra("email", emailUser);
                pedidosIntent.putExtra("username", usernameUser);
                startActivity(pedidosIntent);
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                Intent perfilIntent = new Intent(HistorialPedidosClienteActivity.this, ProfileActivity.class);
                perfilIntent.putExtra("name", nameUser);
                perfilIntent.putExtra("email", emailUser);
                perfilIntent.putExtra("username", usernameUser);
                startActivity(perfilIntent);
                return true;
            } else if (itemId == R.id.navigation_fav) {
                // Acción para favoritos
                Intent favoritosIntent = new Intent(HistorialPedidosClienteActivity.this, FavoritosActivity.class);
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