package com.example.wlac_yudo_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class PerfilFragment extends Fragment {
    // Fragmento que decide qué perfil mostrar (alumno o profesor)

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser usuarioActual = mAuth.getCurrentUser();

        if (usuarioActual != null) {
            // Consultar rol del usuario en Firebase
            mDatabase.child("users").child(usuarioActual.getUid()).child("role")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String rol = snapshot.getValue(String.class);
                            // Cargar fragmento según el rol
                            if ("profesor".equals(rol)) {
                                cargarFragmento(new ProfesorFragment());
                            } else {
                                cargarFragmento(new AlumnoFragment());
                            }
                        } else {
                            // Por defecto mostrar perfil de alumno
                            cargarFragmento(new AlumnoFragment());
                        }
                    });
        }

        return view;
    }

    // Carga el fragmento correspondiente en el contenedor
    private void cargarFragmento(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.commit();
    }
}