package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarcarAsistenciaFragment extends Fragment {

    private FirebaseFirestore db;
    private LinearLayout layoutAlumnos;

    public MarcarAsistenciaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marcar_asistencia, container, false);

        layoutAlumnos = view.findViewById(R.id.layout_alumnos);
        db = FirebaseFirestore.getInstance();

        cargarAlumnos();

        return view;
    }

    private void cargarAlumnos() {
        db.collection("Users")
                .whereEqualTo("rol", "alumno")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        String id = doc.getId();

                        View alumnoView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_asistencia_alumno, layoutAlumnos, false);

                        TextView txtNombre = alumnoView.findViewById(R.id.txt_nombre_alumno);
                        CheckBox chkAsistio = alumnoView.findViewById(R.id.chk_asistio);
                        Button btnGuardar = alumnoView.findViewById(R.id.btn_guardar_asistencia);

                        txtNombre.setText(nombre);

                        btnGuardar.setOnClickListener(v -> {
                            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            Asistencia asistencia = new Asistencia(id, fecha, "Clase General", chkAsistio.isChecked());

                            db.collection("Asistencias")
                                    .add(asistencia)
                                    .addOnSuccessListener(ref -> {
                                        btnGuardar.setText("✓ Guardado");
                                        btnGuardar.setEnabled(false);
                                    })
                                    .addOnFailureListener(e -> {
                                        btnGuardar.setText("Error");
                                    });
                        });

                        layoutAlumnos.addView(alumnoView);
                    }
                });
    }
}
