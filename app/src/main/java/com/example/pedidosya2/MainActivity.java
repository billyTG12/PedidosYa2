//
//package com.example.pedidosya2;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class MainActivity extends AppCompatActivity {
//    LoginResponse loginResponse;
//    TextView username;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        username = findViewById(R.id.textView2);
//        Intent intent = getIntent();
//        if(intent.getExtras() != null){
//            loginResponse = (LoginResponse) intent.getSerializableExtra("data");
//            username.setText(loginResponse.getUsername());
//            Log.e("TAG","=====> "+loginResponse.getEmail());
//        }
//        Button btnActivity=findViewById(R.id.idbutton);
//        btnActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(MainActivity.this, CategoriaActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//}