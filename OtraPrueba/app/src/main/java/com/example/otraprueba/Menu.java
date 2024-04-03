package com.example.otraprueba;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Menu extends AppCompatActivity {



    private static final String TAG = "Menu";
    private Button btnJugar;
    private String usuarioLogueado;

    private Button btnInstrucciones;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

         usuarioLogueado = getIntent().getStringExtra("usuarioLogueado");
         Log.i(TAG,"valor del nick: "+usuarioLogueado);

        btnJugar = findViewById(R.id.btnJugar);

        btnInstrucciones = findViewById(R.id.btnInstrucciones);

        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Menu.this, Juego.class);
                intent.putExtra("usuarioLogueado", usuarioLogueado);
                startActivity(intent);

            }
        });

        btnInstrucciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Instrucciones.class);
                startActivity(intent);

            }
        });

        Button btnPuntuaciones = findViewById(R.id.btnPuntuaciones);
        btnPuntuaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos una instancia del fragmento
                UsersFragment usersFragment = new UsersFragment();

                // Obtenemos el fragment manager y comenzamos una transacción
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Reemplazamos el contenido del contenedor principal por el del fragmento
                fragmentTransaction.replace(R.id.fragment_container, usersFragment);

                // Añadimos la transacción al backstack
                fragmentTransaction.addToBackStack(null);

                // Realizamos la transacción
                fragmentTransaction.commit();
            }
        });




    }







}
