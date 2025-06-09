package com.example.wlac_yudo_app;

import com.google.firebase.firestore.PropertyName; // Import this


public class Producto {
    // Modelo para representar productos en la tienda

    private String id; // ID de Firestore
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private String imageUrl; // URL de la imagen

    // Constructor vacío requerido por Firestore
    public Producto() {}

    public Producto(String nombre, String descripcion, double precio,
                    String categoria, String imageUrl) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.imageUrl = imageUrl;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // Anotaciones para compatibilidad con Firestore
    @PropertyName("imageUrl")
    public String getImageUrl() { return imageUrl; }

    @PropertyName("imageUrl")
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}