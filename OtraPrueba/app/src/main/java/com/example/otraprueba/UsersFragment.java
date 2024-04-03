package com.example.otraprueba;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private static final String TAG = "UsersFragment";
    private RecyclerView recyclerViewUsuarios;
    private FirebaseFirestore db;
    private CollectionReference referencia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        // Inicializamos Firebase Firestore
        db = FirebaseFirestore.getInstance();
        referencia = db.collection("usuarios");

        // Configuramos el RecyclerView
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsers);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtenemos datos de usuarios desde Firestore y configurar el adaptador
        getUsersDesdeFireBase();

        return view;
    }

    private void getUsersDesdeFireBase() {
        referencia.orderBy("puntuacion", Query.Direction.DESCENDING) // Ordenar por puntuacion de forma descendente
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> userList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            UserAdapter adapter = new UserAdapter(userList);
                            recyclerViewUsuarios.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error, no se ha podido obtener la información requerida: ", task.getException());
                        }
                    }
                });
    }
}
