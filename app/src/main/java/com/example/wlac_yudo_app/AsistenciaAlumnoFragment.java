package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Objects;

public class AsistenciaAlumnoFragment extends Fragment {

    private FirebaseFirestore db;
    private LinearLayout layoutAsistencias;

    public AsistenciaAlumnoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_asistencia_alumno, container, false);

        layoutAsistencias = view.findViewById(R.id.layout_asistencias);
        db = FirebaseFirestore.getInstance();

        cargarAsistencias();

        return view;
    }

    private void cargarAsistencias() {
        String alumnoId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        db.collection("Asistencias")
                .whereEqualTo("alumnoId", alumnoId)
                .whereEqualTo("asistio", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String fecha = doc.getString("fecha");
                        String clase = doc.getString("clase");

                        TextView txt = new TextView(getContext());
                        txt.setText("✔ " + fecha + " - " + clase);
                        txt.setTextSize(16);
                        layoutAsistencias.addView(txt);
                    }
                });
    }
}
