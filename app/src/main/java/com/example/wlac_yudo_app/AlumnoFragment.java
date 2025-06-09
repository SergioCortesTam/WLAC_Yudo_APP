package com.example.wlac_yudo_app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlumnoFragment extends Fragment {
    // Campos de texto para mostrar información del alumno
    private TextView tvNombre, tvCinturon, tvClases, tvFechaRegistro, tvAsistenciasTotal, tvAsistenciasMes;
    private Button btnVerAsistencia;
    private View viewColorCinturon; // Vista para mostrar color del cinturón
    private LinearLayout headerLayout; // Layout principal del encabezado
    private FirebaseFirestore db; // Instancia de Firestore

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno, container, false);

        // Obtener referencias a los elementos de la interfaz
        tvNombre = view.findViewById(R.id.tv_alumno_nombre);
        tvCinturon = view.findViewById(R.id.tv_alumno_cinturon);
        tvClases = view.findViewById(R.id.tv_clases_alumno);
        tvFechaRegistro = view.findViewById(R.id.tv_fecha_registro);
        tvAsistenciasTotal = view.findViewById(R.id.tv_asistencias_total);
        tvAsistenciasMes = view.findViewById(R.id.tv_asistencias_mes);
        btnVerAsistencia = view.findViewById(R.id.btn_ver_asistencia);
        viewColorCinturon = view.findViewById(R.id.view_color_cinturon);
        headerLayout = view.findViewById(R.id.header_layout);

        db = FirebaseFirestore.getInstance();

        // Valores iniciales para las asistencias
        tvAsistenciasTotal.setText("0");
        tvAsistenciasMes.setText("0");

        // Obtener email del usuario actual
        String email = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : null;

        if (email != null) {
            // Mostrar estado de carga mientras se obtienen datos
            tvNombre.setText("Cargando...");

            // Consultar datos del usuario en Firestore
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                            // Configurar nombre
                            String nombre = doc.getString("nombre");
                            tvNombre.setText(nombre != null ? nombre : "Nombre no disponible");

                            // Configurar cinturón y su color
                            String cinturon = doc.getString("cinturon");
                            if (cinturon != null) {
                                tvCinturon.setText(cinturon);
                                int colorCinturon = getColorForCinturon(cinturon);
                                setBeltColor(colorCinturon);
                                setHeaderColor(colorCinturon);
                            } else {
                                tvCinturon.setText("No asignado");
                                setBeltColor(Color.GRAY);
                                setHeaderColor(Color.parseColor("#667eea"));
                            }

                            // Configurar clases asignadas
                            Object clasesField = doc.get("clases");
                            if (clasesField instanceof List) {
                                List<String> clases = (List<String>) clasesField;
                                if (!clases.isEmpty()) {
                                    String clasesStr = android.text.TextUtils.join(" • ", clases);
                                    tvClases.setText(clasesStr);
                                } else {
                                    tvClases.setText("No hay clases asignadas");
                                }
                            } else {
                                tvClases.setText("No hay clases asignadas");
                            }

                            // Configurar fecha de registro
                            com.google.firebase.Timestamp fecha = doc.getTimestamp("fecha_registro");
                            if (fecha != null) {
                                Date date = fecha.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", new Locale("es", "ES"));
                                tvFechaRegistro.setText(sdf.format(date));
                            } else {
                                tvFechaRegistro.setText("N/A");
                            }

                            // Cargar datos de asistencias
                            String alumnoId = doc.getId();
                            cargarAsistencias(alumnoId);

                            // Animar elementos de la interfaz
                            animateContent();

                        } else {
                            tvNombre.setText("Usuario no encontrado");
                        }
                    })
                    .addOnFailureListener(e -> tvNombre.setText("Error al cargar datos"));
        } else {
            tvNombre.setText("Usuario no autenticado");
        }

        // Configurar acción del botón "Ver Asistencia"
        btnVerAsistencia.setOnClickListener(v -> {
            // Animación de pulsación
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();

                        // Navegar al fragmento de asistencias
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_content, new VerAsistenciasAlumnoFragment())
                                .addToBackStack(null)
                                .commit();
                    })
                    .start();
        });

        return view;
    }

    // Establece el color del cinturón como círculo
    private void setBeltColor(int color) {
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(color);
        circle.setStroke(2, Color.WHITE);
        viewColorCinturon.setBackground(circle);
    }

    // Establece color degradado para el encabezado
    private void setHeaderColor(int color) {
        int[] colors = {
                adjustColorBrightness(color, 1.1f),
                adjustColorBrightness(color, 0.8f)
        };

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);

        headerLayout.setBackground(gradient);
    }

    // Ajusta el brillo de un color
    private int adjustColorBrightness(int color, float factor) {
        int red = Math.round(Color.red(color) * factor);
        int green = Math.round(Color.green(color) * factor);
        int blue = Math.round(Color.blue(color) * factor);

        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        return Color.rgb(red, green, blue);
    }

    // Devuelve color correspondiente a cada cinturón
    private int getColorForCinturon(String cinturon) {
        switch (cinturon.toLowerCase(Locale.ROOT)) {
            case "blanco": return Color.parseColor("#E8E8E8");
            case "amarillo": return Color.parseColor("#FFD600");
            case "naranja": return Color.parseColor("#FF6D00");
            case "verde": return Color.parseColor("#4CAF50");
            case "azul": return Color.parseColor("#2196F3");
            case "marrón": case "marron": return Color.parseColor("#8D6E63");
            case "negro": return Color.parseColor("#424242");
            case "rojo": return Color.parseColor("#F44336");
            default: return Color.parseColor("#9E9E9E");
        }
    }

    // Carga las asistencias desde Firestore
    private void cargarAsistencias(String alumnoId) {
        // Asistencias totales
        db.collection("asistencias")
                .whereEqualTo("alumnoId", alumnoId)
                .whereEqualTo("asistio", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    int total = snapshot.size();
                    animateCounter(tvAsistenciasTotal, total);
                })
                .addOnFailureListener(e -> tvAsistenciasTotal.setText("-"));

        // Asistencias del mes actual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String inicioMes = sdf.format(cal.getTime());

        cal.add(Calendar.MONTH, 1);
        String inicioMesSiguiente = sdf.format(cal.getTime());

        db.collection("asistencias")
                .whereEqualTo("alumnoId", alumnoId)
                .whereEqualTo("asistio", true)
                .whereGreaterThanOrEqualTo("fecha", inicioMes)
                .whereLessThan("fecha", inicioMesSiguiente)
                .get()
                .addOnSuccessListener(snapshot -> {
                    int countMes = snapshot.size();
                    animateCounter(tvAsistenciasMes, countMes);
                })
                .addOnFailureListener(e -> tvAsistenciasMes.setText("-"));
    }

    // Anima el contador de asistencias
    private void animateCounter(TextView textView, int targetValue) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetValue);
        animator.setDuration(800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            textView.setText(String.valueOf(value));
        });
        animator.start();
    }

    // Anima la aparición del contenido
    private void animateContent() {
        View[] views = {tvNombre, tvCinturon, tvClases, btnVerAsistencia};

        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setAlpha(0f);
            view.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay(i * 80L)
                    .start();
        }
    }
}