package com.example.pedidosya2;
import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Verificar si FirebaseApp se ha inicializado previamente
        if (FirebaseApp.getApps(this).isEmpty()) {
            Log.d("MyApplication", "Inicializando FirebaseApp...");
            FirebaseApp.initializeApp(this);
        } else {
            Log.d("MyApplication", "FirebaseApp ya está inicializado.");
        }

        // Habilitar persistencia offline para Firebase Database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}