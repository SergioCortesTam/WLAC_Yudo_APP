package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductosFragment extends Fragment {
    // Fragmento para mostrar productos de la tienda

    private RecyclerView recyclerProductos;
    private ProductoAdapter productoAdapter;
    private List<Producto> listaProductos;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Configurar RecyclerView
        recyclerProductos = view.findViewById(R.id.recycler_productos);
        listaProductos = new ArrayList<>();
        productoAdapter = new ProductoAdapter(requireContext(), listaProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerProductos.setAdapter(productoAdapter);

        // Botones flotantes
        FloatingActionButton fabAgregarProducto = view.findViewById(R.id.fab_agregar_producto);
        FloatingActionButton fabCarrito = view.findViewById(R.id.fab_carrito);

        // Verificar rol para mostrar botón de añadir
        checkUserRoleForFAB(fabAgregarProducto);

        // Cargar productos iniciales
        loadProductos("Todos");

        // Navegar al carrito
        fabCarrito.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new CarritoFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    // Verificar si el usuario es profesor para mostrar botón de añadir
    private void checkUserRoleForFAB(FloatingActionButton fab) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() &&
                                "profesor".equals(documentSnapshot.getString("role"))) {
                            fab.setVisibility(View.VISIBLE);
                        } else {
                            fab.setVisibility(View.GONE);
                        }
                    });
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    // Cargar productos desde Firestore
    private void loadProductos(String categoria) {
        Query query = db.collection("productos")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Aplicar filtro si no es "Todos"
        if (!"Todos".equals(categoria)) {
            query = query.whereEqualTo("categoria", mapCategoria(categoria));
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaProductos.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Producto producto = document.toObject(Producto.class);
                    producto.setId(document.getId());
                    listaProductos.add(producto);
                }
                productoAdapter.notifyDataSetChanged();
            }
        });
    }

    // Mapear nombre de categoría a valor en Firestore
    private String mapCategoria(String categoria) {
        switch (categoria) {
            case "Clases de Judogis": return "judogi";
            case "Merchandising": return "merchandising";
            case "Otros": return "otros";
            default: return null;
        }
    }
}