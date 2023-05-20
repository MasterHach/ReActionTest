package com.alex.reactiontest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.reactiontest.entities.User;

import java.text.DecimalFormat;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> userList;

    public UsersAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        Log.d("user: ", String.valueOf(user));
        holder.textViewUsername.setText(user.nickname);
        holder.bestScore.setText("" + user.best_score);

        DecimalFormat df = new DecimalFormat("#.#");
        String formattedValue = "0.0";
        double percent = 0.0;
        if (user.total_games != 0) {
            percent = (double) user.positive_games / user.total_games * 100;
            formattedValue = df.format(percent);
        }
        holder.WinRate.setText(formattedValue + "%");
        // Установите остальные данные пользователя в соответствующие визуальные элементы
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewUsername;
        public TextView bestScore;
        public TextView WinRate;


        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            bestScore = view.findViewById(R.id.tvBest);
            WinRate = view.findViewById(R.id.tvWinrate);
        }
    }
}