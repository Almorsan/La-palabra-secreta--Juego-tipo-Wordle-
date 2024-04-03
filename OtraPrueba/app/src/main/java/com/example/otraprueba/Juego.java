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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Juego extends AppCompatActivity {


    List<String> listaPalabrasCincoLetras;
    String palabraSecreta;
    private static final String TAG = "Juego";
    private EditText editTextPalabraUsuario;
    private List<EditText> editTexts;
    int contadorGeneral=1;
    boolean compararLetras;
    EditText editText;
    int filaActual = 1;
    int contadorDerrota=0;
    private TextView miTextView;
    Map<Character, Integer> letrasAmarillas = new HashMap<>();
    private TextWatcher editTextPalabraUsuarioWatcher;
    private int IDEditText =0;
    private String usuarioLogueado;
    private int contadorUsuario;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);


        /*
        en la primera iteración del mainActivity, este List será null. Si, por el contrario,
        nos encontramos en una nueva iteración, obtendremos un objeto List con
        una lista de palabras de cinco letras, a la cual se le ha quitado la palabra
        de nuestra última partida

         */
        listaPalabrasCincoLetras= getIntent().getStringArrayListExtra("DATOS_EXTRA");
        usuarioLogueado = getIntent().getStringExtra("usuarioLogueado");
        contadorUsuario = getIntent().getIntExtra("contadorUsuario", 0);
        Log.i(TAG,"valor del nick: "+usuarioLogueado);
        Log.i(TAG,"valor del contador: "+contadorUsuario);

        if (listaPalabrasCincoLetras == null) {
            listaPalabrasCincoLetras = listaPalabras();

        }

        palabraSecreta=obtenerPalabraAleatoria(listaPalabrasCincoLetras).toUpperCase();



        //en primer lugar, nos encontramos en la primera fila
        configurarFila(1, 5, editTextPalabraUsuarioWatcher);

        //configuramos los filtros
        configurarFiltros();

        //manejamos el evento al pulsar la tecla Intro
        editTextPalabraUsuario.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return pulsarTeclaIntro(keyCode, event);
            }
        });

    }


    private void configurarFiltros() {
        /*con este método configuramos los filtros necesarios para que el input
        introducido por el usuario no cumple los requisitos necesarios, no se tenga en
        cuenta. El usuario deberá introducir, por lo tanto, una palabra de cinco
        letras que no contenga caracteres numéricos o especiales
         */

        InputFilter.LengthFilter verificarLongitud = new InputFilter.LengthFilter(5);
        InputFilter verificarTexto = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // Filtra el input para permitir solo letras y asegura longitud de 5 caracteres

                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        return "";
                    }
                }

                // Asegura que la longitud total sea igual a 5
                int length = dest.length() - (dend - dstart) + (end - start);
                if (length > 5) {
                    return "";
                }

                return null; // Acepta la entrada tal como es
            }
        };

        editTextPalabraUsuario.setFilters(new InputFilter[]{verificarLongitud, verificarTexto});
    }





    private void configurarFila(int primeraColumna, int ultimaColumna, TextWatcher textWatcher) {
       /* con este método configuramos en qué fila nos encontramos
       Además, utilizamos un textWathcher para que, a medida que el usuario vaya introduciendo
       la palabra, las letras se vayan colocando en el EditText correspondiente y, si el usuario borra
       una letra, también se vaya borrando de dicho editText

        */


        editTextPalabraUsuario = findViewById(R.id.editTextPalabraUsuario);
        editTexts = new ArrayList<>();

        for (int i = primeraColumna; i <= ultimaColumna; i++) {
            editTexts.add((EditText) findViewById(getResources().getIdentifier("editText" + i, "id", getPackageName())));
        }

        //borramos el antiguo textWatcher en caso de que sea necesario
        if (textWatcher != null) {
            editTextPalabraUsuario.removeTextChangedListener(textWatcher);
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                //no hace falta implementar nada aquí
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //no hace falta implementar nada aquí
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // a medida que el usuario vaya escribiendo la palabra, se irán mostrando
                //en su edit view correspondiente. Si el usuario borra una letra,
                //también se borrará del editView
                String palabra = editable.toString().toUpperCase();

                for (int i = 0; i < editTexts.size(); i++) {
                    if (i < palabra.length()) {
                        editTexts.get(i).setText(String.valueOf(palabra.charAt(i)));
                    } else {
                        editTexts.get(i).setText("");
                    }
                }
            }
        };

        editTextPalabraUsuario.addTextChangedListener(watcher);

    }


    private boolean pulsarTeclaIntro(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

            //guardamos la palabra introducida por el usuario
            String miPalabra = editTextPalabraUsuario.getText().toString().toUpperCase();

            //verificamos que la palabra tiene cinco letras
            if (miPalabra.length() != 5) {

                return true; // devuelve true para indicar que hemos manejado el evento
                //el rersto del código del método no se ejecutrará
            }


            filaActual++;
            // cambiamos de fila
            switch (filaActual) {
                case 2:
                    configurarFila(6, 10, editTextPalabraUsuarioWatcher);
                    break;
                case 3:
                    configurarFila(11, 15, editTextPalabraUsuarioWatcher);
                    break;
                case 4:
                    configurarFila(16, 20, editTextPalabraUsuarioWatcher);
                    break;
                case 5:
                    configurarFila(21, 25, editTextPalabraUsuarioWatcher);
                    break;
                case 6:
                    configurarFila(26, 30, editTextPalabraUsuarioWatcher);
                    break;
            }


            //llamamos al método para generar un hashmap con las letras amarillas de la palabra
            //así como el número de veces que se repite
            letrasAmarillas = contarLetrasAmarillas(palabraSecreta, miPalabra);


            //en este método hacemos las comparaciones necesarias para cambiar
            //el color de la palabra. Aunque acabamos de cambiar de fila, se cambiarán
            //de color las letras de la fila anterior
            compararLetras = compararLetras(palabraSecreta, miPalabra);


            editTextPalabraUsuario.removeTextChangedListener(editTextPalabraUsuarioWatcher);

            // limpiamos el contenido del EditText para la próxima entrada
            editTextPalabraUsuario.getText().clear();

            boolean victoria = victoria(palabraSecreta, miPalabra);


            //aquí verificamos si el usuario gana o pierde
            if (victoria) {
                finDelJuego(victoria);

            } else if (contadorDerrota == 6 && !victoria) {
                finDelJuego(victoria);
            }

            //utilizamos un Handler para que el requestFocus se implemente correctamente
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    editTextPalabraUsuario.requestFocus();
                }
            }, 400);

            return true; // devolvemos true para indicar que se ha manejado el evento
        }
        return false;
    }





    public static List<String> listaPalabras() {
        List<String> listaPalabras = new ArrayList<>();
        listaPalabras.addAll(Arrays.asList(
                "abeto", "actor", "aguas", "agudo", "alado",
                "albas", "altar", "Anton", "atizo", "avala",
                "avion", "azul",
                "babas", "bacas", "bache", "bajes", "balas",
                "bebes", "belen", "Berto", "bicho", "bizco",
                "bueno", "busca",
                "cabra", "cafes", "cajas", "calar", "calas",
                "calca", "calla", "calma", "camba", "campo",
                "canas", "cantos", "capto", "caras", "Carlo",
                "carro", "casas", "catar", "caida", "cejas",
                "Celia", "cenas", "cepas", "cerca", "cerco",
                "cerdo", "chile", "china", "ciego", "cines",
                "citas", "clara", "clavo", "colas", "colon",
                "colon", "coral", "coras", "corea", "corro",
                "cosas", "costo", "crudo", "curar",
                "dados", "dagas", "datos", "daños", "dejar",
                "dejes", "denso", "dices", "divos", "dotes",
                "dunas", "dures", "duros",
                "ellos", "echas", "edito", "elevo", "emule",
                "emulo", "enoje", "error", "estas",
                "fallo", "falto", "feria", "fetos", "fijos",
                "filas", "filia", "finca",
                "gafas", "galas", "Gales", "galos", "ganas",
                "ganes", "gases", "gasto", "giras",
                "gordo", "gorro", "grave", "grito",
                "hacer", "halos", "hasta", "heces", "hielo",
                "ideas", "india", "inflo", "islas",
                "Japon", "jefas", "jerga", "Josue", "julio",
                "malos", "mania", "marca", "marco", "marti",
                "maria", "melon", "menos", "meter", "metro",
                "moler", "monte", "morir",
                "nacer", "nadar", "narro", "natas", "naves",
                "necio", "ninos", "notas", "nubes",
                "obras", "ocios", "ollas", "ondas", "onzas",
                "opera", "otros", "ovulo", "oirte",
                "palas", "paris", "pedir", "pelea", "pelos",
                "peras", "perro", "pesos", "pilas", "pinto",
                "poder",
                "Qatar", "quedo", "quema", "quito",
                "reloj", "rubio", "rasco", "ratas", "ratos",
                "redes", "remar", "renos", "renta",
                "sabio", "sacar", "salir", "selva", "sanar",
                "sopas", "secar", "serio", "situó", "sobar",
                "sonar", "subir", "sucio", "siete",
                "tabla", "tacos", "Tania", "tapas", "tazas",
                "telon", "tener", "tenis", "terco", "terso",
                "Texas", "tipos", "tiras", "todas", "todos",
                "tomar", "Tomas", "tonos", "tonto", "toque",
                "torpe", "trote",
                "vacas", "vagos", "valer", "valor", "veces",
                "vedas", "velas", "vemos", "venas", "venir",
                "verde", "viera", "vigas", "vinos", "vivir",
                "volar", "votar",
                "yates", "yemas", "Yemen", "yendo", "yenes",
                "yesca", "yogur", "yugos",
                "zonas", "zorro", "zurdo"
        ));

        return listaPalabras;
    }

    public static String obtenerPalabraAleatoria(List<String> listaPalabras) {
        /* con este método obtenemos una palabra aleatoria de la lista
        de palabras
         */


        // creamos un objeto Random para generar índices aleatorios
        Random random = new Random();

        // obtenemos un índice aleatorio dentro del rango de la lista
        int indiceAleatorio = random.nextInt(listaPalabras.size());

        // obtenemos la palabra en el índice aleatorio
        return listaPalabras.get(indiceAleatorio);
    }





    public boolean compararLetras(String palabraSecreta, String miPalabra) {
        /*en este método aplicamos la lógica por la cual comparamos las letras
        de la palabra introducida por el usuario con las de la palabra secreta

         */


        boolean compara=false;
        final String MIPALABRA=miPalabra;
        final String PALABRASECRETA=palabraSecreta;



        for (int i = 0; i < PALABRASECRETA.length(); i++) {
            final int constante = i;

            if (MIPALABRA.charAt(i) == PALABRASECRETA.charAt(i)) {
                IDEditText++;
                cambiarColorYTexto(MIPALABRA.charAt(i), IDEditText, Color.GREEN, getDelay(i));
            } else if (palabraSecreta.contains(String.valueOf(miPalabra.charAt(i)))) {

                if (letrasAmarillas.containsKey(miPalabra.charAt(i))) {
                    IDEditText++;
                    cambiarColorYTexto(MIPALABRA.charAt(i), IDEditText, Color.YELLOW, getDelay(i));
                    modificarContador(miPalabra.charAt(i), letrasAmarillas);
                } else {
                    IDEditText++;
                    cambiarColorYTexto(MIPALABRA.charAt(i), IDEditText, Color.GRAY, getDelay(i));
                }
            } else {
                IDEditText++;
                cambiarColorYTexto(MIPALABRA.charAt(i), IDEditText, Color.GRAY, getDelay(i));
            }
        }


        contadorGeneral++;
        contadorDerrota++;
        return compara;
    }

    private void cambiarColorYTexto(final char letra, final int id, final int color, long retardo) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editText = obtenerEditTextPorID(id);
                editText.setBackgroundColor(color);
                editText.setText(String.valueOf(letra));
            }
        }, retardo);
    }

    private EditText obtenerEditTextPorID(int id) {
        /*con este método obtenemos el id del textView del cual queremos
        cambiar el color
         */
        int idMetodo = getResources().getIdentifier("editText" + id, "id", getPackageName());
        return findViewById(idMetodo);
    }

    private long getDelay(int indice) {
        /*con este método ajustamos el tiempo que tardará en cambiar la letra de color
        creando una especie de «animación». Dependiendo de a qué columna pertenezca cada letra
        tardará en aparecer entre 200 (primera columna) y 1000 (quinta columna) milisegundos.

         */

        return 200 * (indice + 1);
    }


    public Map<Character, Integer> contarLetrasAmarillas(String palabraSecreta, String miPalabra) {
        /*con este método creamos un hashmap que contendrá las letras amarillas
        que hay en la palabra introducida por el usuario, así como el número de veces que se repiten en la
        palabra (esto nos servirá como contador con el cual podremos verificar
        si debemos cambiar la letra a color amarillo o, por el contrario, cambiarlo a gris.

         */
        Map<Character, Integer> letrasAmarillas = new HashMap<>();

        // creamos una copia de la palabraSecreta para manipularla
        StringBuilder palabraRestante = new StringBuilder(palabraSecreta);

        for (int i = 0; i < Math.min(palabraSecreta.length(), miPalabra.length()); i++) {
            if (miPalabra.charAt(i) == palabraSecreta.charAt(i)) {
                // la letra es verde, así que la sustituimos por un espacio en blanco
                palabraRestante.setCharAt(i, ' ');
            }
        }

        for (int i = 0; i < miPalabra.length(); i++) {
            char letraUsuario = miPalabra.charAt(i);

            if (letraUsuario != palabraSecreta.charAt(i) && palabraRestante.indexOf(String.valueOf(letraUsuario)) != -1) {
                // si se cumple esta condición, la letra es amarilla
                // incrementaremos el contador en el hashmap. Por defecto, el primer valor será 1
                //si la letra aparece más veces, el valor del contador se incrementará
                letrasAmarillas.put(letraUsuario, letrasAmarillas.getOrDefault(letraUsuario, 0) + 1);

                // borramos la letra encontrada en palabraRestante para evitar contarla varias veces
                palabraRestante.setCharAt(palabraRestante.indexOf(String.valueOf(letraUsuario)), ' ');
            }
        }

        return letrasAmarillas;
    }



    public static void modificarContador(char letra, Map<Character, Integer> hashMap) {
          /*con este método se modifica el contador del hashmap letrasAmarillas
          para que no se cambie ninguna letra a color amarillo siempre que el contador
          asociado a dicha letra sea cero
          */

        // verificamos si el HashMap contiene la letra en cuestion
        if (hashMap.containsKey(letra)) {
            // obtenemos el valor actual del contador
            int contadorActual = hashMap.get(letra);

            // actualizamos el contador restando 1
            contadorActual--;

            // si el contador es mayor que cero, actualizamos el hashmap con
            //el valor actual del contador. Si no, borramos la letra del hashmap
            if (contadorActual > 0) {

                hashMap.put(letra, contadorActual);
            } else {

                hashMap.remove(letra);
            }


        }
    }


    boolean victoria (String palabraSecreta,String miPalabra){
        boolean victoria=false;

        if (palabraSecreta.equals(miPalabra)) {
            victoria=true;
        }

        return victoria;

    }




    private void ocultarTeclado(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }





    public void finDelJuego(boolean victoria) {
        /*en este método se encuentra toda la lógica referente al final de la partida,
        tanto si el usuario ha ganado como si no
        */

        Button botonIzquierdo = findViewById(R.id.botonIzquierdo);
        Button botonDerecho = findViewById(R.id.botonDerecho);


        //habilitamos los botones que en un primer momento no eran visibles
        botonIzquierdo.setVisibility(View.VISIBLE);
        botonDerecho.setVisibility(View.VISIBLE);


        EditText miEditText = findViewById(R.id.editTextPalabraUsuario);

        // deshabilitamos el editText
        miEditText.setFocusable(false);
        miEditText.setFocusableInTouchMode(false);
        miEditText.setClickable(false);

        //botón para jugar otra vez
        botonIzquierdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia otra instancia de la misma actividad (MainActivity)
                Intent intent = new Intent(Juego.this, Juego.class);

                listaPalabrasCincoLetras.remove(palabraSecreta);

                intent.putStringArrayListExtra("PALABRAS_CINCO_LETRAS", new ArrayList<>(listaPalabrasCincoLetras));
                intent.putExtra("contadorUsuario", contadorUsuario);
                intent.putExtra("usuarioLogueado", usuarioLogueado);

                // lanzamos la nueva instancia de la actividad, pasándole el ArrayList de
                //palabras sin la palabra secreta de esta partida
                startActivity(intent);

                // finalizamos la instancia actual de la actividad
                finish();
            }
        });

        //botón para dejar de jugar
        botonDerecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // al pulsar este botón, se cierra la app
                finishAffinity();
            }
        });

        //habilitamos el textView que no estaba visible en un primer momento
        // dependiendo del resultado de la partida, se muestra un mensaje u otro
        miTextView = findViewById(R.id.mensajeTextView);
        miTextView.setVisibility(View.VISIBLE);

        if (victoria) {
            contadorUsuario++;
            actualizarPuntuacionSiSuperior(contadorUsuario);
            miTextView.setText("¡Enhorabuena, has ganado!\nLa palabra secreta era: " + palabraSecreta+"\nRacha actual: "+contadorUsuario);


        } else {
            miTextView.setText("Lo siento, has perdido. La palabra secreta era: "+palabraSecreta);
            contadorUsuario=0;

        }

        ocultarTeclado(miEditText);

    }


    // actualizamos la puntuación en Firestore si la racha es superior
    public void actualizarPuntuacionSiSuperior( int nuevaPuntuacion) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // consultamos el documento del usuario por el campo "nick"
        Query query = db.collection("usuarios").whereEqualTo("nick", usuarioLogueado).limit(1);

        // ejecutamos la consulta
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    // obtemenos el documento del usuario
                    DocumentReference usuarioRef = document.getReference();

                    // obtenemos el valor actual de la puntuación
                    Integer puntuacionActual = document.getLong("puntuacion").intValue();

                    // Verificamos si la nueva puntuación es superior a la actual
                    if (puntuacionActual == null || nuevaPuntuacion > puntuacionActual) {
                        // actualizamos la puntuación si es superior
                        usuarioRef.update("puntuacion", nuevaPuntuacion);
                    }
                }
            } else {

            }
        });
    }













}
