package com.example.wlac_yudo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button btnHistoria, btnHorarios, btnProductos;
    private ImageButton btnLoginIcono;
    private ImageButton btnInstagram, btnWhatsapp, btnYoutube, btnFacebook, btnX;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences prefs;
    private static final String PREFS = "app_prefs";
    private static final String KEY_SESSION = "session_activa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Botones top
        btnHistoria   = findViewById(R.id.btn_historia);
        btnHorarios   = findViewById(R.id.btn_horarios);
        btnProductos  = findViewById(R.id.btn_productos);
        btnLoginIcono = findViewById(R.id.btn_login_icono);

        // Botones redes
        btnInstagram = findViewById(R.id.btn_instagram);
        btnWhatsapp  = findViewById(R.id.btn_whatsapp);
        btnYoutube   = findViewById(R.id.btn_youtube);
        btnFacebook  = findViewById(R.id.btn_facebook);
        btnX         = findViewById(R.id.btn_x);

        // Fragmento inicial
        cargarFragmento(new HistoriaFragment());

        // Listeners
        btnHistoria.setOnClickListener(v -> cargarFragmento(new HistoriaFragment()));
        btnHorarios.setOnClickListener(v -> cargarFragmento(new HorariosFragment()));
        btnProductos.setOnClickListener(v -> cargarFragmento(new ProductosFragment()));

        btnInstagram.setOnClickListener(v -> abrirEnlace("https://www.instagram.com/wlacyudo/#"));
        btnWhatsapp .setOnClickListener(v -> abrirEnlace("https://wa.me/123456789"));
        btnYoutube  .setOnClickListener(v -> abrirEnlace("https://www.youtube.com/channel/UCkLErE2yakUmCuhfEaeuFAg"));
        btnFacebook .setOnClickListener(v -> abrirEnlace("https://www.facebook.com"));
        btnX        .setOnClickListener(v -> abrirEnlace("https://twitter.com"));

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

        btnLoginIcono.setImageResource(
                (logueado && sessionActiva)
                        ? R.drawable.ic_usuario_desbloqueado
                        : R.drawable.ic_usuario_bloqueado
        );
    }

    private void mostrarMenuSesion(View anchor) {
        PopupMenu menu = new PopupMenu(this, anchor);
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

    private void cargarFragmento(Fragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, f);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void abrirEnlace(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    // ✅ NUEVO: Detectar rol real desde Firestore
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
                    // Podrías mostrar un Toast de error aquí si quieres
                });
    }
}
