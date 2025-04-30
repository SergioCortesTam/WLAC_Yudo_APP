package com.example.wlac_yudo_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class HorariosFragment extends Fragment {

    private Spinner spinnerHorarios;
    private Button btnContactar;

    public HorariosFragment() {
        // Constructor vacío obligatorio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_horarios, container, false);

        // Inicializar el Spinner y el botón de contacto
        spinnerHorarios = view.findViewById(R.id.selector_horarios);
        btnContactar = view.findViewById(R.id.boton_contactar);

        // Crear un ArrayList con los horarios directamente en el código
        ArrayList<String> horariosList = new ArrayList<>();
        horariosList.add("Lunes y Miércoles: 17:00 - 21:00");
        horariosList.add("Martes y Jueves: 16:30 - 18:30");

        // Crear un ArrayAdapter y asignarlo al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, horariosList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHorarios.setAdapter(adapter);

        // Configurar el botón de contacto
        btnContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para abrir la aplicación de correo
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "contacto@tuclub.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre inscripciones");

                // Verificar si hay alguna aplicación de correo disponible
                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
                } else {
                    // Mostrar un mensaje si no hay ninguna aplicación de correo instalada
                    Toast.makeText(getActivity(), "No hay ninguna aplicación de correo instalada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
