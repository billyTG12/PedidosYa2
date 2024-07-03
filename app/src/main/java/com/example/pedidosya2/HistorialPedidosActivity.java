package com.example.pedidosya2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pedidosya2.entidades.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistorialPedidosActivity extends AppCompatActivity {

    private static final String TAG = "HistorialPedidosActivity";

    private ListView listViewHistorialPedidos;
    private PedidoAdapter adapter;
    private ArrayList<Pedido> listaPedidos;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pedidos);

        // Inicializar Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        listViewHistorialPedidos = findViewById(R.id.listViewHistorialPedidos);
        listaPedidos = new ArrayList<>();
        adapter = new PedidoAdapter(this, R.layout.item_pedido, listaPedidos);
        listViewHistorialPedidos.setAdapter(adapter);

        // Cargar los pedidos atendidos
        cargarHistorialPedidos();
    }

    private void cargarHistorialPedidos() {
        DatabaseReference pedidosRef = mDatabase.child("Pedidos");

        pedidosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPedidos.clear(); // Limpiar la lista antes de agregar nuevos pedidos

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pedido pedido = snapshot.getValue(Pedido.class);
                    if (pedido != null && pedido.isAtendido()) { // Filtrar solo los pedidos atendidos
                        listaPedidos.add(pedido);
                    }
                }

                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HistorialPedidosActivity.this, "Error al cargar historial de pedidos desde Firebase", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al cargar historial de pedidos desde Firebase", databaseError.toException());
            }
        });
    }
}