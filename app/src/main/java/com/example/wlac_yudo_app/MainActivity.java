package com.example.wlac_yudo_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // Botones superiores
    Button btnHistoria, btnHorarios, btnProductos, btnIniciarSesion;

    // Botones de redes sociales
    ImageButton btnInstagram, btnWhatsapp, btnYoutube, btnFacebook, btnX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de botones superiores
        btnHistoria = findViewById(R.id.btn_historia);
        btnHorarios = findViewById(R.id.btn_horarios);
        btnProductos = findViewById(R.id.btn_productos);
        btnIniciarSesion = findViewById(R.id.btn_login);

        // Inicialización de botones inferiores (redes)
        btnInstagram = findViewById(R.id.btn_instagram);
        btnWhatsapp = findViewById(R.id.btn_whatsapp);
        btnYoutube = findViewById(R.id.btn_youtube);
        btnFacebook = findViewById(R.id.btn_facebook);
        btnX = findViewById(R.id.btn_x);

        // Fragmento inicial al abrir la app
        cargarFragmento(new HistoriaFragment());

        // Funciones de botones superiores
        btnHistoria.setOnClickListener(view -> cargarFragmento(new HistoriaFragment()));
        btnHorarios.setOnClickListener(view -> cargarFragmento(new HorariosFragment()));
        btnProductos.setOnClickListener(view -> cargarFragmento(new ProductosFragment()));

        // Funciones de botones inferiores (abrir redes)
        btnInstagram.setOnClickListener(view -> abrirEnlace("https://www.instagram.com/wlacyudo/#"));
        btnWhatsapp.setOnClickListener(view -> abrirEnlace("https://wa.me/123456789"));
        btnYoutube.setOnClickListener(view -> abrirEnlace("https://www.youtube.com/channel/UCkLErE2yakUmCuhfEaeuFAg"));
        btnFacebook.setOnClickListener(view -> abrirEnlace("https://www.facebook.com"));
        btnX.setOnClickListener(view -> abrirEnlace("https://twitter.com"));

        // Lógica para manejar la visualización del fragmento de login o de usuario logueado
        if (estaUsuarioLogueado()) {
            if (esUsuarioProfesor()) {
                cargarFragmento(new ProfesorFragment());
            } else {
                cargarFragmento(new AlumnoFragment());
            }
        } else {
            cargarFragmento(new LoginFragment());
        }

        // Función para gestionar el botón de Iniciar Sesión (mostrar o cerrar sesión según el estado)
        btnIniciarSesion.setOnClickListener(v -> {
            if (estaUsuarioLogueado()) {
                if (esUsuarioProfesor()) {
                    cargarFragmento(new ProfesorFragment());
                } else {
                    cargarFragmento(new AlumnoFragment());
                }
            } else {
                cargarFragmento(new LoginFragment());
            }
        });
    }

    // Método para cargar fragmentos
    private void cargarFragmento(Fragment fragmento) {
        FragmentTransaction transaccion = getSupportFragmentManager().beginTransaction();
        transaccion.replace(R.id.main_content, fragmento);
        transaccion.commit();
    }

    // Método para abrir URLs en el navegador (redes sociales)
    private void abrirEnlace(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    // Método para comprobar si el usuario está logueado en Firebase
    private boolean estaUsuarioLogueado() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    // Método para comprobar si el usuario es un profesor
    private boolean esUsuarioProfesor() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return email != null && email.contains("@profesor.com");
    }
}
