package com.example.pedidosya2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.entidades.CarritoCompras;
import com.example.pedidosya2.entidades.Producto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmarPedidoActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmarPedidoActivity";
    private static final int REQUEST_CODE_MAPS_ACTIVITY = 1;

    private TextView ubicacionTextView;
    private TextView metodoPagoTextView;
    private TextView totalTextView;
    private TextView nameTextView;
    private TextView emailTextView;

    private List<Producto> productosEnCarrito;
    private String userId;
    private DatabaseReference mDatabase;
    private AdaptadorProductosCarrito adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_pedido);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ubicacionTextView = findViewById(R.id.ubicacionTextView);
        metodoPagoTextView = findViewById(R.id.metodoPagoTextView);
        totalTextView = findViewById(R.id.totalTextView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        userId = sharedPreferences.getString("userID", "");

        nameTextView.setText("Nombre: " + name);
        emailTextView.setText("Email: " + email);
        Intent intent = getIntent();
        double total = intent.getDoubleExtra("total", 0.0);
        totalTextView.setText("Total: $" + String.format("%.2f", total));

        productosEnCarrito = CarritoCompras.getInstance().getProductos();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductos);
        adaptador = new AdaptadorProductosCarrito(this, productosEnCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        Button btnElegirUbicacion = findViewById(R.id.elegir_ubicacion);
        btnElegirUbicacion.setOnClickListener(v -> lanzarMapsActivity());

        Button btnSeleccionarMetodoPago = findViewById(R.id.seleccionar_metodo_pago);
        btnSeleccionarMetodoPago.setOnClickListener(v -> mostrarDialogoMetodoPago());

        Button btnFinalizarCompra = findViewById(R.id.finalizar_compra);
        btnFinalizarCompra.setOnClickListener(v -> guardarPedidoEnFirebase(name, email));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_categoria) {
                startActivity(new Intent(ConfirmarPedidoActivity.this, CategoriaActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_pedidos) {
                verPedidosCliente();
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                startActivity(new Intent(ConfirmarPedidoActivity.this, ProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_fav) {
                Intent favoritosIntent = new Intent(ConfirmarPedidoActivity.this, FavoritosActivity.class);
                startActivity(favoritosIntent);
                return true;
            } else if (itemId == R.id.navigation_car) {
                Intent carritoIntent = new Intent(ConfirmarPedidoActivity.this, CarritoActivity.class);
                startActivity(carritoIntent);
                return true;
            }

            return false;
        });
    }

    private void lanzarMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MAPS_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAPS_ACTIVITY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String direccion = data.getStringExtra("direccion");
                if (direccion != null && !direccion.isEmpty()) {
                    mostrarUbicacion(direccion);
                }
            }
        }
    }

    private void mostrarUbicacion(String direccion) {
        ubicacionTextView.setText("Ubicación seleccionada: " + direccion);
        ubicacionTextView.setVisibility(View.VISIBLE);
    }

    private void mostrarDialogoMetodoPago() {
        String[] metodosDePago = {"Efectivo", "Yape"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione un método de pago")
                .setItems(metodosDePago, (dialog, which) -> {
                    String metodoPago = metodosDePago[which];
                    metodoPagoTextView.setText("Método de pago seleccionado: " + metodoPago);
                    metodoPagoTextView.setVisibility(View.VISIBLE);
                });
        builder.show();
    }

    private void guardarPedidoEnFirebase(String nombreUsuario, String emailUsuario) {
        String direccion = ubicacionTextView.getText().toString().trim();
        String metodoPago = metodoPagoTextView.getText().toString().trim();
        String totalString = totalTextView.getText().toString().trim();

        String[] parts = totalString.split(" ");
        String valorNumerico = parts[parts.length - 1].replace("$", "").replace(",", ".");

        double total;
        try {
            total = Double.parseDouble(valorNumerico);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error al convertir total a double: " + e.getMessage());
            Toast.makeText(this, "Error al procesar el total del pedido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (direccion.isEmpty() || metodoPago.isEmpty()) {
            Toast.makeText(ConfirmarPedidoActivity.this, "Por favor, complete la ubicación y el método de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        String pedidoId = mDatabase.child("Pedidos").push().getKey();

        if (pedidoId == null) {
            Toast.makeText(this, "Error al generar ID para el pedido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con los datos del pedido
        Map<String, Object> pedidoMap = new HashMap<>();
        pedidoMap.put("id", pedidoId); // Agregar el ID al mapa
        pedidoMap.put("userId", userId); // Guardar el ID del usuario
        pedidoMap.put("direccion", direccion);
        pedidoMap.put("metodoPago", metodoPago);
        pedidoMap.put("total", total);
        pedidoMap.put("atendido", false);
        pedidoMap.put("nombreUsuario", nombreUsuario);
        pedidoMap.put("emailUsuario", emailUsuario);

        // Agregar los productos al mapa del pedido
        Map<String, Object> productosMap = new HashMap<>();
        for (Producto producto : productosEnCarrito) {
            String sanitizedKey = sanitizeKey(producto.getNombre());
            productosMap.put(sanitizedKey, producto.getCantidad());
        }
        pedidoMap.put("platos", productosMap);

        // Guardar el pedido en la base de datos de Firebase
        mDatabase.child("Pedidos").child(pedidoId).setValue(pedidoMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ConfirmarPedidoActivity.this, "Pedido guardado en Firebase", Toast.LENGTH_SHORT).show();

                    // Vaciar el carrito y notificar al adaptador
                    CarritoCompras.getInstance().vaciarCarrito();
                    adaptador.notifyDataSetChanged();

                    // Redirigir a la actividad CategoriaActivity
                    Intent intent = new Intent(ConfirmarPedidoActivity.this, CategoriaActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar el pedido en Firebase: " + e.getMessage());
                    Toast.makeText(ConfirmarPedidoActivity.this, "Error al guardar el pedido en Firebase", Toast.LENGTH_SHORT).show();
                });
    }

    private String sanitizeKey(String key) {
        return key.replace("/", " ")
                .replace(".", " ")
                .replace("#", " ")
                .replace("$", " ")
                .replace("[", " ")
                .replace("]", " ");
    }

    private void verPedidosCliente() {
        // Obtener el ID del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", "");

        // Redirigir a la actividad para mostrar los pedidos del cliente
        Intent intent = new Intent(ConfirmarPedidoActivity.this, PedidosClienteActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}