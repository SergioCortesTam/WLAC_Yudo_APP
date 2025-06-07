package com.example.wlac_yudo_app; // Asegúrate que el package sea el correcto

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<String, CartItem> items; // Usamos un Map para manejar cantidades y evitar duplicados fácilmente

    private CartManager() {
        items = new HashMap<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(Producto producto, String tallaSeleccionada) {
        if (items.containsKey(producto.getId())) {
            CartItem currentItem = items.get(producto.getId());
            if (currentItem != null) {
                currentItem.incrementQuantity();
            }
        } else {
            items.put(producto.getId(), new CartItem(producto, 1));
        }
    }

    public void removeItem(Producto producto) {
        if (items.containsKey(producto.getId())) {
            CartItem currentItem = items.get(producto.getId());
            if (currentItem != null) {
                currentItem.decrementQuantity();
                if (currentItem.getQuantity() <= 0) {
                    items.remove(producto.getId());
                }
            }
        }
    }

    public void removeProductCompletely(String productId) {
        items.remove(productId);
    }


    public List<CartItem> getCartItems() {
        return new ArrayList<>(items.values());
    }

    public void clearCart() {
        items.clear();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items.values()) {
            total += item.getProducto().getPrecio() * item.getQuantity();
        }
        return total;
    }

    // Clase interna para representar un item en el carrito con cantidad
    public static class CartItem {
        private Producto producto;
        private int quantity;

        public CartItem(Producto producto, int quantity) {
            this.producto = producto;
            this.quantity = quantity;
        }

        public Producto getProducto() {
            return producto;
        }

        public int getQuantity() {
            return quantity;
        }

        public void incrementQuantity() {
            this.quantity++;
        }

        public void decrementQuantity() {
            this.quantity--;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}