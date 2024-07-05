package com.example.pedidosya2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class LoginDeliveryActivity extends AppCompatActivity {
    EditText workerUsername, workerPassword;
    Button workerLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_delivery);


        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Si el usuario está logueado, ir a la pantalla principal
            Intent intent = new Intent(LoginDeliveryActivity.this, DeliveryHomeActivity.class);
            startActivity(intent);
            finish();
        }


        workerUsername = findViewById(R.id.login_username_delivery);
        workerPassword = findViewById(R.id.login_password_delivery);
        workerLoginButton = findViewById(R.id.login_button_delivery);

        workerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginDeliveryActivity", "Botón de login presionado");
                if (!validateUsername() || !validatePassword()) {
                    Log.d("LoginDeliveryActivity", "Validación fallida");
                    return;
                } else {
                    Log.d("LoginDeliveryActivity", "Validación exitosa, chequeando trabajador");
                    checkWorker();
                }
            }
        });
    }

    private Boolean validateUsername() {
        String val = workerUsername.getText().toString();
        if (val.isEmpty()) {
            workerUsername.setError("El nombre de usuario no puede estar vacío");
            return false;
        } else {
            workerUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = workerPassword.getText().toString();
        if (val.isEmpty()) {
            workerPassword.setError("El password no puede estar vacío");
            return false;
        } else {
            workerPassword.setError(null);
            return true;
        }
    }

    private void checkWorker() {
        String workerUsernameValue = workerUsername.getText().toString().trim();
        String workerPasswordValue = workerPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("trabajadores");
        Query checkWorkerQuery = reference.orderByChild("username").equalTo(workerUsernameValue);

        checkWorkerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Asegurarse de manejar los diferentes tipos de datos
                        Object passwordObj = snapshot.child("password").getValue();
                        if (passwordObj instanceof String) {
                            String passwordFromDB = (String) passwordObj;
                            if (passwordFromDB.equals(workerPasswordValue)) {

                                SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                Intent intent = new Intent(LoginDeliveryActivity.this, DeliveryHomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                workerPassword.setError("credenciales inválidas");
                                workerPassword.requestFocus();
                            }
                        } else {
                            // Manejar el caso cuando el password no es un String
                            workerPassword.setError("tipo de dato inválido para el password");
                            workerPassword.requestFocus();
                        }
                    }
                } else {
                    // Si no existe el trabajador
                    workerUsername.setError("el trabajador no existe");
                    workerUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error de base de datos
                Toast.makeText(LoginDeliveryActivity.this, "error en la base de datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}