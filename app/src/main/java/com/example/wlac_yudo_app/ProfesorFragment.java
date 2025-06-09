package com.example.wlac_yudo_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.*;

import java.util.*;

public class ProfesorFragment extends Fragment {

    private MaterialCardView cardGestionarCinturones, cardVerClases, cardMarcarAsistencia, cardAnadirProducto;
    private FirebaseFirestore db;
    private List<String> alumnoIds, alumnoNombres;

    public ProfesorFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profesor, container, false);

        db = FirebaseFirestore.getInstance();

        cardGestionarCinturones = view.findViewById(R.id.card_gestionar_cinturones);
        cardVerClases           = view.findViewById(R.id.card_ver_clases);
        cardMarcarAsistencia    = view.findViewById(R.id.card_marcar_asistencia);
        cardAnadirProducto      = view.findViewById(R.id.card_anadir_producto);

        alumnoIds = new ArrayList<>();
        alumnoNombres = new ArrayList<>();
        db.collection("users")
                .whereEqualTo("role", "alumno")
                .get()
                .addOnSuccessListener(qs -> {
                    for (DocumentSnapshot doc : qs) {
                        alumnoIds.add(doc.getId());
                        alumnoNombres.add(doc.getString("nombre"));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar alumnos.", Toast.LENGTH_SHORT).show();
                    if (alumnoIds == null) alumnoIds = new ArrayList<>();
                    if (alumnoNombres == null) alumnoNombres = new ArrayList<>();
                });

        cardGestionarCinturones.setOnClickListener(v -> mostrarDialogCinturones());
        cardVerClases.setOnClickListener(v -> mostrarDialogClases());
        cardMarcarAsistencia.setOnClickListener(v -> replaceFragment(new MarcarAsistenciaFragment()));
        cardAnadirProducto.setOnClickListener(v -> mostrarDialogAnadirProducto());

        return view;
    }

    /** Detecta si está en modo oscuro */
    private boolean esModoOscuro() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    /** Método helper para crear diálogos con colores corregidos */
    private AlertDialog crearDialogoConColoresCorregidos(String titulo, View contenido,
                                                         DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(titulo);
        builder.setView(contenido);
        builder.setPositiveButton("Guardar", positiveListener);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            aplicarColoresBotones(dialog);
        });

        dialog.show();
        return dialog;
    }

    /** Aplica colores adaptativos a los botones del diálogo */
    private void aplicarColoresBotones(AlertDialog dialog) {
        if (dialog == null) return;

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if (esModoOscuro()) {
            // Modo oscuro: usar colores claros
            if (positiveButton != null) {
                positiveButton.setTextColor(Color.LTGRAY);
            }
            if (negativeButton != null) {
                negativeButton.setTextColor(Color.LTGRAY);
            }
        } else {
            // Modo claro: usar colores oscuros
            if (positiveButton != null) {
                positiveButton.setTextColor(Color.parseColor("#1976D2")); // Azul
            }
            if (negativeButton != null) {
                negativeButton.setTextColor(Color.parseColor("#1976D2"));
            }
        }
    }

    /** Muestra un AlertDialog para añadir un nuevo producto */
    private void mostrarDialogAnadirProducto() {
        // Layout para el diálogo
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 20);

        // Input para Nombre del Producto
        final TextInputLayout nombreLayout = new TextInputLayout(requireContext());
        final TextInputEditText etNombreProducto = new TextInputEditText(requireContext());
        etNombreProducto.setHint("Nombre del Producto");
        nombreLayout.addView(etNombreProducto);
        layout.addView(nombreLayout);

        // Input para Descripción
        final TextInputLayout descLayout = new TextInputLayout(requireContext());
        final TextInputEditText etDescripcionProducto = new TextInputEditText(requireContext());
        etDescripcionProducto.setHint("Descripción");
        descLayout.addView(etDescripcionProducto);
        layout.addView(descLayout);

        // Input para Precio
        final TextInputLayout precioLayout = new TextInputLayout(requireContext());
        final TextInputEditText etPrecioProducto = new TextInputEditText(requireContext());
        etPrecioProducto.setHint("Precio (ej: 25.99)");
        etPrecioProducto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        precioLayout.addView(etPrecioProducto);
        layout.addView(precioLayout);

        // Spinner para Categoría
        TextView tvCategoria = new TextView(requireContext());
        tvCategoria.setText("Categoría:");
        tvCategoria.setPadding(0,20,0,5);
        layout.addView(tvCategoria);

        Spinner sCategoria = new Spinner(requireContext());
        String[] categorias = {"Judogui", "Merchandaising", "Otros"};
        ArrayAdapter<String> adCategoria = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categorias
        );
        adCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategoria.setAdapter(adCategoria);
        layout.addView(sCategoria);

        // Input para URL de Imagen
        final TextInputLayout imageUrlLayout = new TextInputLayout(requireContext());
        final TextInputEditText etImageUrlProducto = new TextInputEditText(requireContext());
        etImageUrlProducto.setHint("URL de la Imagen (opcional)");
        imageUrlLayout.addView(etImageUrlProducto);
        layout.addView(imageUrlLayout);

        // Crear diálogo con colores corregidos
        crearDialogoConColoresCorregidos("Añadir Nuevo Producto", layout, (dialog, which) -> {
            String nombre = etNombreProducto.getText().toString().trim();
            String descripcion = etDescripcionProducto.getText().toString().trim();
            String precioStr = etPrecioProducto.getText().toString().trim();
            String categoria = (String) sCategoria.getSelectedItem();
            String imageUrl = etImageUrlProducto.getText().toString().trim();

            if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
                Toast.makeText(requireContext(), "Nombre, descripción y precio son obligatorios.", Toast.LENGTH_LONG).show();
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(precioStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Formato de precio inválido.", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, Object> producto = new HashMap<>();
            producto.put("nombre", nombre);
            producto.put("descripcion", descripcion);
            producto.put("precio", precio);
            producto.put("categoria", categoria);
            producto.put("imageUrl", imageUrl.isEmpty() ? null : imageUrl);
            producto.put("timestamp", FieldValue.serverTimestamp());

            db.collection("productos")
                    .add(producto)
                    .addOnSuccessListener(documentReference -> Toast.makeText(requireContext(),
                            "Producto '" + nombre + "' añadido con éxito.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(),
                            "Error al añadir producto: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }

    /** Muestra un AlertDialog con Spinner para seleccionar alumno y cinturón */
    private void mostrarDialogCinturones() {
        if (alumnoNombres == null || alumnoNombres.isEmpty()) {
            if (alumnoIds.isEmpty() && alumnoNombres.isEmpty()) {
                Toast.makeText(requireContext(), "Cargando lista de alumnos, por favor espere.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No hay alumnos para gestionar.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Spinner de alumnos
        Spinner sAlumnos = new Spinner(requireContext());
        ArrayAdapter<String> adAl = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                alumnoNombres
        );
        adAl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sAlumnos.setAdapter(adAl);

        // Spinner de cinturones
        String[] cinturones = {"Blanco", "Blanco-Amarillo", "Amarillo", "Amarillo-Naranja", "Naranja",
                "Naranja-Verde", "Verde", "Verde-Azul", "Azul", "Azul-Marrón", "Marrón",
                "Marrón-Negro", "Negro", "Negro 1º Dan", "Negro 2º Dan"};

        Spinner sCint = new Spinner(requireContext());
        ArrayAdapter<String> adC = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cinturones
        );
        adC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCint.setAdapter(adC);

        // Layout
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);
        layout.addView(sAlumnos);
        layout.addView(sCint);

        // Crear diálogo con colores corregidos
        crearDialogoConColoresCorregidos("Gestionar Cinturón", layout, (dialog, which) -> {
            int posAlumno = sAlumnos.getSelectedItemPosition();
            if (posAlumno == AdapterView.INVALID_POSITION || posAlumno >= alumnoIds.size()) {
                Toast.makeText(requireContext(), "Seleccione un alumno válido.", Toast.LENGTH_SHORT).show();
                return;
            }
            String alumnoId = alumnoIds.get(posAlumno);
            String cint = (String) sCint.getSelectedItem();
            db.collection("users")
                    .document(alumnoId)
                    .update("cinturon", cint);
            Toast.makeText(requireContext(),
                    "Cinturón de " + alumnoNombres.get(posAlumno) + " actualizado a " + cint,
                    Toast.LENGTH_SHORT).show();
        });
    }

    /** Muestra un AlertDialog con checkboxes para editar clases de un alumno */
    private void mostrarDialogClases() {
        if (alumnoIds == null || alumnoIds.isEmpty()) {
            if (alumnoIds.isEmpty() && alumnoNombres.isEmpty()) {
                Toast.makeText(requireContext(), "Cargando lista de alumnos, por favor espere.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No hay alumnos para gestionar.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Spinner de alumnos
        Spinner sAlumnos = new Spinner(requireContext());
        ArrayAdapter<String> adAl = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                alumnoNombres
        );
        adAl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sAlumnos.setAdapter(adAl);

        // Clases posibles
        final String[] todasClases = {
                "ChiquiYudo", "YudoInfantil","YudoAlevin", "DefensaCadete", "YudoJuvenil", "DefensaPersonal"
        };

        // Layout de checkboxes
        LinearLayout checkLayout = new LinearLayout(requireContext());
        checkLayout.setOrientation(LinearLayout.VERTICAL);
        checkLayout.setPadding(50, 20, 50, 10);
        List<CheckBox> cajas = new ArrayList<>();
        for (String clase : todasClases) {
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(clase);
            cajas.add(cb);
            checkLayout.addView(cb);
        }

        // Layout total
        LinearLayout contenido = new LinearLayout(requireContext());
        contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.addView(sAlumnos);
        contenido.addView(checkLayout);

        // Listener para cargar clases del alumno
        sAlumnos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == AdapterView.INVALID_POSITION || pos >= alumnoIds.size()) return;
                String aid = alumnoIds.get(pos);
                db.collection("users").document(aid).get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                Object raw = doc.get("clases");
                                List<String> act = raw instanceof List ? (List<String>) raw : new ArrayList<>();
                                for (int i = 0; i < todasClases.length; i++) {
                                    cajas.get(i).setChecked(act.contains(todasClases[i]));
                                }
                            } else {
                                for (CheckBox caja : cajas) {
                                    caja.setChecked(false);
                                }
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al cargar clases.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Crear diálogo con colores corregidos
        crearDialogoConColoresCorregidos("Editar Clases", contenido, (dialog, which) -> {
            int posAlumno = sAlumnos.getSelectedItemPosition();
            if (posAlumno == AdapterView.INVALID_POSITION || posAlumno >= alumnoIds.size()) {
                Toast.makeText(requireContext(), "Seleccione un alumno válido.", Toast.LENGTH_SHORT).show();
                return;
            }
            String alumnoId = alumnoIds.get(posAlumno);
            List<String> sel = new ArrayList<>();
            for (int i = 0; i < cajas.size(); i++) {
                if (cajas.get(i).isChecked()) sel.add(todasClases[i]);
            }
            db.collection("users")
                    .document(alumnoId)
                    .update("clases", sel)
                    .addOnSuccessListener(unused -> Toast.makeText(requireContext(),
                            "Clases actualizadas", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(),
                            "Error al guardar clases", Toast.LENGTH_SHORT).show());
        });
    }

    /** Reemplaza fragment actual manteniendo back stack */
    private void replaceFragment(Fragment f) {
        if (getActivity() == null) return;
        FragmentTransaction ft = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        ft.replace(R.id.main_content, f);
        ft.addToBackStack(null);
        ft.commit();
    }
}