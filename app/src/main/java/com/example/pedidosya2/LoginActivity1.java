package com.example.pedidosya2;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity1 extends AppCompatActivity {
    EditText loginUsername, loginPassword;
    Button loginButton, workerButton;
    TextView signupRedirectText;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si el usuario ya está logueado
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Redirigir directamente a CategoriaActivity
            Intent intent = new Intent(LoginActivity1.this, CategoriaActivity.class);
            startActivity(intent);
            finish(); // Finalizar esta actividad
            return; // Salir del onCreate para no ejecutar el resto del código
        }

        setContentView(R.layout.activity_login1);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        workerButton = findViewById(R.id.workerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validarUsername() || !validarPassword()) {
                    return;
                } else {
                    comprobarUsuario();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity1.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        workerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity1.this, LoginDeliveryActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validarUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("El nombre de usuario no puede estar vacío");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validarPassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("El password no puede estar vacío");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void comprobarUsuario() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserQuery = reference.orderByChild("username").equalTo(userUsername);

        checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String passwordFromDB = ds.child("password").getValue(String.class);

                        // Verificar la contraseña
                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                            // Guardar el userID y otros datos del usuario en SharedPreferences
                            String userID = ds.getKey(); // Obtener el userID
                            String nameFromDB = ds.child("name").getValue(String.class);
                            String emailFromDB = ds.child("email").getValue(String.class);

                            // Guardar datos del usuario en SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userID", userID); // Guardar el userID
                            editor.putString("name", nameFromDB);
                            editor.putString("email", emailFromDB);
                            editor.putString("username", userUsername);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            // Redirigir a la siguiente actividad
                            Intent intent = new Intent(LoginActivity1.this, CategoriaActivity.class);
                            startActivity(intent);
                            finish(); // Finalizar LoginActivity1
                            return;
                        } else {
                            loginPassword.setError("Credenciales incorrectas");
                            loginPassword.requestFocus();
                        }
                    }
                } else {
                    loginUsername.setError("El usuario no existe");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity1.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}