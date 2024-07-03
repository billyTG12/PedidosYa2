//package com.example.pedidosya2;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.pedidosya2.entidades.Usuario;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class LoginActivity extends AppCompatActivity {
//    Button btnLogin;
//    EditText edUsername, edPassword;
//    TextView noAccount;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        btnLogin = findViewById(R.id.btnLogin);
//        edUsername = findViewById(R.id.et_email);
//        edPassword = findViewById(R.id.et_password);
//        noAccount = findViewById(R.id.tvCreateAccount);
//
//        noAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//            }
//        });
//
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String username = edUsername.getText().toString();
//                String password = edPassword.getText().toString();
//
//                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
//                    Toast.makeText(LoginActivity.this, "Por favor ingrese nombre de usuario y contraseña", Toast.LENGTH_LONG).show();
//                } else {
//                    loginUser(username, password);
//                }
//            }
//        });
//    }
//
//    private void loginUser(String username, String password) {
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setUsername(username);
//        loginRequest.setPassword(password);
//
//        Call<LoginResponse> loginResponseCall = ApiClient.getService().loginUser(loginRequest);
//        loginResponseCall.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                if(response.isSuccessful()){
//                    LoginResponse loginResponse = response.body();
//
//                    // Verifica si el nombre de usuario y la contraseña coinciden con los ingresados por el usuario
//                    if (loginResponse != null && loginResponse.getUsername().equals(username)) {
//                        // Inicio de sesión exitoso
//                        // Puedes iniciar la nueva actividad aquí
//                        startActivity(new Intent(LoginActivity.this, CategoriaActivity.class));
//                        finish();
//                    } else {
//                        // Las credenciales son incorrectas
//                        Toast.makeText(LoginActivity.this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    // Manejar la respuesta de error aquí
//                    // Por ejemplo, mostrar un mensaje de error
//                    String message = "\n" + "Se produjo un error. Vuelva a intentarlo más tarde. ...";
//                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                // Manejar el fallo de la llamada aquí
//                // Por ejemplo, mostrar un mensaje de error con la causa del fallo
//                String message = t.getLocalizedMessage();
//                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}