package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfesorFragment extends Fragment {

    private Button btnGestionarCinturones, btnVerClases, btnMarcarAsistencia;

    public ProfesorFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profesor, container, false);

        btnGestionarCinturones = view.findViewById(R.id.btn_gestionar_cinturones);
        btnVerClases = view.findViewById(R.id.btn_ver_clases);
        btnMarcarAsistencia = view.findViewById(R.id.btn_marcar_asistencia);

        btnGestionarCinturones.setOnClickListener(v -> {
            // Aquí iría la lógica para gestionar los cinturones
        });

        btnVerClases.setOnClickListener(v -> {
            // Aquí iría la lógica para ver las clases de los alumnos
        });

        btnMarcarAsistencia.setOnClickListener(v -> {
            // Aquí iría la lógica para marcar la asistencia
        });

        return view;
    }
}
