package com.example.pedidosya2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pedidosya2.entidades.Pedido;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapters.PedidoAdapter;


public class DeliveryHomeActivity extends AppCompatActivity {

    private static final String TAG = "DeliveryHomeActivity";

    private ListView listViewPedidos;
    private PedidoAdapter adapter;
    private ArrayList<Pedido> listaPedidos;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_home);

        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
             Intent intent = new Intent(DeliveryHomeActivity.this, LoginActivity1.class);
            startActivity(intent);
            finish();
            return;
        }

         mDatabase = FirebaseDatabase.getInstance().getReference();

        listViewPedidos = findViewById(R.id.listViewPedidos);
        listaPedidos = new ArrayList<>();
        adapter = new PedidoAdapter(this, R.layout.item_pedido, listaPedidos);
        listViewPedidos.setAdapter(adapter);

         cargarPedidosNoAtendidos();

        Button btnCerrarSesion = findViewById(R.id.logoutButton);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

         Button btnMostrarHistorial = findViewById(R.id.btnMostrarHistorial);
        btnMostrarHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirHistorialPedidos();
            }
        });
    }

    private void cargarPedidosNoAtendidos() {
        DatabaseReference pedidosRef = mDatabase.child("Pedidos");

        pedidosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPedidos.clear(); // Limpiar la lista antes de agregar nuevos pedidos

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pedido pedido = snapshot.getValue(Pedido.class);
                    if (pedido != null && !pedido.isAtendido()) { // Filtrar solo los pedidos no atendidos
                        listaPedidos.add(pedido);
                    }
                }

                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DeliveryHomeActivity.this, "Error al cargar pedidos desde Firebase", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al cargar pedidos desde Firebase", databaseError.toException());
            }
        });
    }

    private void abrirHistorialPedidos() {
        Intent intent = new Intent(DeliveryHomeActivity.this, HistorialPedidosActivity.class);
        startActivity(intent);
    }

    private void cerrarSesion() {
        // Cerrar sesión en Firebase Authentication
       // FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(DeliveryHomeActivity.this, LoginActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}