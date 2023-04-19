package com.alex.reactiontest;

import static android.opengl.ETC1.getWidth;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MemoryGameActivity extends AppCompatActivity {
    private int numDots;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        // генерируем случайное число точек
        numDots = (int) (Math.random() * 9) + 7;
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseAuth mauth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mauth.getCurrentUser();

        Button checkButton = findViewById(R.id.check_button);
        EditText numDotsEditText = findViewById(R.id.num_dots_edit_text);
        // создаем и добавляем на экран точки
        ViewGroup dotsLayout = findViewById(R.id.dots_layout);
        for (int i = 0; i < numDots; i++) {
            Circle dot = new Circle(this);
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
                Intent intent = new Intent(MemoryGameActivity.this, MainActivity.class);
                String input = numDotsEditText.getText().toString();
                int guess;
                try {
                    guess = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    guess = -1;
                }
                if (guess == numDots) {
                    if (currentUser != null) {
                        uid = currentUser.getUid();
                        DatabaseReference myRef = database.getReference("users")
                                .child(uid)
                                .child("memory_game_counter");
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Long kk = snapshot.getValue(Long.class);
                                Log.d("score", String.valueOf(kk));
                                myRef.setValue(kk + 1);

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
                    Toast.makeText(MemoryGameActivity.this, "Вы выиграли! Good!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MemoryGameActivity.this, "Вы проиграли!", Toast.LENGTH_SHORT).show();
                }

                startActivity(intent);
            }
        });
    }
}
