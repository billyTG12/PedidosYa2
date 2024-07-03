package com.example.pedidosya2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView profileName, profileEmail, profileUsername, profilePassword;
    TextView titleName, titleUsername;
    Button editProfile, logoutButton, verPedidosButton;
    CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);
        logoutButton = findViewById(R.id.logoutButton);
        //verPedidosButton = findViewById(R.id.verPedidosButton);
        profileImage = findViewById(R.id.profileImage); // Añadir ImageView para la imagen de perfil

        showAllUserData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

//        //verPedidosButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verPedidosCliente();
//            }
//        });

        // Configurar BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_categoria) {
                    startActivity(new Intent(ProfileActivity.this, CategoriaActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_pedidos) {
                    verPedidosCliente();
                    return true;
                } else if (itemId == R.id.navigation_perfil) {
                    // Ya estamos en el perfil, no hacer nada o implementar otra acción
                    return true;
                } else if (itemId == R.id.navigation_fav) {
                    // Acción para favoritos
                    Intent favoritosIntent = new Intent(ProfileActivity.this, FavoritosActivity.class);
                    startActivity(favoritosIntent);
                    return true;
                } else if (itemId == R.id.navigation_car) {
                    // Acción para carrito de compras
                    Intent carritoIntent = new Intent(ProfileActivity.this, CarritoActivity.class);
                    startActivity(carritoIntent);
                    return true;
                }

                return false;
            }
        });
    }

    public void showAllUserData() {
        // Obtener el nombre de usuario del SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userUsername = sharedPreferences.getString("username", "");

        // Obtener una referencia a la base de datos de Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query query = reference.orderByChild("username").equalTo(userUsername);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtener los datos del usuario
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String nameFromDB = userSnapshot.child("name").getValue(String.class);
                        String emailFromDB = userSnapshot.child("email").getValue(String.class);
                        String usernameFromDB = userSnapshot.child("username").getValue(String.class);
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        String profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String.class); // Obtener la URL de la imagen

                        // Mostrar los datos en los TextViews
                        titleName.setText(nameFromDB);
                        titleUsername.setText(usernameFromDB);
                        profileName.setText(nameFromDB);
                        profileEmail.setText(emailFromDB);
                        profileUsername.setText(usernameFromDB);
                        profilePassword.setText(passwordFromDB);

                        // Cargar la imagen usando Picasso
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Picasso.get().load(profileImageUrl).placeholder(R.drawable.ic_profile_placeholder).into(profileImage);
                        } else {
                            // Si no hay URL de imagen, cargar una imagen por defecto
                            Picasso.get().load(R.drawable.ic_profile_placeholder).into(profileImage);
                        }
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void passUserData() {
        String userUsername = profileUsername.getText().toString().trim();  // Obtener el nombre de usuario del TextView
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);  // Buscar por el campo 'username'
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtener los datos del usuario de Firebase
                    String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    // Lanzar la actividad EditProfileActivity y pasar los datos del usuario
                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("username", usernameFromDB);
                    intent.putExtra("password", passwordFromDB);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cerrarSesion() {
        // Cerrar sesión en Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Limpiar datos de SharedPreferences (si es necesario)
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        // Redirigir a la pantalla de inicio de sesión
        Intent intent = new Intent(ProfileActivity.this, LoginActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finalizar esta actividad
    }

    private void verPedidosCliente() {
        // Obtener el ID del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", "");

        // Redirigir a la actividad para mostrar los pedidos del cliente
        Intent intent = new Intent(ProfileActivity.this, PedidosClienteActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}