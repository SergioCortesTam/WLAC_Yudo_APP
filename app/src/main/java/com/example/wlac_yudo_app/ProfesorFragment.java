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

    private Button btnGestionarCinturones;
    private Button btnVerClases;
    private Button btnMarcarAsistencia;

    public ProfesorFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profesor, container, false);

        // Enlazamos los botones
        btnGestionarCinturones = view.findViewById(R.id.btn_gestionar_cinturones);
        btnVerClases           = view.findViewById(R.id.btn_ver_clases);
        btnMarcarAsistencia    = view.findViewById(R.id.btn_marcar_asistencia);

        // Lógica para gestionar cinturones
        btnGestionarCinturones.setOnClickListener(v -> {
            // TODO: implementar gestión de cinturones
        });

        // Lógica para ver clases de los alumnos
        btnVerClases.setOnClickListener(v -> {
            // TODO: implementar visualización de clases
        });

        // Navegación a MarcarAsistenciaFragment
        btnMarcarAsistencia.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new MarcarAsistenciaFragment())
                    .addToBackStack(null)  // Permite volver atrás
                    .commit();
        });

        return view;
    }
}
