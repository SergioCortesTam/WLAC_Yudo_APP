package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MarcarAsistenciaFragment extends Fragment {

    private FirebaseFirestore db;
    private LinearLayout layoutAlumnos;
    private TextView txtFecha;
    private Calendar fechaActual;
    private Button btnGuardar;

    public MarcarAsistenciaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marcar_asistencia, container, false);

        db = FirebaseFirestore.getInstance();
        // Usamos el ID correcto según el XML - layout_alumno
        layoutAlumnos = view.findViewById(R.id.layout_alumno);
        txtFecha = view.findViewById(R.id.txt_fecha_asistencia);
        Button btnAnterior = view.findViewById(R.id.btn_fecha_anterior);
        Button btnSiguiente = view.findViewById(R.id.btn_fecha_siguiente);
        btnGuardar = view.findViewById(R.id.btn_guardar_asistencias);

        fechaActual = Calendar.getInstance();
        actualizarFecha();

        btnAnterior.setOnClickListener(v -> {
            fechaActual.add(Calendar.DAY_OF_MONTH, -1);
            actualizarFecha();
        });
        btnSiguiente.setOnClickListener(v -> {
            fechaActual.add(Calendar.DAY_OF_MONTH, 1);
            actualizarFecha();
        });

        btnGuardar.setOnClickListener(v -> guardarTodasAsistencias());

        return view;
    }

    private void actualizarFecha() {
        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(fechaActual.getTime());
        txtFecha.setText("Fecha: " + fechaStr);
        cargarAlumnosYAsistencias(fechaStr);
    }

    private void cargarAlumnosYAsistencias(String fechaStr) {
        layoutAlumnos.removeAllViews();

        db.collection("users")
                .whereEqualTo("role", "alumno")
                .get()
                .addOnSuccessListener(alumnosSnapshot -> {
                    for (QueryDocumentSnapshot doc : alumnosSnapshot) {
                        String alumnoId = doc.getId();
                        String nombre = doc.getString("nombre");

                        // Inflamos el item
                        View alumnoView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_asistencia_alumno, layoutAlumnos, false);

                        TextView txtNombre = alumnoView.findViewById(R.id.txt_nombre_alumno);
                        CheckBox chkAsistio = alumnoView.findViewById(R.id.chk_asistio);

                        txtNombre.setText(nombre);
                        alumnoView.setTag(alumnoId);  // guardamos el ID para luego

                        // Carga estado previo
                        String docId = alumnoId + "_" + fechaStr;
                        db.collection("Asistencias").document(docId)
                                .get()
                                .addOnSuccessListener(asistenciaDoc -> {
                                    if (asistenciaDoc.exists()) {
                                        Boolean asistio = asistenciaDoc.getBoolean("asistio");
                                        chkAsistio.setChecked(asistio != null && asistio);
                                    }
                                });

                        layoutAlumnos.addView(alumnoView);
                    }
                });
    }

    private void guardarTodasAsistencias() {
        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(fechaActual.getTime());

        WriteBatch batch = db.batch();
        int childCount = layoutAlumnos.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View alumnoView = layoutAlumnos.getChildAt(i);
            String alumnoId = (String) alumnoView.getTag();
            TextView txtNombre = alumnoView.findViewById(R.id.txt_nombre_alumno);
            CheckBox chk = alumnoView.findViewById(R.id.chk_asistio);

            // Preparamos datos
            HashMap<String, Object> data = new HashMap<>();
            data.put("alumnoId", alumnoId);
            data.put("nombreAlumno", txtNombre.getText().toString());
            data.put("fecha", fechaStr);
            data.put("asistio", chk.isChecked());
            data.put("clase", "Clase General");

            // Referencia al doc
            String docId = alumnoId + "_" + fechaStr;
            batch.set(db.collection("Asistencias").document(docId), data, SetOptions.merge());
        }

        // Ejecutamos batch
        batch.commit()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Asistencias guardadas", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al guardar asistencias", Toast.LENGTH_LONG).show()
                );
    }
}