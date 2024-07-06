package com.example.pedidosya2;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private LatLng ubicacionSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Configurar el evento de clic en el mapa para seleccionar una ubicación
        mMap.setOnMapClickListener(this);

        // Configurar la cámara para mostrar solo la ciudad de Cajamarca
        LatLng cajamarca = new LatLng(-7.163, -78.500);
        float zoomLevel = 12f; // Controla el nivel de zoom, ajusta según sea necesario

        // Mover la cámara y establecer el nivel de zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cajamarca, zoomLevel));


        mMap.addMarker(new MarkerOptions()
                .position(cajamarca)
                .title("Cajamarca"));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Limitar la selección de ubicaciones dentro de los límites de la ciudad de Cajamarca
        LatLng cajamarcaBoundsSW = new LatLng(-7.189, -78.530);
        LatLng cajamarcaBoundsNE = new LatLng(-7.150, -78.475);

        // Verificar si la ubicación seleccionada está dentro de los límites
        if (latLng.latitude >= cajamarcaBoundsSW.latitude && latLng.latitude <= cajamarcaBoundsNE.latitude &&
                latLng.longitude >= cajamarcaBoundsSW.longitude && latLng.longitude <= cajamarcaBoundsNE.longitude) {
           mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));

            String direccion = obtenerDireccion(latLng.latitude, latLng.longitude);

            Toast.makeText(this, "Ubicación guardada: " + direccion, Toast.LENGTH_SHORT).show();

            // Devuelve la ubicación seleccionada a ConfirmarPedidoActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("direccion", direccion);
            setResult(RESULT_OK, resultIntent);
            finish(); // Cierra MapsActivity y devuelve el resultado
        } else {
            // La ubicación está fuera de los límites de Cajamarca, mostrar un mensaje de error
            Toast.makeText(this, "La ubicación seleccionada está fuera de los límites de Cajamarca", Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerDireccion(double latitud, double longitud) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String direccion = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    builder.append(address.getAddressLine(i)).append(", ");
                }
                direccion = builder.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return direccion;
    }
}