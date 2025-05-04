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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AsistenciaFragment extends Fragment {

    private Button btnSeleccionarFecha;
    private TextView tvFecha, tvDetalle;
    private FirebaseFirestore db;

    public AsistenciaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asistencia, container, false);

        btnSeleccionarFecha = view.findViewById(R.id.btn_seleccionar_fecha);
        tvFecha             = view.findViewById(R.id.tv_fecha_seleccionada);
        tvDetalle           = view.findViewById(R.id.tv_detalle_asistencia);
        db = FirebaseFirestore.getInstance();

        btnSeleccionarFecha.setOnClickListener(v -> {
            // 1) Construye y muestra el selector de fecha
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecciona una fecha")
                    .build();
            picker.show(getChildFragmentManager(), "DATE_PICKER");

            // 2) Cuando el usuario confirma:
            picker.addOnPositiveButtonClickListener(selection -> {
                // convertir timestamp a "yyyy-MM-dd"
                String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date(selection));
                tvFecha.setText("Fecha: " + fecha);

                // 3) Cargar la asistencia para esa fecha
                cargarAsistencia(fecha);
            });
        });

        return view;
    }

    private void cargarAsistencia(String fecha) {
        db.collection("Asistencias")
                .whereEqualTo("fecha", fecha)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        tvDetalle.setText("No hay registros de asistencia para " + fecha);
                    } else {
                        StringBuilder sb = new StringBuilder("Asistencia:\n");
                        for (QueryDocumentSnapshot doc : snapshot) {
                            String alumnoId = doc.getString("alumnoId");
                            Boolean asistio = doc.getBoolean("asistio");
                            sb.append(alumnoId)
                                    .append(": ")
                                    .append(asistio != null && asistio ? "✅" : "❌")
                                    .append("\n");
                        }
                        tvDetalle.setText(sb.toString());
                    }
                })
                .addOnFailureListener(e ->
                        tvDetalle.setText("Error al cargar asistencia: " + e.getMessage())
                );
    }
}
