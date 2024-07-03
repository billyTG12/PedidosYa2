package com.example.pedidosya2;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.pedidosya2.entidades.Pedido;
import com.example.pedidosya2.entidades.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class PedidoAdapter extends ArrayAdapter<Pedido> {

    private static final String TAG = "PedidoAdapter";
    private Context mContext;
    private int mResource;
    private List<Pedido> mPedidos;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mValueEventListener;

    public PedidoAdapter(Context context, int resource, List<Pedido> pedidos) {
        super(context, resource, pedidos);
        mContext = context;
        mResource = resource;
        mPedidos = pedidos != null ? pedidos : new ArrayList<>();

        // Inicializar la referencia a la base de datos
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Pedidos");

        // Escuchar cambios en la base de datos
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPedidos.clear(); // Limpiar la lista actual de pedidos

                // Iterar sobre los datos recibidos y agregar solo los pedidos no atendidos (atendido = false)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pedido pedido = snapshot.getValue(Pedido.class);
                    if (pedido != null && !pedido.isAtendido()) { // Filtrar solo los pedidos no atendidos
                        pedido.setId(snapshot.getKey()); // Establecer el ID del pedido
                        mPedidos.add(pedido);
                    }
                }

                Log.d(TAG, "Pedidos cargados: " + mPedidos.size());

                // Notificar al adaptador que los datos han cambiado
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error al obtener pedidos desde Firebase: " + databaseError.getMessage());
            }
        };

        // Agregar el listener a la referencia de la base de datos
        mDatabaseRef.addValueEventListener(mValueEventListener);
    }

    // Método onDestroy para limpiar el listener
    @Override
    public void clear() {
        super.clear();
        if (mDatabaseRef != null && mValueEventListener != null) {
            mDatabaseRef.removeEventListener(mValueEventListener);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Pedido pedido = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        // Referencias a los TextViews y Button en el layout de item_pedido
        TextView textUbicacion = convertView.findViewById(R.id.textUbicacion);
        TextView textMetodoPago = convertView.findViewById(R.id.textMetodoPago);
        TextView textTotal = convertView.findViewById(R.id.textTotal);
        TextView textNombre = convertView.findViewById(R.id.textNombre);
        TextView textEmail = convertView.findViewById(R.id.textEmail);
        TextView textAtendido = convertView.findViewById(R.id.textAtendido);
        TextView textProductos = convertView.findViewById(R.id.textProductos); // TextView para mostrar productos
        Button btnEntregarPedido = convertView.findViewById(R.id.btnEntregarPedido);

        // Verificar que el pedido no sea nulo
        if (pedido != null) {
            textUbicacion.setText("Ubicación: " + pedido.getDireccion());
            textMetodoPago.setText("Método de pago: " + pedido.getMetodoPago());
            textTotal.setText("Total: $" + pedido.getTotal());
            textNombre.setText("Nombre: " + pedido.getNombreUsuario());
            textEmail.setText("Email: " + pedido.getEmailUsuario());
            textAtendido.setText("Atendido: " + (pedido.isAtendido() ? "Sí" : "No"));

            // Mostrar los productos si existen
            if (pedido.getPlatos() != null && !pedido.getPlatos().isEmpty()) {
                StringBuilder productosText = new StringBuilder("Productos:\n");
                for (Map.Entry<String, Integer> entry : pedido.getPlatos().entrySet()) {
                    productosText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                textProductos.setText(productosText.toString());
            } else {
                textProductos.setText("No hay productos registrados");
            }

            // Ocultar el botón de entrega de pedido en HistorialPedidosActivity
            if (mContext instanceof HistorialPedidosActivity) {
                btnEntregarPedido.setVisibility(View.GONE);
            } else {
                btnEntregarPedido.setVisibility(View.VISIBLE);
                btnEntregarPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Abrir la actividad del mapa al hacer clic en el botón
                        Intent intent = new Intent(mContext, MapaEntregaActivity.class);
                        intent.putExtra("direccion", pedido.getDireccion());
                        intent.putExtra("id", pedido.getId());
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        return convertView;
    }

    // Método updateData() para actualizar la lista de pedidos (como lo tenías antes)
    public void updateData(List<Pedido> newData) {
        mPedidos.clear();  // Limpiar la lista actual
        if (newData != null) {
            mPedidos.addAll(newData);  // Agregar la nueva lista de pedidos
        }
        Log.d(TAG, "Datos actualizados: " + mPedidos.size());
        notifyDataSetChanged();  // Notificar cambios a la vista
    }
}