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

public class MainActivity extends AppCompatActivity {
    Button btnHistoria, btnHorarios, btnProductos, btnLogin;

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
        btnLogin = findViewById(R.id.btn_login);

        // Inicialización de botones inferiores (redes)
        btnInstagram = findViewById(R.id.btn_instagram);
        btnWhatsapp = findViewById(R.id.btn_whatsapp);
        btnYoutube = findViewById(R.id.btn_youtube);
        btnFacebook = findViewById(R.id.btn_facebook);
        btnX = findViewById(R.id.btn_x);

        // Fragmento inicial al abrir la app
        loadFragment(new HistoriaFragment());

        // Funciones de botones superiores
        btnHistoria.setOnClickListener(view -> loadFragment(new HistoriaFragment()));
        btnHorarios.setOnClickListener(view -> loadFragment(new HorariosFragment()));
        btnProductos.setOnClickListener(view -> loadFragment(new ProductosFragment()));
        btnLogin.setOnClickListener(view -> loadFragment(new LoginFragment()));

        // Funciones de botones inferiores (abrir redes)
        btnInstagram.setOnClickListener(view -> openLink("https://www.instagram.com/wlacyudo/#"));
        btnWhatsapp.setOnClickListener(view -> openLink(""));
        btnYoutube.setOnClickListener(view -> openLink("https://www.youtube.com/channel/UCkLErE2yakUmCuhfEaeuFAg"));
        btnFacebook.setOnClickListener(view -> openLink("https://www.youtube.com/channel/UCkLErE2yakUmCuhfEaeuFAg"));
        btnX.setOnClickListener(view -> openLink("https://twitter.com"));
    }

    // Método para cargar fragmentos
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.commit();
    }

    // Método para abrir URLs en navegador
    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
