package com.example.pedidosya2;
public class UsuarioClass {
    private String id;
    private String name;
    private String email;
    private String username;
    private String password;
    private String profileImageUrl;

    public UsuarioClass() {
        // Constructor vacío requerido por Firebase
    }

    public UsuarioClass(String id, String name, String email, String username, String password, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profileImageUrl = profileImageUrl; // Inicializar el nuevo campo
    }

    public UsuarioClass(String id, String name, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
