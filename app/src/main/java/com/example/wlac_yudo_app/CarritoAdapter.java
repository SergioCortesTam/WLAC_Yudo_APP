package com.example.wlac_yudo_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<CartManager.CartItem> cartItems;
    private Context context;
    private OnCartUpdatedListener cartUpdatedListener; // Interfaz para actualizar el total

    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    public CarritoAdapter(Context context, List<CartManager.CartItem> cartItems, OnCartUpdatedListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartUpdatedListener = listener;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        CartManager.CartItem cartItem = cartItems.get(position);
        Producto producto = cartItem.getProducto();

        holder.tvNombreProducto.setText(producto.getNombre());
        holder.tvPrecioProducto.setText(String.format(Locale.getDefault(), "€%.2f", producto.getPrecio()));
        holder.tvCantidad.setText(String.format(Locale.getDefault(), "Qty: %d", cartItem.getQuantity()));

        holder.btnEliminar.setOnClickListener(v -> {
            CartManager.getInstance().removeProductCompletely(producto.getId());
            cartItems.remove(position); // Elimina de la lista local del adaptador
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size()); // Actualiza las posiciones
            cartUpdatedListener.onCartUpdated(); // Notifica para actualizar el total y la vista
            Toast.makeText(context, producto.getNombre() + " eliminado del carrito", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartManager.CartItem> newCartItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newCartItems);
        notifyDataSetChanged();
        cartUpdatedListener.onCartUpdated();
    }

    static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreProducto, tvPrecioProducto, tvCantidad;
        ImageButton btnEliminar;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto = itemView.findViewById(R.id.txt_nombre_producto_carrito);
            tvPrecioProducto = itemView.findViewById(R.id.txt_precio_producto_carrito);
            tvCantidad = itemView.findViewById(R.id.txt_cantidad_carrito);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_item_carrito);
        }
    }
}