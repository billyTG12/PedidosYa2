package com.example.pedidosya2;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.UserService;
import com.example.pedidosya2.entidades.Usuario;

import java.util.List;

import Adapters.UsuariosAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListUsuariosActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private UsuariosAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_usuarios);

        mRecyclerView = findViewById(R.id.idRvusuarios);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://661d250fe7b95ad7fa6c456d.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        UserService service = retrofit.create(UserService.class);


        Call<List<Usuario>> call = service.getUsers();
        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ListUsuariosActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Usuario> usuarios = response.body();
                mAdapter = new UsuariosAdapter(ListUsuariosActivity.this, usuarios);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(ListUsuariosActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}