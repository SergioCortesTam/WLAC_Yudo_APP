package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AlumnoFragment extends Fragment {

    private TextView tvNombre, tvCinturon, tvClases;
    private Button btnVerAsistencia;
    private FirebaseFirestore db;

    public AlumnoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno, container, false);

        tvNombre = view.findViewById(R.id.tv_alumno_nombre);
        tvCinturon = view.findViewById(R.id.tv_alumno_cinturon);
        tvClases = view.findViewById(R.id.tv_clases_alumno);
        btnVerAsistencia = view.findViewById(R.id.btn_ver_asistencia); // asegúrate de tenerlo en tu XML

        db = FirebaseFirestore.getInstance();

        String email = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getEmail() : null;

        if (email != null) {
            db.collection("Users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            String nombre = doc.getString("nombre");
                            String cinturon = doc.getString("cinturon");
                            List<String> clases = (List<String>) doc.get("clases");

                            tvNombre.setText("Nombre: " + (nombre != null ? nombre : "Desconocido"));
                            tvCinturon.setText("Cinturón: " + (cinturon != null ? cinturon : "No asignado"));

                            if (clases != null && !clases.isEmpty()) {
                                StringBuilder clasesTexto = new StringBuilder("Clases:\n");
                                for (String clase : clases) {
                                    clasesTexto.append("• ").append(clase).append("\n");
                                }
                                tvClases.setText(clasesTexto.toString());
                            } else {
                                tvClases.setText("Clases: No asignadas aún");
                            }
                        } else {
                            tvNombre.setText("Usuario no encontrado.");
                        }
                    })
                    .addOnFailureListener(e -> tvNombre.setText("Error al cargar los datos."));
        } else {
            tvNombre.setText("Usuario no autenticado.");
        }

        btnVerAsistencia.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new AsistenciaFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
