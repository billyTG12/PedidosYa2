package com.example.pedidosya2;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected String nameUser;
    protected String emailUser;
    protected String usernameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_categoria) {
                    // Navegar a CategoriaActivity
                    Intent categoriaIntent = new Intent(BaseActivity.this, CategoriaActivity.class);
                    startActivity(categoriaIntent);
                    return true;
                } else if (itemId == R.id.navigation_pedidos) {
                    // Navegar a PedidosClienteActivity con los datos
                    Intent pedidosIntent = new Intent(BaseActivity.this, PedidosClienteActivity.class);
                    pedidosIntent.putExtra("name", nameUser);
                    pedidosIntent.putExtra("email", emailUser);
                    pedidosIntent.putExtra("username", usernameUser);
                    startActivity(pedidosIntent);
                    return true;
                } else if (itemId == R.id.navigation_perfil) {
                    // Navegar a ProfileActivity con los datos
                    Intent perfilIntent = new Intent(BaseActivity.this, ProfileActivity.class);
                    perfilIntent.putExtra("name", nameUser);
                    perfilIntent.putExtra("email", emailUser);
                    perfilIntent.putExtra("username", usernameUser);
                    startActivity(perfilIntent);
                    return true;
                }

                return false;
            }
        });
    }
}