package com.example.wlac_yudo_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;

public class HorariosFragment extends Fragment {

    private MaterialAutoCompleteTextView selectorHorarios;
    private Button btnContactar;

    public HorariosFragment() {
        // Constructor vacío obligatorio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_horarios, container, false);

        // Inicializar las vistas
        selectorHorarios = view.findViewById(R.id.selector_horarios);
        btnContactar = view.findViewById(R.id.boton_contactar);

        // Lista de horarios
        ArrayList<String> horariosList = new ArrayList<>();
        horariosList.add("Lunes y Miércoles: 17:00 - 21:00");
        horariosList.add("Martes y Jueves: 16:30 - 18:30");

        // Adaptador para MaterialAutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                horariosList);
        selectorHorarios.setAdapter(adapter);

        // Mostrar el primer horario por defecto sin abrir el dropdown
        selectorHorarios.setText(horariosList.get(0), false);

        // Configurar botón de contacto por email
        btnContactar.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", "contacto@tuclub.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre inscripciones");

            if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
            } else {
                Toast.makeText(getActivity(), "No hay ninguna aplicación de correo instalada", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
