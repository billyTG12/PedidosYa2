package com.example.pedidosya2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pedidosya2.entidades.Producto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class DetalleProductoActivity extends AppCompatActivity {

    private DatabaseReference favoritosDatabaseReference;
    private SharedPreferences sharedPreferences;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_producto);

        Producto producto = (Producto) getIntent().getSerializableExtra("producto");

        ImageView imageView = findViewById(R.id.imagenProducto);
        TextView nombreTextView = findViewById(R.id.nombreProducto);
        TextView descripcionTextView = findViewById(R.id.descripcionProducto);
        Button btnAnadirAFavoritos = findViewById(R.id.btnAnadirAFavoritos);

        sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", "");

        Picasso.get().load(producto.getUrlImagen()).into(imageView);
        nombreTextView.setText(producto.getNombre());
        descripcionTextView.setText(producto.getDescripcion());

        favoritosDatabaseReference = FirebaseDatabase.getInstance().getReference("favoritos");

        btnAnadirAFavoritos.setOnClickListener(v -> {
            if (userId.isEmpty()) {
                Toast.makeText(DetalleProductoActivity.this, "Error: No se pudo obtener el ID de usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            // Asignar el ID de usuario al producto antes de guardarlo en Firebase
            producto.setUserId(userId);

            favoritosDatabaseReference.child(userId).child(producto.getId()).setValue(producto)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DetalleProductoActivity.this, "Producto añadido a favoritos", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DetalleProductoActivity.this, "Error al añadir producto a favoritos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}