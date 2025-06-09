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
    // Fragmento para que los profesores marquen asistencia de alumnos

    private FirebaseFirestore db;
    private LinearLayout layoutAlumnos; // Contenedor para lista de alumnos
    private TextView txtFecha; // Muestra fecha seleccionada
    private Calendar fechaActual; // Fecha que se está visualizando
    private Button btnGuardar; // Botón para guardar asistencias

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marcar_asistencia, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener referencias de vistas
        layoutAlumnos = view.findViewById(R.id.layout_alumno);
        txtFecha = view.findViewById(R.id.txt_fecha_asistencia);
        Button btnAnterior = view.findViewById(R.id.btn_fecha_anterior);
        Button btnSiguiente = view.findViewById(R.id.btn_fecha_siguiente);
        btnGuardar = view.findViewById(R.id.btn_guardar_asistencias);

        // Configurar fecha actual
        fechaActual = Calendar.getInstance();
        actualizarFecha(); // Cargar datos iniciales

        // Botones para navegar entre fechas
        btnAnterior.setOnClickListener(v -> {
            fechaActual.add(Calendar.DAY_OF_MONTH, -1);
            actualizarFecha();
        });

        btnSiguiente.setOnClickListener(v -> {
            fechaActual.add(Calendar.DAY_OF_MONTH, 1);
            actualizarFecha();
        });

        // Guardar asistencias marcadas
        btnGuardar.setOnClickListener(v -> guardarTodasAsistencias());

        return view;
    }

    // Actualiza la fecha mostrada y carga los alumnos
    private void actualizarFecha() {
        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(fechaActual.getTime());
        txtFecha.setText(fechaStr);
        cargarAlumnosYAsistencias(fechaStr); // Cargar datos para esta fecha
    }

    // Carga alumnos y sus estados de asistencia
    private void cargarAlumnosYAsistencias(String fechaStr) {
        layoutAlumnos.removeAllViews(); // Limpiar vista anterior

        // Consultar todos los alumnos
        db.collection("users")
                .whereEqualTo("role", "alumno")
                .get()
                .addOnSuccessListener(alumnosSnapshot -> {
                    for (QueryDocumentSnapshot doc : alumnosSnapshot) {
                        String alumnoId = doc.getId();
                        String nombre = doc.getString("nombre");

                        // Crear vista para cada alumno
                        View alumnoView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_asistencia_alumno, layoutAlumnos, false);

                        TextView txtNombre = alumnoView.findViewById(R.id.txt_nombre_alumno);
                        CheckBox chkAsistio = alumnoView.findViewById(R.id.chk_asistio);

                        txtNombre.setText(nombre);
                        alumnoView.setTag(alumnoId); // Guardar ID para referencia

                        // Cargar estado de asistencia previo (si existe)
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

    // Guarda todas las asistencias marcadas
    private void guardarTodasAsistencias() {
        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(fechaActual.getTime());

        // Usar batch para múltiples escrituras eficientes
        WriteBatch batch = db.batch();

        // Recorrer todos los alumnos en la vista
        for (int i = 0; i < layoutAlumnos.getChildCount(); i++) {
            View alumnoView = layoutAlumnos.getChildAt(i);
            String alumnoId = (String) alumnoView.getTag();
            CheckBox chk = alumnoView.findViewById(R.id.chk_asistio);

            // Preparar datos de asistencia
            HashMap<String, Object> data = new HashMap<>();
            data.put("alumnoId", alumnoId);
            data.put("nombreAlumno", ((TextView) alumnoView.findViewById(R.id.txt_nombre_alumno)).getText().toString());
            data.put("fecha", fechaStr);
            data.put("asistio", chk.isChecked());
            data.put("clase", "Clase General");

            // Añadir operación al batch
            String docId = alumnoId + "_" + fechaStr;
            batch.set(db.collection("Asistencias").document(docId), data, SetOptions.merge());
        }

        // Ejecutar todas las operaciones
        batch.commit()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Asistencias guardadas", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_LONG).show()
                );
    }
}