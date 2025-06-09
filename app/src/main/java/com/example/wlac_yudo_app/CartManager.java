package com.example.wlac_yudo_app; // Asegúrate que el package sea el correcto

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    // Gestor del carrito de compras (Singleton)
    private static CartManager instance;
    private Map<String, CartItem> items; // Mapa de productos por ID

    private CartManager() {
        items = new HashMap<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Añade un producto al carrito
    public void addItem(Producto producto, String tallaSeleccionada) {
        if (items.containsKey(producto.getId())) {
            items.get(producto.getId()).incrementQuantity();
        } else {
            items.put(producto.getId(), new CartItem(producto, 1));
        }
    }

    // Reduce cantidad de un producto
    public void removeItem(Producto producto) {
        if (items.containsKey(producto.getId())) {
            CartItem currentItem = items.get(producto.getId());
            currentItem.decrementQuantity();
            if (currentItem.getQuantity() <= 0) {
                items.remove(producto.getId());
            }
        }
    }

    // Elimina completamente un producto
    public void removeProductCompletely(String productId) {
        items.remove(productId);
    }

    // Obtiene todos los items del carrito
    public List<CartItem> getCartItems() {
        return new ArrayList<>(items.values());
    }

    // Vacía el carrito
    public void clearCart() {
        items.clear();
    }

    // Calcula el precio total
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items.values()) {
            total += item.getProducto().getPrecio() * item.getQuantity();
        }
        return total;
    }

    // Representa un item en el carrito
    public static class CartItem {
        private Producto producto;
        private int quantity;

        public CartItem(Producto producto, int quantity) {
            this.producto = producto;
            this.quantity = quantity;
        }

        public Producto getProducto() { return producto; }
        public int getQuantity() { return quantity; }
        public void incrementQuantity() { this.quantity++; }
        public void decrementQuantity() { this.quantity--; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}