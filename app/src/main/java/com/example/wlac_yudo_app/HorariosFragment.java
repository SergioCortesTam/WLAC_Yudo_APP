package com.example.wlac_yudo_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.HashMap;

public class HorariosFragment extends Fragment {

    private MaterialAutoCompleteTextView selectorGrupo, selectorTurno;
    private TableLayout tablaHorarios;
    private MaterialButton btnContactar;

    private final String[] grupos = {
            "Chiqui Judo", "Judo Infantil", "Judo Alevín", "Judo Cadete", "Judo Adultos", "Defensa Personal"
    };

    private final String[] turnos = {
            "Lunes y Miércoles", "Martes y Jueves"
    };

    private final String[] horas = {
            "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00",
            "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00"
    };

    private final HashMap<String, Integer> grupoIndex = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horarios, container, false);

        selectorGrupo = view.findViewById(R.id.selector_grupo);
        selectorTurno = view.findViewById(R.id.selector_turno);
        tablaHorarios = view.findViewById(R.id.tabla_horarios);
        btnContactar = view.findViewById(R.id.boton_contactar);

        for (int i = 0; i < grupos.length; i++) {
            grupoIndex.put(grupos[i], i);
        }

        ArrayAdapter<String> grupoAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, grupos);
        selectorGrupo.setAdapter(grupoAdapter);

        ArrayAdapter<String> turnoAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, turnos);
        selectorTurno.setAdapter(turnoAdapter);

        // Mostrar el menú desplegable al hacer clic
        selectorGrupo.setOnClickListener(v -> selectorGrupo.showDropDown());
        selectorTurno.setOnClickListener(v -> selectorTurno.showDropDown());

        selectorGrupo.setOnItemClickListener((parent, view1, position, id) -> actualizarTabla());
        selectorTurno.setOnItemClickListener((parent, view1, position, id) -> actualizarTabla());

        btnContactar.setOnClickListener(v -> enviarCorreo());

        return view;
    }

    private void actualizarTabla() {
        if (selectorGrupo.getText().toString().isEmpty() || selectorTurno.getText().toString().isEmpty())
            return;

        tablaHorarios.removeViews(1, tablaHorarios.getChildCount() - 1);

        int index = grupoIndex.get(selectorGrupo.getText().toString());
        String hora = horas[index];
        String turno = selectorTurno.getText().toString();

        String[] dias = turno.split(" y ");

        for (String dia : dias) {
            TableRow row = new TableRow(getContext());
            row.setPadding(16, 16, 16, 16);

            TextView diaTV = new TextView(getContext());
            diaTV.setText(dia);

            TextView horaTV = new TextView(getContext());
            horaTV.setText(hora);

            TextView grupoTV = new TextView(getContext());
            grupoTV.setText(grupos[index]);

            row.addView(diaTV);
            row.addView(horaTV);
            row.addView(grupoTV);

            tablaHorarios.addView(row);
        }
    }

    private void enviarCorreo() {
        String grupo = selectorGrupo.getText().toString();
        String turno = selectorTurno.getText().toString();

        if (grupo.isEmpty() || turno.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor selecciona grupo y turno", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = grupoIndex.get(grupo);
        String hora = horas[index];

        String cuerpo = "Hola,\n\nMe gustaría inscribir a mi hijo/a al club de Judo. A continuación, adjunto los datos para la inscripción:\n\n"
                + "Nombre del niño/a: [Poner aquí el nombre del niño/a]\n"
                + "Año de nacimiento: [Poner aquí el año de nacimiento]\n"
                + "Grupo seleccionado: " + grupo + "\n"
                + "Días: " + turno + "\n"
                + "Hora: " + hora + "\n\n"
                + "Por favor, háganme llegar la información necesaria para formalizar la inscripción.\n\n"
                + "Gracias de antemano.\n\n[Nombre del padre/madre/tutor]";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"o0osergio0o@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Solicitud de inscripción al Club de Judo");
        emailIntent.putExtra(Intent.EXTRA_TEXT, cuerpo);

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No se encontró una aplicación de correo", Toast.LENGTH_SHORT).show();
        }
    }

}
