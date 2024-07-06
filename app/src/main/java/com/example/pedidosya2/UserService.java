//package com.example.pedidosya2;
//
//import com.example.pedidosya2.LoginRequest;
//import com.example.pedidosya2.LoginResponse;
//import com.example.pedidosya2.RegisterRequest;
//import com.example.pedidosya2.RegisterResponse;
//import com.example.pedidosya2.entidades.Producto;
//import com.example.pedidosya2.entidades.Usuario;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.GET;
//import retrofit2.http.POST;
//import retrofit2.http.PUT;
//import retrofit2.http.Path;
//
//public interface UserService {
//
//    @POST("users/")
//    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
//
//
//    @POST("users/")
//    Call<RegisterResponse> registerUsers(@Body RegisterRequest registerRequest);
//    @GET("users/")
//    Call<List<Usuario>> getUsers();
//
//
//        @PUT("users/{userId}")
//        Call<Usuario> updateUser(@Path("userId") int userId, @Body Usuario user);
//    }
//
//
//
