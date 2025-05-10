package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class VerAsistenciasAlumnoFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout layoutAsistencias;

    public VerAsistenciasAlumnoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ver_asistencias_alumno, container, false);

        layoutAsistencias = view.findViewById(R.id.layout_lista_asistencias);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cargarAsistencias();
        return view;
    }

    private void cargarAsistencias() {
        String email = mAuth.getCurrentUser() != null
                ? mAuth.getCurrentUser().getEmail()
                : null;
        Log.d("VerAsistencias", "Email actual = " + email);
        if (email == null) return;

        // 1) Obtenemos el document ID en 'users' que corresponde a este email
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(userSnap -> {
                    if (userSnap.isEmpty()) {
                        Log.w("VerAsistencias", "No se encontró usuario en 'users'");
                        return;
                    }
                    // Asumimos un único documento
                    DocumentSnapshot userDoc = userSnap.getDocuments().get(0);
                    String alumnoId = userDoc.getId();
                    Log.d("VerAsistencias", "AlumnoId = " + alumnoId);

                    // 2) Ahora consultamos las asistencias de ese alumnoId
                    db.collection("Asistencias")
                            .whereEqualTo("alumnoId", alumnoId)
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                Log.d("VerAsistencias", "Docs recuperados: " + snapshot.size());
                                layoutAsistencias.removeAllViews();
                                for (QueryDocumentSnapshot doc : snapshot) {
                                    String fecha = doc.getString("fecha");
                                    Boolean asistio = doc.getBoolean("asistio");
                                    Log.d("VerAsistencias", "→ " + fecha + " → " + asistio);

                                    View asistenciaView = LayoutInflater.from(getContext())
                                            .inflate(R.layout.item_asistencia_registro, layoutAsistencias, false);

                                    TextView txtFecha = asistenciaView.findViewById(R.id.txt_fecha_asistencia);
                                    TextView txtEstado = asistenciaView.findViewById(R.id.txt_estado_asistencia);

                                    txtFecha.setText("📅 " + fecha);
                                    txtEstado.setText(asistio != null && asistio
                                            ? "✅ Asistió"
                                            : "❌ Faltó");

                                    layoutAsistencias.addView(asistenciaView);
                                }
                            })
                            .addOnFailureListener(e -> Log.e("VerAsistencias", "Error leyendo Asistencias", e));
                })
                .addOnFailureListener(e ->
                        Log.e("VerAsistencias", "Error buscando usuario en 'users'", e)
                );
    }
}
