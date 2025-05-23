package com.example.wlac_yudo_app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarritoFragment extends Fragment implements CarritoAdapter.OnCartUpdatedListener {

    private RecyclerView recyclerCarritoItems;
    private CarritoAdapter carritoAdapter;
    private List<CartManager.CartItem> itemsDelCarrito;
    private TextView tvTotalCarrito, tvCarritoVacio;
    private Button btnSolicitarProductos;
    private final String TU_DIRECCION_DE_CORREO = "tuemail@example.com"; // Reemplaza con tu email real

    public CarritoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);

        recyclerCarritoItems = view.findViewById(R.id.recycler_carrito_items);
        tvTotalCarrito = view.findViewById(R.id.txt_total_carrito);
        btnSolicitarProductos = view.findViewById(R.id.btn_solicitar_productos);
        tvCarritoVacio = view.findViewById(R.id.txt_carrito_vacio);

        itemsDelCarrito = new ArrayList<>(CartManager.getInstance().getCartItems()); // Copia para el adaptador
        carritoAdapter = new CarritoAdapter(getContext(), itemsDelCarrito, this);

        recyclerCarritoItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCarritoItems.setAdapter(carritoAdapter);

        btnSolicitarProductos.setOnClickListener(v -> enviarSolicitudPorEmail());

        actualizarVistaCarrito();
        return view;
    }

    private void enviarSolicitudPorEmail() {
        List<CartManager.CartItem> currentCartItems = CartManager.getInstance().getCartItems();
        if (currentCartItems.isEmpty()) {
            Toast.makeText(getContext(), "El carrito está vacío.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Solicitud de productos:\n\n");
        for (CartManager.CartItem item : currentCartItems) {
            Producto p = item.getProducto();
            emailBody.append(String.format(Locale.getDefault(), "- %s (Cantidad: %d) - Precio Ud.: €%.2f\n",
                    p.getNombre(), item.getQuantity(), p.getPrecio()));
        }
        emailBody.append(String.format(Locale.getDefault(), "\nTotal del pedido: €%.2f", CartManager.getInstance().getTotalPrice()));
        emailBody.append("\n\nPor favor, preparar este pedido si los artículos están disponibles.");
        // Aquí podrías añadir información del usuario si está logueado y quieres incluirla.
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // if (user != null && user.getEmail() != null) {
        //     emailBody.append("\n\nSolicitado por: ").append(user.getEmail());
        // }


        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Solo apps de email
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{TU_DIRECCION_DE_CORREO});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Solicitud de Productos - App Yudo");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody.toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email usando..."));
            // Opcional: Limpiar carrito después de enviar
            // CartManager.getInstance().clearCart();
            // actualizarVistaCarrito();
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "No hay ninguna aplicación de email instalada.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCartUpdated() {
        actualizarVistaCarrito();
    }

    private void actualizarVistaCarrito() {
        // Actualiza la lista local del adaptador si es necesario (aunque el listener ya lo hace).
        // Aquí principalmente actualizamos el total y la visibilidad de "carrito vacío".
        itemsDelCarrito.clear();
        itemsDelCarrito.addAll(CartManager.getInstance().getCartItems()); // Obtiene la lista más reciente
        carritoAdapter.notifyDataSetChanged(); // Notifica al adaptador de los cambios

        double total = CartManager.getInstance().getTotalPrice();
        tvTotalCarrito.setText(String.format(Locale.getDefault(), "Total: €%.2f", total));

        if (itemsDelCarrito.isEmpty()) {
            tvCarritoVacio.setVisibility(View.VISIBLE);
            recyclerCarritoItems.setVisibility(View.GONE);
            btnSolicitarProductos.setEnabled(false);
        } else {
            tvCarritoVacio.setVisibility(View.GONE);
            recyclerCarritoItems.setVisibility(View.VISIBLE);
            btnSolicitarProductos.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Asegúrate de que el carrito se actualice si el usuario vuelve a este fragment
        actualizarVistaCarrito();
    }
}