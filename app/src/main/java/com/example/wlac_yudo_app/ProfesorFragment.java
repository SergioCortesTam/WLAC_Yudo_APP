package com.example.wlac_yudo_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.*;

import java.util.*;

public class ProfesorFragment extends Fragment {

    private MaterialCardView cardGestionarCinturones, cardVerClases, cardMarcarAsistencia;
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

        // Nueva vinculación con las tarjetas del layout
        cardGestionarCinturones = view.findViewById(R.id.card_gestionar_cinturones);
        cardVerClases           = view.findViewById(R.id.card_ver_clases);
        cardMarcarAsistencia    = view.findViewById(R.id.card_marcar_asistencia);

        // Cargar alumnos
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
                });

        // Listeners para cada tarjeta
        cardGestionarCinturones.setOnClickListener(v -> mostrarDialogCinturones());
        cardVerClases         .setOnClickListener(v -> mostrarDialogClases());
        cardMarcarAsistencia  .setOnClickListener(v -> replaceFragment(new MarcarAsistenciaFragment()));

        return view;
    }



    /** Muestra un AlertDialog con Spinner para seleccionar alumno y cinturón */
    private void mostrarDialogCinturones() {
        // Spinner de alumnos
        Spinner sAlumnos = new Spinner(requireContext());
        ArrayAdapter<String> adAl = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                alumnoNombres
        );
        adAl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sAlumnos.setAdapter(adAl);

        // Spinner de cinturones fijos
        String[] cinturones = {
                "Blanco","Amarillo","Naranja","Verde","Azul","Marrón","Negro"
        };
        Spinner sCint = new Spinner(requireContext());
        ArrayAdapter<String> adC = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cinturones
        );
        adC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCint.setAdapter(adC);

        // Layout vertical para el diálogo
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);
        layout.addView(sAlumnos);
        layout.addView(sCint);

        new AlertDialog.Builder(requireContext())
                .setTitle("Gestionar Cinturón")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    int posAlumno = sAlumnos.getSelectedItemPosition();
                    String alumnoId = alumnoIds.get(posAlumno);
                    String cint = (String) sCint.getSelectedItem();
                    db.collection("users")
                            .document(alumnoId)
                            .update("cinturon", cint);
                    Toast.makeText(requireContext(),
                            "Cinturón de " + alumnoNombres.get(posAlumno) +
                                    " actualizado a " + cint,
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /** Muestra un AlertDialog con checkboxes para editar clases de un alumno */
    private void mostrarDialogClases() {
        if (alumnoIds == null || alumnoIds.isEmpty()) {
            Toast.makeText(requireContext(), "Espera a que carguen los alumnos.", Toast.LENGTH_SHORT).show();
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
                "ChiquiYudo", "YudoInfantil", "YudoJuvenil", "DefensaPersonal"
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

        // Listener para cargar clases del alumno
        sAlumnos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String aid = alumnoIds.get(pos);
                db.collection("users").document(aid).get()
                        .addOnSuccessListener(doc -> {
                            Object raw = doc.get("clases");
                            List<String> act = raw instanceof List ? (List<String>) raw : new ArrayList<>();
                            for (int i = 0; i < todasClases.length; i++) {
                                cajas.get(i).setChecked(act.contains(todasClases[i]));
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al cargar clases.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Layout total
        LinearLayout contenido = new LinearLayout(requireContext());
        contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.addView(sAlumnos);
        contenido.addView(checkLayout);

        // Mostrar el diálogo
        new AlertDialog.Builder(requireContext())
                .setTitle("Editar Clases")
                .setView(contenido)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    int posAlumno = sAlumnos.getSelectedItemPosition();
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
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    /** Reemplaza fragment actual manteniendo back stack */
    private void replaceFragment(Fragment f) {
        FragmentTransaction ft = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        ft.replace(R.id.main_content, f);
        ft.addToBackStack(null);
        ft.commit();
    }
}
