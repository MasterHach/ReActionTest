package com.alex.reactiontest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alex.reactiontest.Circle;
import com.alex.reactiontest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MemoryGameFragment extends Fragment {

    private int numDots;
    private String uid;
    public NavController controller;
    public NavOptions options;
    Bundle lol;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_memory_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = NavHostFragment.findNavController(this);
        options = new NavOptions.Builder()
                .build();
        numDots = (int) (Math.random() * 9) + 7;
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseAuth mauth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mauth.getCurrentUser();

        Button checkButton = view.findViewById(R.id.check_button);
        EditText numDotsEditText = view.findViewById(R.id.num_dots_edit_text);
        // создаем и добавляем на экран точки
        ViewGroup dotsLayout = view.findViewById(R.id.dots_layout);
        for (int i = 0; i < numDots; i++) {
            Circle dot = new Circle(getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            //dot.setRandom();
            dotsLayout.addView(dot, layoutParams);
            dot.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dotsLayout.removeView(dot);
                    checkButton.setVisibility(View.VISIBLE);
                    numDotsEditText.setVisibility(View.VISIBLE);
                }
            }, 2000);
        }

        // устанавливаем обработчик для кнопки "Проверить"


        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.mainMenuFragment, lol, options);
                String input = numDotsEditText.getText().toString();
                int guess;
                try {
                    guess = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    guess = -1;
                }

                    if (currentUser != null) {
                        uid = currentUser.getUid();
                        DatabaseReference myRef = database.getReference("users")
                                .child(uid);

                        int finalGuess = guess;
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Long positive_games = snapshot.child("positive_games").getValue(Long.class);
                                Long total_games = snapshot.child("total_games").getValue(Long.class);
                                Log.d("score", String.valueOf(positive_games));
                                if (finalGuess == numDots) {
                                    myRef.child("positive_games").setValue(positive_games + 1);
                                    Toast.makeText(getContext(), "Вы выиграли! Good!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Вы проиграли!", Toast.LENGTH_SHORT).show();
                                }
                                myRef.child("total_games").setValue(total_games + 1);

//                                if (kk == null) {
//
//                                    myRef.setValue(1);
//                                } else {
//                                    myRef.setValue((int)kk + 1);
//                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }



            }
        });

    }
}