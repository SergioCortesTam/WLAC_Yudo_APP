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

    private static final String TAG = "ProductosFragment";

    private RecyclerView recyclerProductos;
    private ProductoAdapter productoAdapter;
    private List<Producto> listaProductos;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ChipGroup chipGroupFiltros;
    private FloatingActionButton fabAgregarProducto, fabCarrito;
    private String currentCategoryFilter = "Todos"; // Default filter

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerProductos = view.findViewById(R.id.recycler_productos);
        chipGroupFiltros = view.findViewById(R.id.chip_group_filtros);
        fabAgregarProducto = view.findViewById(R.id.fab_agregar_producto);
        fabCarrito = view.findViewById(R.id.fab_carrito);

        setupRecyclerView();
        setupChipGroupListener();
        checkUserRoleForFAB();
        loadProductos(currentCategoryFilter);

        fabAgregarProducto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Funcionalidad de añadir desde aquí no implementada (usar menú Profesor).", Toast.LENGTH_LONG).show();
        });

        // --- INICIO DE CAMBIO: Navegación al CarritoFragment ---
        fabCarrito.setOnClickListener(v -> {
            // Toast.makeText(getContext(), "Abrir carrito de compras.", Toast.LENGTH_SHORT).show(); // Opcional, puedes quitarlo
            replaceFragment(new CarritoFragment());
        });
        // --- FIN DE CAMBIO ---

        return view;
    }

    private void setupRecyclerView() {
        listaProductos = new ArrayList<>();
        // Asegúrate que el contexto no sea nulo. requireContext() es más seguro aquí si el fragment está adjunto.
        productoAdapter = new ProductoAdapter(requireContext(), listaProductos);
        recyclerProductos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerProductos.setAdapter(productoAdapter);
    }

    private void setupChipGroupListener() {
        chipGroupFiltros.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = group.findViewById(checkedId);
            if (selectedChip != null) {
                currentCategoryFilter = selectedChip.getText().toString();
            } else {
                currentCategoryFilter = "Todos";
            }
            loadProductos(currentCategoryFilter);
        });
    }


    private void checkUserRoleForFAB() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (getActivity() == null || !isAdded()) { // Prevenir crash si el fragment no está adjunto
                            return;
                        }
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("profesor".equals(role)) {
                                fabAgregarProducto.setVisibility(View.VISIBLE);
                            } else {
                                fabAgregarProducto.setVisibility(View.GONE);
                            }
                        } else {
                            fabAgregarProducto.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (getActivity() == null || !isAdded()) {
                            return;
                        }
                        Log.w(TAG, "Error getting user role", e);
                        fabAgregarProducto.setVisibility(View.GONE);
                    });
        } else {
            if (getActivity() == null || !isAdded()) {
                return;
            }
            fabAgregarProducto.setVisibility(View.GONE);
        }
    }


    private void loadProductos(String categoria) {
        Log.d(TAG, "Cargando productos para la categoría: " + categoria);
        Query query = db.collection("productos").orderBy("timestamp", Query.Direction.DESCENDING);

        if (!"Todos".equalsIgnoreCase(categoria) && categoria != null) {
            query = query.whereEqualTo("categoria", categoria);
        }

        query.get().addOnCompleteListener(task -> {
            if (getActivity() == null || !isAdded()) { // Prevenir crash
                return;
            }
            if (task.isSuccessful()) {
                listaProductos.clear();
                if (task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Producto producto = document.toObject(Producto.class);
                        producto.setId(document.getId());
                        listaProductos.add(producto);
                    }
                }
                productoAdapter.notifyDataSetChanged(); // Se puede optimizar con notifyItemRangeInserted etc.
                Log.d(TAG, "Productos cargados: " + listaProductos.size());
                if(listaProductos.isEmpty() && getContext() != null){ // getContext() para el Toast
                    Toast.makeText(getContext(), "No hay productos en esta categoría.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "Error al cargar productos.", task.getException());
                if(getContext() != null){
                    Toast.makeText(getContext(), "Error al cargar productos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        if (getActivity() == null || !isAdded()) { // Comprobación adicional
            return;
        }
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        // Asegúrate que R.id.main_content es el ID correcto de tu contenedor de Fragments en la Activity
        transaction.replace(R.id.main_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}