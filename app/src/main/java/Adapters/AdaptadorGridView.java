package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pedidosya2.entidades.Categoria;
import com.example.pedidosya2.R;
import com.example.pedidosya2.RestaurantesActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorGridView extends BaseAdapter {
    private Context mContext;
    private List<Categoria> mCategorias;

    public AdaptadorGridView(Context context, List<Categoria> categorias) {
        mContext = context;
        mCategorias = categorias;
    }

    @Override
    public int getCount() {
        return mCategorias.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategorias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.item_categorias, null);
        }

        final Categoria categoria = mCategorias.get(position);

        TextView nombreTextView = gridView.findViewById(R.id.tvNombre);
        TextView detalleTextView = gridView.findViewById(R.id.tvDatelleCategoria);
        ImageView imageView = gridView.findViewById(R.id.imageView);

        nombreTextView.setText(categoria.getNombre());
        detalleTextView.setText(categoria.getDetalle());
        Picasso.get().load(categoria.getImagen()).into(imageView);

        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreCategoria = categoria.getNombre();
                Intent intent = new Intent(mContext, RestaurantesActivity.class);
                intent.putExtra("nombre_categoria_seleccionada", nombreCategoria);
                mContext.startActivity(intent);
            }
        });

        return gridView;
    }
}