package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// LoginFragment.java
public class LoginFragment extends Fragment {
    // Fragmento para inicio de sesión

    private EditText edtUsuario, edtContrasena;
    private Button btnIniciarSesion;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Obtener referencias
        edtUsuario = view.findViewById(R.id.campo_usuario);
        edtContrasena = view.findViewById(R.id.campo_contrasena);
        btnIniciarSesion = view.findViewById(R.id.boton_iniciar_sesion);
        Button btnOlvidoContrasena = view.findViewById(R.id.olvido_contrasena);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        btnOlvidoContrasena.setOnClickListener(v -> mostrarDialogoRecuperarContrasena());

        return view;
    }

    // Gestiona el inicio de sesión
    private void iniciarSesion() {
        String correo = edtUsuario.getText().toString().trim();
        String pass = edtContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(correo)) {
            edtUsuario.setError("Ingresa tu correo");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtContrasena.setError("Ingresa tu contraseña");
            return;
        }

        // Autenticar con Firebase
        mAuth.signInWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        verificarRolPorEmail(correo);
                    } else {
                        Toast.makeText(getContext(), "Error de autenticación", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Verifica el rol del usuario (alumno/profesor)
    private void verificarRolPorEmail(String correo) {
        db.collection("users")
                .whereEqualTo("email", correo)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot doc = snapshot.getDocuments().get(0);
                        String rol = doc.getString("role");

                        // Navegar al fragmento adecuado
                        Fragment destino = "profesor".equalsIgnoreCase(rol)
                                ? new ProfesorFragment()
                                : new AlumnoFragment();

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_content, destino)
                                .commit();
                    }
                });
    }

    // Muestra diálogo para recuperar contraseña
    private void mostrarDialogoRecuperarContrasena() {
        // ... (implementación del diálogo)
    }
}