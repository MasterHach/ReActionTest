package com.alex.reactiontest;

import static android.opengl.ETC1.getWidth;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class MemoryGameActivity extends AppCompatActivity {
    private int numDots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        // генерируем случайное число точек
        numDots = (int) (Math.random() * 9) + 7;
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("users");
        Button checkButton = findViewById(R.id.check_button);
        EditText numDotsEditText = findViewById(R.id.num_dots_edit_text);
        // создаем и добавляем на экран точки
        ViewGroup dotsLayout = findViewById(R.id.dots_layout);
        for (int i = 0; i < numDots; i++) {
            Circle dot = new Circle(this);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
            dot.setRandom();
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


                String input = numDotsEditText.getText().toString();
                int guess;
                try {
                    guess = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    guess = -1;
                }
                if (guess == numDots) {
                    //myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("score").setValue(numDots);
                    Toast.makeText(MemoryGameActivity.this, "Вы выиграли! Good!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MemoryGameActivity.this, "Вы проиграли!", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(MemoryGameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
