package com.example.otraprueba;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String COLLECTION_NAME = "usuarios";
    String usuarioLogueado;

    private static final int NOTIFICATCION_ID = 1;
    private static final String CANAL_ID = "MyNotificationChannel";

    private static final String TAG = "MainActivity";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAbrirDialogo = findViewById(R.id.btnAbrirDialogo);
        btnAbrirDialogo.setOnClickListener(v -> dialogoRegistro());


        Button btnSesion = findViewById(R.id.btnIniciarSesion);
        btnSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        Button btnSalir = findViewById(R.id.btnSalir);

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        //Creamos un manejador para controlar el retraso de la notifiación
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                crearNotificacion();
            }
        }, 5000); //


    }

    private void iniciarSesion() {
        final EditText editTextNick = findViewById(R.id.editTextNick);
        final EditText editTextPassword = findViewById(R.id.editTextTextPassword);
        String nick = editTextNick.getText().toString();
        String pass = editTextPassword.getText().toString();
        usuarioLogueado=nick;
        Log.e(TAG, "valor nick: "+nick);
        Log.e(TAG, "valor pass: "+pass);

        verificarUsuario(nick, pass);
    }

    private void verificarUsuario(String nick, String pass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Consulta para buscar documentos con el campo "nick" igual a "nick"
        db.collection(COLLECTION_NAME)
                .whereEqualTo("nick", nick)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            Log.d(TAG, "No se encontraron usuarios con nick: " + nick);
                            // Si no se encuentra un documento con el nick proporcionado
                            Toast.makeText(MainActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Verifica si el documento contiene el campo "pass" y si su valor coincide con la contraseña proporcionada
                                if (document.contains("pass")) {
                                    String passFromFirestore = document.getString("pass");
                                    if (pass.equals(passFromFirestore)) {
                                        Log.d(TAG, "Usuario encontrado con nick: " + nick);
                                        // Si la contraseña coincide, el usuario puede iniciar sesión
                                        // Por ejemplo, aquí puedes abrir una nueva actividad
                                        Intent intent = new Intent(MainActivity.this, Menu.class);
                                        intent.putExtra("usuarioLogueado", usuarioLogueado);
                                        startActivity(intent);
                                        // Cierra la actividad actual
                                        return; // Termina el método después de iniciar sesión
                                    } else {
                                        Log.d(TAG, "Contraseña incorrecta para el usuario con nick: " + nick);
                                        Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "El usuario encontrado con nick: " + nick + " no tiene contraseña");
                                    Toast.makeText(MainActivity.this, "El usuario no tiene contraseña", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        // Manejar cualquier error que pueda ocurrir durante la consulta
                        Log.e(TAG, "Error al verificar el usuario", task.getException());
                        Toast.makeText(MainActivity.this, "Error al verificar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }





    private void dialogoRegistro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Regístrese para jugar");


        View dialogView = getLayoutInflater().inflate(R.layout.cuadro_dialogo, null);
        final EditText editText1 = dialogView.findViewById(R.id.editText1);
        final EditText editText2 = dialogView.findViewById(R.id.editText2);
        final EditText editText3 = dialogView.findViewById(R.id.editText3);
        builder.setView(dialogView);


        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nick = editText1.getText().toString();
                String pass1 = editText2.getText().toString();
                String pass2 = editText3.getText().toString();

                // Llamamos al método para verificar si el usuario puede ser registrado
                validarUsuario(nick, pass1, pass2).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean verifica) {
                        // Verificamos si el usuario puede ser registrado
                        if (verifica) {
                            // Si la verificación es exitosa, llama al método para crear el usuario
                            anadirUsuarioEnFireBase(nick, pass1);
                        } else {


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "Error al verificar el usuario", e);

                        Toast.makeText(MainActivity.this, "Error al verificar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //comprobamos si se han introducir todos los campos y si el nick está disponible
    private Task<Boolean> validarUsuario(String nick, String pass1, String pass2) {
        Log.d(TAG, "Valor de pass1: " + pass1);
        Log.d(TAG, "Valor de pass2: " + pass2);

        if (nick.length()==0||pass1.length()==0||pass2.length()==0) {
            Toast.makeText(getApplicationContext(), "Error, introduzca todos los campos requeridos", Toast.LENGTH_SHORT).show();

            return Tasks.forResult(false);
        } else if (!pass1.equals(pass2)) {
            errorPassword();
            return Tasks.forResult(false); // Retorna una tarea completada con false
        } else {
            // Llamamos al método para verificar si el nick existe en Firebase
            return verificarNickExistente(nick,false);
        }
    }

    private void errorPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Error, las contraseñas no coinciden");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void anadirUsuarioEnFireBase(String nick, String password) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuario = new HashMap<>();

        usuario.put("nick", nick);
        usuario.put("pass", password);
        usuario.put("puntuacion", 0);


        db.collection("usuarios").add(usuario);

        registroCorrecto();


    }

    private void registroCorrecto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuario registrado");
        builder.setMessage("Se ha registrado el usuario exitosamente");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        leerBaseDeDatos();
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //este método se usa internamente para debbuging, comprobando que los registros se guardan en FireBase
    private void leerBaseDeDatos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    // Método para verificar si un nick existe en Firebase Firestore
    private Task<Boolean> verificarNickExistente(String nick, boolean yaTengoNick) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Realiza una consulta para obtener todos los documentos de la colección
        return db.collection(COLLECTION_NAME)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        // Obtiene la lista de todos los nicks existentes
                        List<String> nicks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String nickEnFirestore = document.getString("nick");
                            nicks.add(nickEnFirestore);
                        }

                        // Verifica si el nick dado está presente en la lista de nicks
                        boolean nickExiste = nicks.contains(nick);
                        Log.d(TAG, "¿El nick '" + nick + "' existe?: " + nickExiste);
                        if(nickExiste && !yaTengoNick) {
                            usuarioYaExiste();
                        }
                        return !nickExiste;
                    } else {
                        // Si la consulta no se completó con éxito, devuelve false
                        Log.e(TAG, "Error al obtener los nicks de Firestore: ", task.getException());
                        return false;
                    }
                });
    }

    private void usuarioYaExiste() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Error, este usuario ya existe");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        leerBaseDeDatos();
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void crearNotificacion() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(CANAL_ID, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(canal);
        }

        // Construimos la notificación
        Notification notification = new Notification.Builder(this, CANAL_ID)
                .setContentTitle("¡Comparte la app con tus amigos!")
                .setContentText("¿Quién logrará la mayor puntuación?")
                .setSmallIcon(R.drawable.app_icon)
                .build();
        // Mostramos la notificación
        notificationManager.notify(NOTIFICATCION_ID, notification);
    }
}





