package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// VerAsistenciasAlumnoFragment.java
public class VerAsistenciasAlumnoFragment extends Fragment {
    // Fragmento para que alumnos vean su historial de asistencias

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout layoutAsistencias;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ver_asistencias_alumno, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        layoutAsistencias = view.findViewById(R.id.layoutAsistencias);

        cargarAsistencias();
        return view;
    }

    // Cargar asistencias del alumno actual
    private void cargarAsistencias() {
        String email = mAuth.getCurrentUser().getEmail();

        // Buscar ID del alumno por email
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(userSnap -> {
                    if (!userSnap.isEmpty()) {
                        String alumnoId = userSnap.getDocuments().get(0).getId();

                        // Obtener asistencias de este alumno
                        db.collection("Asistencias")
                                .whereEqualTo("alumnoId", alumnoId)
                                .get()
                                .addOnSuccessListener(asistenciasSnap -> {
                                    layoutAsistencias.removeAllViews();
                                    for (QueryDocumentSnapshot doc : asistenciasSnap) {
                                        // Crear vista para cada asistencia
                                        View itemView = LayoutInflater.from(getContext())
                                                .inflate(R.layout.item_asistencia_registro, layoutAsistencias, false);

                                        TextView txtFecha = itemView.findViewById(R.id.txt_fecha_asistencia);
                                        TextView txtEstado = itemView.findViewById(R.id.txt_estado_asistencia);

                                        txtFecha.setText(doc.getString("fecha"));
                                        txtEstado.setText(doc.getBoolean("asistio") ? "✅ Asistió" : "❌ Faltó");

                                        layoutAsistencias.addView(itemView);
                                    }
                                });
                    }
                });
    }
}