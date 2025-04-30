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

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public PerfilFragment() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Aquí buscamos en la base de datos si es profesor o alumno
            String userId = currentUser.getUid();

            // Imaginemos que en la base de datos tienes un nodo "users/{userId}/role"
            mDatabase.child("users").child(userId).child("role")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String role = snapshot.getValue(String.class);
                            if ("profesor".equals(role)) {
                                loadFragment(new ProfesorFragment());
                            } else {
                                loadFragment(new AlumnoFragment());
                            }
                        } else {
                            // Por defecto consideramos alumno
                            loadFragment(new AlumnoFragment());
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Error leyendo el rol -> mandamos a alumno por defecto
                        loadFragment(new AlumnoFragment());
                    });
        }

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.commit();
    }
}
