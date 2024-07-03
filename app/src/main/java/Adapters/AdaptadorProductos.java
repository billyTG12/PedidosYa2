package Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pedidosya2.DetalleProductoActivity;
import com.example.pedidosya2.R;
import com.example.pedidosya2.entidades.CarritoCompras;
import com.example.pedidosya2.entidades.Producto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ProductoViewHolder> {

    private Context context;
    private List<Producto> productos;
    private boolean mostrarBotonAgregar;

    public AdaptadorProductos(Context context, List<Producto> productos, boolean mostrarBotonAgregar) {
        this.context = context;
        this.productos = productos;
        this.mostrarBotonAgregar = mostrarBotonAgregar;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_productos, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);

        holder.nombreProducto.setText(producto.getNombre());
        holder.precioProducto.setText("Precio: " + producto.getPrecioAsString());

        if (mostrarBotonAgregar) {
            holder.cantidadProducto.setVisibility(View.GONE); // Ocultar TextView de cantidad
            holder.btnAgregarCarrito.setVisibility(View.VISIBLE);
            holder.btnAgregarCarrito.setOnClickListener(v -> mostrarDialogoCantidad(producto));
        } else {
            holder.cantidadProducto.setVisibility(View.VISIBLE); // Mostrar TextView de cantidad
            holder.cantidadProducto.setText("Cantidad: " + producto.getCantidad()); // Actualizar cantidad
            holder.btnAgregarCarrito.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(producto.getUrlImagen())
                .placeholder(R.drawable.china)
                .error(R.drawable.pizzas)
                .into(holder.imagenProducto);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleProductoActivity.class);
            intent.putExtra("producto", producto);
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void actualizarProductos(List<Producto> nuevosProductos) {
        productos.clear();
        productos.addAll(nuevosProductos);
        notifyDataSetChanged();
    }

    private void mostrarDialogoCantidad(Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Seleccionar cantidad");

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialogo_cantidad, (ViewGroup) null, false);
        final NumberPicker numberPicker = viewInflated.findViewById(R.id.numberPickerCantidad);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(1);

        builder.setView(viewInflated);
        builder.setPositiveButton("Agregar", (dialog, which) -> {
            int cantidad = numberPicker.getValue();
            producto.setCantidad(cantidad); // Asegúrate de tener un método setCantidad en Producto
            CarritoCompras.getInstance().agregarProducto(producto);
            Toast.makeText(context, cantidad + " " + producto.getNombre() + " agregado(s) al carrito", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenProducto;
        TextView nombreProducto;
        TextView precioProducto;
        TextView cantidadProducto;
        Button btnAgregarCarrito;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);

            imagenProducto = itemView.findViewById(R.id.idimagenProducto);
            nombreProducto = itemView.findViewById(R.id.idNombreProductos);
            precioProducto = itemView.findViewById(R.id.idPrecioProducto);
            cantidadProducto = itemView.findViewById(R.id.idCantidadProducto);
            btnAgregarCarrito = itemView.findViewById(R.id.idBtnAgregarCarrito);
        }
    }
}