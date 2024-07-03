package com.example.pedidosya2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.entidades.Producto;

import java.util.List;

public class AdaptadorProductosCarrito extends RecyclerView.Adapter<AdaptadorProductosCarrito.ViewHolder> {

    private Context mContext;
    private List<Producto> mProductos;

    public AdaptadorProductosCarrito(Context context, List<Producto> productos) {
        mContext = context;
        mProductos = productos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_producto_carrito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = mProductos.get(position);
        holder.nombreProductoTextView.setText(producto.getNombre());
        holder.cantidadProductoTextView.setText("Cantidad: " + producto.getCantidad());
    }

    @Override
    public int getItemCount() {
        return mProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreProductoTextView;
        TextView cantidadProductoTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreProductoTextView = itemView.findViewById(R.id.nombreProductoTextView);
            cantidadProductoTextView = itemView.findViewById(R.id.cantidadProductoTextView);
        }
    }
}