
package com.example.wlac_yudo_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class HistoriaFragment extends Fragment {

    private static final String TAG = "HistoriaFragment";
    // Email para contacto
    private static final String EMAIL = "o0osergio0o@gmail.com";
    // Ubicación real para el mapa (ejemplo usando coordenadas de Madrid)
    private static final double LATITUDE = 40.4168;
    private static final double LONGITUDE = -3.7038;
    private static final String LOCATION_NAME = "WLAC Yudo";

    public HistoriaFragment() {
        // Constructor obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // BOTÓN CONTACTAR
        MaterialButton btnContactar = view.findViewById(R.id.btn_contactar);

        if (btnContactar == null) {
            Log.e(TAG, "El botón de contacto no se encontró en el layout");
            return;
        }

        Log.d(TAG, "Botón de contacto encontrado correctamente");

        btnContactar.setOnClickListener(v -> {
            Log.d(TAG, "Botón de contacto clicado");
            abrirEmail();
        });

        // IMAGEN MAPA
        View imgMapa = view.findViewById(R.id.img_mapa);
        if (imgMapa != null) {
            imgMapa.setOnClickListener(v -> {
                abrirMapa();
            });
        }
    }

    private void abrirEmail() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822"); // Filtra solo apps de correo
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la app WLAC Yudo");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hola, me interesa obtener información sobre WLAC Yudo.");

            startActivity(Intent.createChooser(emailIntent, "Enviar correo con..."));
        } catch (Exception e) {
            Log.e(TAG, "Error al intentar abrir el correo electrónico", e);
            Toast.makeText(requireContext(),
                    "No se pudo abrir el cliente de correo. Asegúrate de tener uno instalado.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void abrirMapa() {
        try {
            // Coordenadas y nombre del lugar
            String nombreLugar = "CDE Wlac Yudo";
            double latitud = 40.2282276;
            double longitud = -3.7698975;

            // Construcción de la URI geo
            Uri locationUri = Uri.parse("geo:0,0?q=" + Uri.encode(nombreLugar + "@" + latitud + "," + longitud));
            Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
            intent.setPackage("com.google.android.apps.maps"); // Prioriza Google Maps si está disponible

            // Inicia el intent
            startActivity(intent);
            Log.d("Mapa", "Intent lanzado para abrir ubicación: " + nombreLugar);
        } catch (Exception e) {
            Log.e("Mapa", "Error al abrir el mapa", e);
            Toast.makeText(requireContext(),
                    "No se pudo abrir el mapa. Asegúrate de tener Google Maps instalado.",
                    Toast.LENGTH_SHORT).show();
        }
    }



}