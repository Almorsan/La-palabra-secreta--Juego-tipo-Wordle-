package com.example.otraprueba;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> listaUsuarios;

    public UserAdapter(List<User> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = listaUsuarios.get(position);
        holder.textViewNick.setText(user.getNick());
        holder.textViewPuntuacion.setText(String.valueOf(user.getPuntuacion()));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNick;
        TextView textViewPuntuacion;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNick = itemView.findViewById(R.id.textViewNick);
            textViewPuntuacion = itemView.findViewById(R.id.textViewPuntuacion);
        }
    }
}


