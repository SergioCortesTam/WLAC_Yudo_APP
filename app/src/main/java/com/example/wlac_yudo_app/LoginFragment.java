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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private EditText edtUsuario, edtContraseña;
    private Button btnIniciarSesion;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // 🔥 Firestore

    public LoginFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        edtUsuario = view.findViewById(R.id.campo_usuario);
        edtContraseña = view.findViewById(R.id.campo_contrasena);
        btnIniciarSesion = view.findViewById(R.id.boton_iniciar_sesion);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // 🔥 Instanciamos Firestore

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());

        return view;
    }

    private void iniciarSesion() {
        String correo = edtUsuario.getText().toString().trim();
        String contraseña = edtContraseña.getText().toString().trim();

        if (TextUtils.isEmpty(correo)) {
            edtUsuario.setError("Ingresa tu correo");
            return;
        }
        if (TextUtils.isEmpty(contraseña)) {
            edtContraseña.setError("Ingresa tu contraseña");
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser usuario = mAuth.getCurrentUser();
                        if (usuario != null) {
                            verificarRolUsuario(usuario.getUid());
                        }
                    } else {
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verificarRolUsuario(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("role");
                        if (rol != null) {
                            if (rol.equalsIgnoreCase("profesor")) {
                                cargarFragmento(new ProfesorFragment());
                            } else if (rol.equalsIgnoreCase("alumno")) {
                                cargarFragmento(new AlumnoFragment());
                            } else {
                                Toast.makeText(getContext(), "Rol desconocido", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "No se encontró el rol", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al obtener rol: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void cargarFragmento(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.addToBackStack(null); // Para poder volver atrás si quieres
        transaction.commit();
    }
}
