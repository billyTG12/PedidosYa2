package com.example.pedidosya2.entidades;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



//Clase para obtener la dirección a partir de las coordenadas
public class GeocoderUtil {

    public static String obtenerDireccion(Context context, double latitud, double longitud) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String direccion = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    builder.append(address.getAddressLine(i)).append(" ");
                }
                direccion = builder.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return direccion;
    }
}