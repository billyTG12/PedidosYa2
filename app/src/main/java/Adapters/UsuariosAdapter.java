//package Adapters;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.pedidosya2.ApiClient;
//import com.example.pedidosya2.ApiService;
//import com.example.pedidosya2.R;
//import com.example.pedidosya2.UserService;
//import com.example.pedidosya2.entidades.Usuario;
//
//import java.util.List;
//
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//
//public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {
//
//    private Context mContext;
//    private List<Usuario> mUsuarios;
//    private UserService mUserService;
//
//    public UsuariosAdapter(Context context, List<Usuario> usuarios) {
//        mContext = context;
//        mUsuarios = usuarios;
//        mUserService = ApiClient.getService();
//    }
//
//    @NonNull
//    @Override
//    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lista_usuarios, parent, false);
//        return new UsuarioViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
//        Usuario usuario = mUsuarios.get(position);
//
//        holder.editTextUsername.setText(usuario.getUsername());
//        holder.editTextFirstName.setText(usuario.getFirstName());
//        holder.editTextLastName.setText(usuario.getLastName());
//        holder.editTextEmail.setText(usuario.getEmail());
//        holder.editTextPassword.setText(usuario.getPassword());
//
//        holder.buttonSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String newUsername = holder.editTextUsername.getText().toString();
//                String newFirstName = holder.editTextFirstName.getText().toString();
//                String newLastName = holder.editTextLastName.getText().toString();
//                String newEmail = holder.editTextEmail.getText().toString();
//                String newPassword = holder.editTextPassword.getText().toString();
//
//                usuario.setUsername(newUsername);
//                usuario.setFirstName(newFirstName);
//                usuario.setLastName(newLastName);
//                usuario.setEmail(newEmail);
//                usuario.setPassword(newPassword);
//
//                notifyDataSetChanged();
//
//
//                Call<Usuario> call = mUserService.updateUser(usuario.getId(), usuario);
//                call.enqueue(new Callback<Usuario>() {
//                    @Override
//                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
//                        if (response.isSuccessful()) {
//                            Toast.makeText(mContext, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
//                        } else {
//
//                            Toast.makeText(mContext, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Call<Usuario> call, Throwable t) {
//                        Toast.makeText(mContext, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();   }
//                });
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mUsuarios.size();
//    }
//
//    public class UsuarioViewHolder extends RecyclerView.ViewHolder {
//        EditText editTextUsername, editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
//        Button buttonSave;
//
//        public UsuarioViewHolder(@NonNull View itemView) {
//            super(itemView);
//            editTextUsername = itemView.findViewById(R.id.editTextUsername);
//            editTextFirstName = itemView.findViewById(R.id.editTextFirstName);
//            editTextLastName = itemView.findViewById(R.id.editTextLastName);
//            editTextEmail = itemView.findViewById(R.id.editTextEmail);
//            editTextPassword = itemView.findViewById(R.id.editTextPassword);
//            buttonSave = itemView.findViewById(R.id.buttonSave);
//        }
//    }
//}