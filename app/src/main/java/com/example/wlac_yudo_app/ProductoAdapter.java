package com.example.wlac_yudo_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

        // Configurar imagen
        if (producto.getImageUrl() != null && !producto.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(producto.getImageUrl())
                    .placeholder(R.drawable.ic_noimagen)
                    .error(R.drawable.ic_noimagen)
                    .into(holder.imgProducto);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_noimagen)
                    .into(holder.imgProducto);
        }

        // Configurar spinners de tallas
        holder.setupSpinners();

        // Configurar listeners para cambio de tipo de talla
        holder.setupTallaListeners();

        // Click en el item para mostrar descripción
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, producto.getDescripcion(), Toast.LENGTH_LONG).show();
        });

        // Botón añadir al carrito
        holder.btnAnadirCarrito.setOnClickListener(v -> {
            String tallaSeleccionada = holder.getTallaSeleccionada();
            CartManager.getInstance().addItem(producto, tallaSeleccionada);
            Toast.makeText(context, producto.getNombre() + " (Talla: " + tallaSeleccionada + ") añadido al carrito", Toast.LENGTH_SHORT).show();
        });
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
        TextView tvNombre, tvPrecio;
        Button btnAnadirCarrito;
        RadioGroup radioGroupTipoTalla;
        RadioButton radioNinos, radioAdultos;
        Spinner spinnerTallasNinos, spinnerTallasAdultos;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.img_producto);
            tvNombre = itemView.findViewById(R.id.txt_nombre_producto);
            tvPrecio = itemView.findViewById(R.id.txt_precio_producto);
            btnAnadirCarrito = itemView.findViewById(R.id.btn_anadir_carrito);
            radioGroupTipoTalla = itemView.findViewById(R.id.radio_group_tipo_talla);
            radioNinos = itemView.findViewById(R.id.radio_ninos);
            radioAdultos = itemView.findViewById(R.id.radio_adultos);
            spinnerTallasNinos = itemView.findViewById(R.id.spinner_tallas_ninos);
            spinnerTallasAdultos = itemView.findViewById(R.id.spinner_tallas_adultos);
        }

        public void setupSpinners() {
            // Configurar spinner de tallas para niños (3-16 años)
            String[] tallasNinos = {"3 años", "4 años", "5 años", "6 años", "7 años", "8 años",
                    "9 años", "10 años", "11 años", "12 años", "13 años", "14 años",
                    "15 años", "16 años"};
            ArrayAdapter<String> adapterNinos = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_item, tallasNinos);
            adapterNinos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTallasNinos.setAdapter(adapterNinos);

            // Configurar spinner de tallas para adultos
            String[] tallasAdultos = {"XS", "S", "M", "L", "XL", "XXL"};
            ArrayAdapter<String> adapterAdultos = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_item, tallasAdultos);
            adapterAdultos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTallasAdultos.setAdapter(adapterAdultos);
        }

        public void setupTallaListeners() {
            radioGroupTipoTalla.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radio_ninos) {
                    spinnerTallasNinos.setVisibility(View.VISIBLE);
                    spinnerTallasAdultos.setVisibility(View.GONE);
                } else if (checkedId == R.id.radio_adultos) {
                    spinnerTallasNinos.setVisibility(View.GONE);
                    spinnerTallasAdultos.setVisibility(View.VISIBLE);
                }
            });
        }

        public String getTallaSeleccionada() {
            if (radioNinos.isChecked()) {
                return spinnerTallasNinos.getSelectedItem().toString();
            } else {
                return spinnerTallasAdultos.getSelectedItem().toString();
            }
        }
    }
}