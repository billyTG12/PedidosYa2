package com.example.pedidosya2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        editTextUsername = findViewById(R.id.et_username_admin);
        editTextPassword = findViewById(R.id.et_password_admin);

        Button buttonLogin = findViewById(R.id.btn_login_admin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (username.equals("admin") && password.equals("admin123")) {

                    Intent intent = new Intent(AdminActivity.this, ListUsuariosActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AdminActivity.this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}