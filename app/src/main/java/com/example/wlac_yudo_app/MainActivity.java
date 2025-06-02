package com.example.wlac_yudo_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private BottomNavigationView socialBar;
    private MaterialButton btnLoginIcono;  // Cambiado a MaterialButton

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences prefs;
    private static final String PREFS = "app_prefs";
    private static final String KEY_SESSION = "session_activa";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.social_bar);
        nav.setItemIconTintList(null); // Esto asegura que NO se aplique tintado


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        tabLayout = findViewById(R.id.tab_layout);
        socialBar = findViewById(R.id.social_bar);
        btnLoginIcono = findViewById(R.id.btn_login_icono);

        // Cargar fragmento inicial
        cargarFragmento(new HistoriaFragment());

        // Tabs: Historia, Horarios, Productos
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        cargarFragmento(new HistoriaFragment());
                        break;
                    case 1:
                        cargarFragmento(new HorariosFragment());
                        break;
                    case 2:
                        cargarFragmento(new ProductosFragment());
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Redes sociales
        socialBar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_instagram) {
                abrirEnlace("https://www.instagram.com/wlacyudo/#");
            } else if (id == R.id.menu_whatsapp) {
                abrirEnlace("https://wa.me/123456789");
            } else if (id == R.id.menu_youtube) {
                abrirEnlace("https://www.youtube.com/channel/UCkLErE2yakUmCuhfEaeuFAg");
            } else if (id == R.id.menu_facebook) {
                abrirEnlace("https://www.facebook.com");
            } else if (id == R.id.menu_x) {
                abrirEnlace("https://twitter.com");
            }

            return true;
        });

        // Login / Perfil
        btnLoginIcono.setOnClickListener(v -> {
            boolean logueado = mAuth.getCurrentUser() != null;
            boolean sessionActiva = prefs.getBoolean(KEY_SESSION, false);

            if (!logueado || !sessionActiva) {
                cargarFragmento(new LoginFragment());
            } else {
                mostrarMenuSesion(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarIconoSesion();
    }

    private void actualizarIconoSesion() {
        boolean logueado = mAuth.getCurrentUser() != null;
        boolean sessionActiva = prefs.getBoolean(KEY_SESSION, false);

        btnLoginIcono.setIconResource(  // Cambiado de setImageResource a setIconResource
                (logueado && sessionActiva)
                        ? R.drawable.ic_usuario_desbloqueado
                        : R.drawable.ic_usuario_bloqueado
        );
    }

    private void mostrarMenuSesion(View anchor) {
        androidx.appcompat.widget.PopupMenu menu = new androidx.appcompat.widget.PopupMenu(this, anchor);
        menu.getMenuInflater().inflate(R.menu.menu_sesion, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_ver_perfil) {
                mostrarPerfilSegunRol();
                return true;
            } else if (id == R.id.menu_cerrar_sesion) {
                mAuth.signOut();
                prefs.edit().putBoolean(KEY_SESSION, false).apply();
                actualizarIconoSesion();
                cargarFragmento(new HistoriaFragment());
                return true;
            }
            return false;
        });
        menu.show();
    }

    private void cargarFragmento(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void abrirEnlace(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void mostrarPerfilSegunRol() {
        String email = mAuth.getCurrentUser() != null ?
                mAuth.getCurrentUser().getEmail() : null;

        if (email == null) return;

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        String rol = snapshot.getDocuments().get(0).getString("role");

                        Fragment fragment;
                        if ("profesor".equalsIgnoreCase(rol)) {
                            fragment = new ProfesorFragment();
                        } else {
                            fragment = new AlumnoFragment();
                        }

                        cargarFragmento(fragment);
                    }
                })
                .addOnFailureListener(e -> {
                    // Aquí puedes mostrar un mensaje de error si quieres
                });
    }
}
