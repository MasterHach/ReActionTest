package com.alex.reactiontest.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alex.reactiontest.R;
import com.alex.reactiontest.UsersAdapter;
import com.alex.reactiontest.entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BestPlayersFragment extends Fragment {

    private UsersAdapter usersAdapter;
    private List<User> userList;
    private TextView error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_best_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        error = view.findViewById(R.id.tvError);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        userList = new ArrayList<>();
        usersAdapter = new UsersAdapter(userList);
        recyclerView.setAdapter(usersAdapter);

        FirebaseDatabase.getInstance("https://reaction-bc351-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (userList.size() < 10) {
                                userList.add(user);
                            } else {
                                break;
                            }
                        }
                        userList.sort(new Comparator<User>() {
                            @Override
                            public int compare(User user1, User user2) {
                                // Сначала сортировка по лучшему счету (bestScore) в порядке убывания
                                int scoreComparison = Integer.compare(user1.best_score, user2.best_score);
                                if (scoreComparison != 0) {
                                    return scoreComparison;
                                }

                                double percent1 = 0.0;
                                if (user1.total_games != 0) {
                                    percent1 = (double) user1.positive_games / user1.total_games * 100;
                                }

                                double percent2 = 0.0;
                                if (user2.total_games != 0) {
                                    percent2 = (double) user2.positive_games / user2.total_games * 100;
                                }

                                // Затем сортировка по проценту побед (winPercentage) в порядке убывания
                                return Double.compare(percent1, percent2);
                            }
                        });
                        Collections.reverse(userList);
                        usersAdapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("error", String.valueOf(databaseError));
                        // Обработка ошибки при получении данных из Firebase Realtime Database
                        error.setVisibility(View.VISIBLE);
                    }
                });

    }
}