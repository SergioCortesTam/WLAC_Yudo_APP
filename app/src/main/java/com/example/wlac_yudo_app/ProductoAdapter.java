package com.example.wlac_yudo_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Importado
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos;
    private Context context;

    public ProductoAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "€%.2f", producto.getPrecio()));
        holder.tvCategoria.setText(producto.getCategoria());
        // holder.tvDescripcion.setText(producto.getDescripcion()); // If you want to show it directly

        if (producto.getImageUrl() != null && !producto.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(producto.getImageUrl())
                    .placeholder(R.drawable.ic_noimagen) // placeholder mientras carga
                    .error(R.drawable.ic_noimagen)       // imagen si falla
                    .into(holder.imgProducto);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_noimagen)
                    .into(holder.imgProducto);
        }


        holder.itemView.setOnClickListener(v -> {
            // Example: Show description in a Toast or navigate to a detail screen
            Toast.makeText(context, producto.getDescripcion(), Toast.LENGTH_LONG).show();
        });

        // --- INICIO DE CAMBIO: Funcionalidad botón añadir carrito ---
        holder.btnAnadirCarrito.setOnClickListener(v -> {
            CartManager.getInstance().addItem(producto);
            Toast.makeText(context, producto.getNombre() + " añadido al carrito", Toast.LENGTH_SHORT).show();
        });
        // --- FIN DE CAMBIO ---
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void updateData(List<Producto> newProductos) {
        this.productos.clear();
        this.productos.addAll(newProductos);
        notifyDataSetChanged();
    }


    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView tvNombre, tvPrecio, tvCategoria, tvDescripcion;
        Button btnAnadirCarrito; // --- AÑADIDO ---

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.img_producto);
            tvNombre = itemView.findViewById(R.id.txt_nombre_producto);
            tvPrecio = itemView.findViewById(R.id.txt_precio_producto);
            tvCategoria = itemView.findViewById(R.id.txt_categoria_producto);
            tvDescripcion = itemView.findViewById(R.id.txt_descripcion_producto);
            btnAnadirCarrito = itemView.findViewById(R.id.btn_anadir_carrito); // --- AÑADIDO ---
        }
    }
}