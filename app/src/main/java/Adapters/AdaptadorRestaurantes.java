package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.R;
import com.example.pedidosya2.entidades.Restaurante;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorRestaurantes extends RecyclerView.Adapter<AdaptadorRestaurantes.ViewHolder> {
    private Context mContext;
    private List<Restaurante> mRestaurantes;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, String restauranteNombre);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdaptadorRestaurantes(Context context, List<Restaurante> restaurantes) {
        mContext = context;
        mRestaurantes = restaurantes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurantes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurante restaurante = mRestaurantes.get(position);

        holder.nombreTextView.setText(restaurante.getNombre());
        holder.direccionTextView.setText(restaurante.getDireccion());
        holder.ratingBar.setRating(Float.parseFloat(restaurante.getCalificacion()));
        Picasso.get().load(restaurante.getImagenUrl()).into(holder.imagenImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(adapterPosition, restaurante.getNombre());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenImageView;
        TextView nombreTextView;
        TextView direccionTextView;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenImageView = itemView.findViewById(R.id.imageView3);
            nombreTextView = itemView.findViewById(R.id.idNombreRes);
            direccionTextView = itemView.findViewById(R.id.idDireccion);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
