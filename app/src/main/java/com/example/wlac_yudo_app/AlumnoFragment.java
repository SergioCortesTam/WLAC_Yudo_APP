package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class AlumnoFragment extends Fragment {

    private TextView tvNombre, tvCinturon, tvClases;

    public AlumnoFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno, container, false);

        tvNombre = view.findViewById(R.id.tv_alumno_nombre);
        tvCinturon = view.findViewById(R.id.tv_alumno_cinturon);
        tvClases = view.findViewById(R.id.tv_clases_alumno);

        // Cargar la información del alumno desde Firebase
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        tvNombre.setText("Nombre: " + email);
        tvCinturon.setText("Cinturón: Azul");
        tvClases.setText("Clases: [Clase 1, Clase 2, Clase 3]");

        return view;
    }
}
