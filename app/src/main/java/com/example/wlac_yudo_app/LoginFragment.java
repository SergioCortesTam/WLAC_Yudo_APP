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

public class LoginFragment extends Fragment {

    private EditText edtUsuario, edtContrasena;
    private Button btnIniciarSesion;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // SharedPreferences
    private static final String PREFS = "app_prefs";
    private static final String KEY_SESSION = "session_activa";

    public LoginFragment() {}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        edtUsuario       = view.findViewById(R.id.campo_usuario);
        edtContrasena    = view.findViewById(R.id.campo_contrasena);
        btnIniciarSesion = view.findViewById(R.id.boton_iniciar_sesion);
        Button btnOlvidoContrasena = view.findViewById(R.id.olvido_contrasena);
        btnOlvidoContrasena.setOnClickListener(v -> mostrarDialogoRecuperarContrasena());


        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());

        return view;
    }

    private void iniciarSesion() {
        String correo = edtUsuario.getText().toString().trim();
        String pass   = edtContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(correo)) {
            edtUsuario.setError("Ingresa tu correo");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtContrasena.setError("Ingresa tu contraseña");
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Guardar flag de sesión activa
                        requireActivity()
                                .getSharedPreferences(PREFS, requireActivity().MODE_PRIVATE)
                                .edit()
                                .putBoolean(KEY_SESSION, true)
                                .apply();
                        verificarRolPorEmail(correo);
                    } else {
                        Toast.makeText(getContext(),
                                "Error de autenticación: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verificarRolPorEmail(String correo) {
        db.collection("users")
                .whereEqualTo("email", correo)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        // Aquí usamos DocumentSnapshot
                        DocumentSnapshot doc = snapshot.getDocuments().get(0);
                        String rol = doc.getString("role");
                        // Navegar sin volver al login
                        Fragment destino = "profesor".equalsIgnoreCase(rol)
                                ? new ProfesorFragment()
                                : new AlumnoFragment();
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_content, destino)
                                .commit();
                    } else {
                        Toast.makeText(getContext(),
                                "No se encontró tu perfil en la base de datos.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Error al consultar rol: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
    }
    private void mostrarDialogoRecuperarContrasena() {
        // Crear un EditText dinámico
        final EditText inputCorreo = new EditText(getContext());
        inputCorreo.setHint("Correo registrado");
        inputCorreo.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputCorreo.setPadding(40, 30, 40, 30);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Recuperar contraseña")
                .setMessage("Ingresa tu correo electrónico para recibir un enlace de recuperación.")
                .setView(inputCorreo)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String correo = inputCorreo.getText().toString().trim();
                    if (!TextUtils.isEmpty(correo)) {
                        FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(correo)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Correo enviado con instrucciones", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}
