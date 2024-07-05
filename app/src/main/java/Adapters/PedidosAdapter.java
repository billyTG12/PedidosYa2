package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosya2.R;
import com.example.pedidosya2.entidades.Pedido;

import java.util.List;
import java.util.Map;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder> {

    private List<Pedido> pedidosList;

    public PedidosAdapter(List<Pedido> pedidosList) {
        this.pedidosList = pedidosList;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido_cliente, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidosList.get(position);
        holder.pedidoNombreUsuario.setText(pedido.getNombreUsuario());
        holder.pedidoEmailUsuario.setText(pedido.getEmailUsuario());
        holder.pedidoDireccion.setText( pedido.getDireccion());
        holder.pedidoMetodoPago.setText(pedido.getMetodoPago());
        holder.pedidoTotal.setText("$" + pedido.getTotal());
        holder.pedidoAtendido.setText((pedido.isAtendido() ? "Atendido" : "No Atendido"));

        // Mostrar los platos si existen
        if (pedido.getPlatos() != null && !pedido.getPlatos().isEmpty()) {
            StringBuilder platosText = new StringBuilder("Platos:\n");
            for (Map.Entry<String, Integer> entry : pedido.getPlatos().entrySet()) {
                platosText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            holder.pedidoPlatos.setText(platosText.toString());
        } else {
            holder.pedidoPlatos.setText("No hay platos registrados");
        }
    }

    @Override
    public int getItemCount() {
        return pedidosList.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView pedidoNombreUsuario, pedidoEmailUsuario, pedidoDireccion, pedidoMetodoPago, pedidoTotal, pedidoAtendido, pedidoPlatos;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            pedidoNombreUsuario = itemView.findViewById(R.id.pedido_nombre_usuario);
            pedidoEmailUsuario = itemView.findViewById(R.id.pedido_email_usuario);
            pedidoDireccion = itemView.findViewById(R.id.pedido_direccion);
            pedidoMetodoPago = itemView.findViewById(R.id.pedido_metodo_pago);
            pedidoTotal = itemView.findViewById(R.id.pedido_total);
            pedidoAtendido = itemView.findViewById(R.id.pedido_atendido);
            pedidoPlatos = itemView.findViewById(R.id.pedido_platos); // TextView para mostrar platos
        }
    }
}