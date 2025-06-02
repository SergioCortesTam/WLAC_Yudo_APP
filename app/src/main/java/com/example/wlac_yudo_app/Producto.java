package com.example.wlac_yudo_app;

import com.google.firebase.firestore.PropertyName; // Import this

public class Producto {
    private String id; // Firestore document ID
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private String imageUrl;

    public Producto() {
        // Firestore necesita un constructor vacío
    }

    public Producto(String nombre, String descripcion, double precio, String categoria, String imageUrl) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }

    @PropertyName("imageUrl") // Use this if your Firestore field name is imageUrl
    public String getImageUrl() { return imageUrl; }


    // Setters
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @PropertyName("imageUrl")
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}