package com.example.pedidosya2;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.pedidosya2.databinding.ActivityMapaEntregaBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class MapaEntregaActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityMapaEntregaBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String direccion;
    private String id;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapaEntregaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("Pedidos");

        direccion = getIntent().getStringExtra("direccion");
        id = getIntent().getStringExtra("id");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnmarcarentregado = findViewById(R.id.btnEntregarPedido);
        btnmarcarentregado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar al método marcarPedidoComoEntregado() cuando se haga clic en el botón
                marcarPedidoComoEntregado();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (direccion != null && !direccion.isEmpty()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(direccion, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng destinationLocation = new LatLng(address.getLatitude(), address.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(destinationLocation).title("Entrega"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15));

                    if (checkLocationPermission()) {
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, location -> {
                                    if (location != null) {
                                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                        // Cargar el icono de GPS personalizado desde los recursos
                                        BitmapDescriptor icon = getBitmapDescriptorFromVector(R.drawable.ic_custom_location_marker);
                                        googleMap.addMarker(new MarkerOptions()
                                                .position(currentLocation)
                                                .title("Ubicación Actual")
                                                .icon(icon));

                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                                        // Dibujar la ruta solo si lo deseas
                                        drawRoute(currentLocation, destinationLocation);
                                    } else {
                                        Toast.makeText(MapaEntregaActivity.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(this, "No se encontró la ubicación", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Dirección no disponible", Toast.LENGTH_SHORT).show();
        }
    }
    private BitmapDescriptor getBitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(this, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestLocationPermission();
            return false;
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }


    private void drawRoute(LatLng origin, LatLng destination) {
        String apiKey = getGoogleMapsApiKey();

        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(this, "Clave API de Google Maps no encontrada", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .mode(TravelMode.DRIVING);

        request.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                if (result != null && result.routes != null && result.routes.length > 0) {
                    List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());

                    runOnUiThread(() -> {
                        googleMap.addPolyline(new PolylineOptions()
                                .addAll(decodedPath)
                                .color(Color.BLUE)
                                .width(10f));
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MapaEntregaActivity.this, "No se encontró una ruta", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Throwable e) {
                runOnUiThread(() -> {
                    Toast.makeText(MapaEntregaActivity.this, "Error al obtener la ruta", Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        });
    }
    private String getGoogleMapsApiKey() {
        // Obtener la clave API de Google Maps desde el archivo AndroidManifest.xml
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            return bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void marcarPedidoComoEntregado() {
        Log.d("MapaEntregaActivity", "marcarPedidoComoEntregado: id=" + id); // Verifica el valor de pedidoId
        if (id != null) {
            mDatabase.child(id).child("atendido").setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MapaEntregaActivity.this, "Pedido marcado como entregado", Toast.LENGTH_SHORT).show();
                        Log.d("MapaEntregaActivity", "marcarPedidoComoEntregado: Pedido marcado como entregado");
                        finish(); // Cierra la actividad después de actualizar el estado
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MapaEntregaActivity.this, "Error al marcar el pedido como entregado", Toast.LENGTH_SHORT).show();
                        Log.e("MapaEntregaActivity", "marcarPedidoComoEntregado: Error al marcar el pedido como entregado", e);
                        e.printStackTrace();
                    });
        } else {
            Log.e("MapaEntregaActivity", "marcarPedidoComoEntregado: pedidoId es nulo");
            Toast.makeText(this, "Error: El pedido no tiene un ID válido", Toast.LENGTH_SHORT).show();
        }
    }
}