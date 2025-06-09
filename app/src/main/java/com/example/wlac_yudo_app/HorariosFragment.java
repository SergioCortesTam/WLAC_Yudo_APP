package com.example.wlac_yudo_app;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class HorariosFragment extends Fragment {
    // Fragmento para mostrar horarios y formulario de inscripción
    // Campos del formulario
    private TextInputEditText campoNombre, campoFechaNacimiento, campoTelefono, campoEmail, campoObservaciones;
    private MaterialAutoCompleteTextView selectorGrupo, selectorTurno, selectorExperiencia;
    private TableLayout tablaHorarios;
    private MaterialButton btnInscribirse;
    private MaterialCardView cardHorarios;

    // Cards de precios
    private MaterialCardView precio1Card, precio2Card, precio3Card;
    private TextView textoPrecio1, textoPrecio2, textoPrecio3;

    // Mapas de datos
    private final HashMap<String, Integer> grupoIndex = new HashMap<>();
    private final HashMap<String, Integer> grupoAPrecio = new HashMap<>();

    // Arrays de datos
    private final String[] preciosTexto = {"10,00 €", "18,00 €", "20,00 €"};
    private Calendar fechaSeleccionada = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horarios, container, false);

        inicializarVistas(view);
        configurarDatos();
        configurarEventos();
        ponerPreciosClaritos();

        return view;
    }
    // Inicializa elementos de la interfaz
    private void inicializarVistas(View view) {
        // Campos de formulario
        campoNombre = view.findViewById(R.id.campo_nombre);
        campoFechaNacimiento = view.findViewById(R.id.campo_fecha_nacimiento);
        campoTelefono = view.findViewById(R.id.campo_telefono);
        campoEmail = view.findViewById(R.id.campo_email);
        campoObservaciones = view.findViewById(R.id.campo_observaciones);

        // Selectores
        selectorGrupo = view.findViewById(R.id.selector_grupo);
        selectorTurno = view.findViewById(R.id.selector_turno);
        selectorExperiencia = view.findViewById(R.id.selector_experiencia);

        // Otros elementos
        tablaHorarios = view.findViewById(R.id.tabla_horarios);
        btnInscribirse = view.findViewById(R.id.boton_inscribirse);
        cardHorarios = view.findViewById(R.id.card_horarios);

        // Cards de precios
        precio1Card = view.findViewById(R.id.precio_1);
        precio2Card = view.findViewById(R.id.precio_2);
        precio3Card = view.findViewById(R.id.precio_3);

        textoPrecio1 = view.findViewById(R.id.texto_precio_1);
        textoPrecio2 = view.findViewById(R.id.texto_precio_2);
        textoPrecio3 = view.findViewById(R.id.texto_precio_3);
    }

    // Configura datos para los selectores
    private void configurarDatos() {
        String[] grupos = {"Chiqui Judo", "Judo Infantil", "Judo Alevín", "Judo Cadete", "Judo Adultos", "Defensa Personal"};
        String[] turnos = {"Lunes y Miércoles", "Martes y Jueves"};
        String[] experiencias = {"Sin experiencia", "Menos de 1 año", "1-3 años", "Más de 3 años", "Cinturón negro"};

        // Configurar mapas
        for (int i = 0; i < grupos.length; i++) {
            grupoIndex.put(grupos[i], i);
        }

        grupoAPrecio.put("Chiqui Judo", 0);
        grupoAPrecio.put("Judo Infantil", 0);
        grupoAPrecio.put("Judo Alevín", 1);
        grupoAPrecio.put("Judo Cadete", 1);
        grupoAPrecio.put("Judo Adultos", 2);
        grupoAPrecio.put("Defensa Personal", 2);

        // Configurar adaptadores
        ArrayAdapter<String> grupoAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, grupos);
        selectorGrupo.setAdapter(grupoAdapter);

        ArrayAdapter<String> turnoAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, turnos);
        selectorTurno.setAdapter(turnoAdapter);

        ArrayAdapter<String> experienciaAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, experiencias);
        selectorExperiencia.setAdapter(experienciaAdapter);
    }

    private void configurarEventos() {
        // Eventos de click para mostrar dropdowns
        selectorGrupo.setOnClickListener(v -> selectorGrupo.showDropDown());
        selectorTurno.setOnClickListener(v -> selectorTurno.showDropDown());
        selectorExperiencia.setOnClickListener(v -> selectorExperiencia.showDropDown());

        // Eventos de selección
        selectorGrupo.setOnItemClickListener((parent, view, position, id) -> {
            actualizarTabla();
            actualizarPrecioSeleccionado();
        });

        selectorTurno.setOnItemClickListener((parent, view, position, id) -> actualizarTabla());

        // Configurar selector de fecha
        campoFechaNacimiento.setOnClickListener(v -> mostrarSelectorFecha());

        // Botón de inscripción
        btnInscribirse.setOnClickListener(v -> procesarInscripcion());
    }
    // Muestra selector de fecha
    private void mostrarSelectorFecha() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, year, month, dayOfMonth) -> {
                    fechaSeleccionada.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    campoFechaNacimiento.setText(sdf.format(fechaSeleccionada.getTime()));
                },
                fechaSeleccionada.get(Calendar.YEAR),
                fechaSeleccionada.get(Calendar.MONTH),
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    // Actualiza la tabla de horarios
    private void actualizarTabla() {
        if (selectorGrupo.getText().toString().isEmpty() || selectorTurno.getText().toString().isEmpty()) {
            cardHorarios.setVisibility(View.GONE);
            return;
        }

        // Mostrar la card de horarios
        cardHorarios.setVisibility(View.VISIBLE);

        // Limpiar filas existentes (excepto el header) y Generar filas de horarios dinámicamente
        tablaHorarios.removeViews(1, tablaHorarios.getChildCount() - 1);

        int index = grupoIndex.get(selectorGrupo.getText().toString());
        String[] horas = {"16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00"};
        String hora = horas[index];
        String turno = selectorTurno.getText().toString();
        String[] dias = turno.split(" y ");

        for (String dia : dias) {
            TableRow row = new TableRow(getContext());
            row.setPadding(16, 16, 16, 16);

            // Configurar layout parameters para que las columnas se distribuyan bien
            TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            );
            row.setLayoutParams(tableParams);

            // Crear TextView para día con ancho fijo
            TextView diaTV = new TextView(getContext());
            diaTV.setText(dia);
            diaTV.setGravity(android.view.Gravity.CENTER);
            diaTV.setTextColor(getResources().getColor(R.color.text_primary));
            diaTV.setPadding(8, 8, 8, 8);
            TableRow.LayoutParams diaParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            diaTV.setLayoutParams(diaParams);

            // Crear TextView para hora con ancho fijo
            TextView horaTV = new TextView(getContext());
            horaTV.setText(hora);
            horaTV.setGravity(android.view.Gravity.CENTER);
            horaTV.setTextColor(getResources().getColor(R.color.text_primary));
            horaTV.setPadding(8, 8, 8, 8);
            TableRow.LayoutParams horaParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f);
            horaTV.setLayoutParams(horaParams);

            // Crear TextView para grupo con más espacio
            TextView grupoTV = new TextView(getContext());
            grupoTV.setText(selectorGrupo.getText().toString());
            grupoTV.setGravity(android.view.Gravity.CENTER);
            grupoTV.setTextColor(getResources().getColor(R.color.text_primary));
            grupoTV.setPadding(8, 8, 8, 8);
            grupoTV.setSingleLine(false); // Permitir múltiples líneas si es necesario
            TableRow.LayoutParams grupoParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f);
            grupoTV.setLayoutParams(grupoParams);

            row.addView(diaTV);
            row.addView(horaTV);
            row.addView(grupoTV);

            tablaHorarios.addView(row);
        }
    }

    private void ponerPreciosClaritos() {
        int colorClaro = getResources().getColor(R.color.precio_claro);
        int textoClaro = getResources().getColor(R.color.text_secondary);

        precio1Card.setCardBackgroundColor(colorClaro);
        precio2Card.setCardBackgroundColor(colorClaro);
        precio3Card.setCardBackgroundColor(colorClaro);

        textoPrecio1.setTextColor(textoClaro);
        textoPrecio2.setTextColor(textoClaro);
        textoPrecio3.setTextColor(textoClaro);
    }

    private void actualizarPrecioSeleccionado() {
        ponerPreciosClaritos();

        String grupoSeleccionado = selectorGrupo.getText().toString();

        if (!grupoAPrecio.containsKey(grupoSeleccionado)) return;

        int precioSeleccionado = grupoAPrecio.get(grupoSeleccionado);

        int colorOscuro = getResources().getColor(R.color.colorPrimary);
        int textoOscuro = getResources().getColor(R.color.text_white);

        switch (precioSeleccionado) {
            case 0:
                precio1Card.setCardBackgroundColor(colorOscuro);
                textoPrecio1.setTextColor(textoOscuro);
                break;
            case 1:
                precio2Card.setCardBackgroundColor(colorOscuro);
                textoPrecio2.setTextColor(textoOscuro);
                break;
            case 2:
                precio3Card.setCardBackgroundColor(colorOscuro);
                textoPrecio3.setTextColor(textoOscuro);
                break;
        }
    }

    // Valida los campos del formulario
    private boolean validarFormulario() {
        boolean esValido = true;
        StringBuilder errores = new StringBuilder();

        // Validar nombre
        if (campoNombre.getText().toString().trim().isEmpty()) {
            campoNombre.setError("El nombre es obligatorio");
            errores.append("• Nombre requerido\n");
            esValido = false;
        } else if (campoNombre.getText().toString().trim().length() < 2) {
            campoNombre.setError("El nombre debe tener al menos 2 caracteres");
            errores.append("• Nombre muy corto\n");
            esValido = false;
        }

        // Validar fecha de nacimiento
        if (campoFechaNacimiento.getText().toString().trim().isEmpty()) {
            campoFechaNacimiento.setError("La fecha de nacimiento es obligatoria");
            errores.append("• Fecha de nacimiento requerida\n");
            esValido = false;
        }

        // Validar teléfono
        String telefono = campoTelefono.getText().toString().trim();
        if (telefono.isEmpty()) {
            campoTelefono.setError("El teléfono es obligatorio");
            errores.append("• Teléfono requerido\n");
            esValido = false;
        } else if (telefono.length() < 9) {
            campoTelefono.setError("Introduce un teléfono válido");
            errores.append("• Teléfono inválido\n");
            esValido = false;
        }

        // Validar email
        String email = campoEmail.getText().toString().trim();
        if (email.isEmpty()) {
            campoEmail.setError("El email es obligatorio");
            errores.append("• Email requerido\n");
            esValido = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            campoEmail.setError("Introduce un email válido");
            errores.append("• Email inválido\n");
            esValido = false;
        }

        // Validar grupo
        if (selectorGrupo.getText().toString().isEmpty()) {
            selectorGrupo.setError("Selecciona un grupo");
            errores.append("• Grupo requerido\n");
            esValido = false;
        }

        // Validar turno
        if (selectorTurno.getText().toString().isEmpty()) {
            selectorTurno.setError("Selecciona un turno");
            errores.append("• Turno requerido\n");
            esValido = false;
        }

        if (!esValido) {
            mostrarToastError("Por favor, corrige los errores en el formulario");
        }

        return esValido;
    }

    private void procesarInscripcion() {
        if (!validarFormulario()) {
            return;
        }

        mostrarToastExito("Validación exitosa. Preparando email...");

        // Esperar un poco para que el usuario vea el mensaje
        btnInscribirse.postDelayed(this::enviarCorreo, 1000);
    }

    // Envía email con los datos de inscripción
    private void enviarCorreo() {
        String nombre = campoNombre.getText().toString().trim();
        String fechaNacimiento = campoFechaNacimiento.getText().toString().trim();
        String telefono = campoTelefono.getText().toString().trim();
        String email = campoEmail.getText().toString().trim();
        String grupo = selectorGrupo.getText().toString();
        String turno = selectorTurno.getText().toString();
        String experiencia = selectorExperiencia.getText().toString();
        String observaciones = campoObservaciones.getText().toString().trim();

        // Obtener hora del grupo seleccionado
        int index = grupoIndex.get(grupo);
        String[] horas = {"16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00"};
        String hora = horas[index];

        // Obtener precio correspondiente
        String precio = "Precio no disponible";
        if (grupoAPrecio.containsKey(grupo)) {
            int precioIndex = grupoAPrecio.get(grupo);
            precio = preciosTexto[precioIndex];
        }

        // Construir el cuerpo del email
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append("Hola,\n\n");
        cuerpo.append("Me gustaría solicitar la inscripción al Club de Judo WLAC. ");
        cuerpo.append("A continuación, adjunto los datos completos:\n\n");

        cuerpo.append("=== DATOS DEL PARTICIPANTE ===\n");
        cuerpo.append("Nombre completo: ").append(nombre).append("\n");
        cuerpo.append("Fecha de nacimiento: ").append(fechaNacimiento).append("\n");
        cuerpo.append("Teléfono: ").append(telefono).append("\n");
        cuerpo.append("Email: ").append(email).append("\n\n");

        cuerpo.append("=== DATOS DE LA INSCRIPCIÓN ===\n");
        cuerpo.append("Grupo seleccionado: ").append(grupo).append("\n");
        cuerpo.append("Días de entrenamiento: ").append(turno).append("\n");
        cuerpo.append("Horario: ").append(hora).append("\n");
        cuerpo.append("Precio mensual: ").append(precio).append("\n\n");

        if (!experiencia.isEmpty()) {
            cuerpo.append("Experiencia previa: ").append(experiencia).append("\n\n");
        }

        if (!observaciones.isEmpty()) {
            cuerpo.append("=== OBSERVACIONES MÉDICAS ===\n");
            cuerpo.append(observaciones).append("\n\n");
        }

        cuerpo.append("Por favor, háganme llegar la información necesaria para ");
        cuerpo.append("formalizar la inscripción y los documentos requeridos.\n\n");
        cuerpo.append("Quedo a la espera de su respuesta.\n\n");
        cuerpo.append("Saludos cordiales.");

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"o0osergio0o@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "✉️ Solicitud de inscripción al Club de Judo WLAC - " + nombre);
        emailIntent.putExtra(Intent.EXTRA_TEXT, cuerpo.toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar solicitud de inscripción"));
            mostrarToastExito("¡Email preparado! Selecciona tu aplicación de correo.");
        } catch (android.content.ActivityNotFoundException ex) {
            mostrarToastError("No se encontró una aplicación de correo instalada");
        }
    }

    // Métodos mejorados para mostrar mensajes con Toast elegante
    private void mostrarToastExito(String mensaje) {
        Toast toast = Toast.makeText(getActivity(), "✅ " + mensaje, Toast.LENGTH_LONG);
        toast.show();
    }

    private void mostrarToastError(String mensaje) {
        Toast toast = Toast.makeText(getActivity(), "❌ " + mensaje, Toast.LENGTH_LONG);
        toast.show();
    }
}