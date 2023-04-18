package com.alex.reactiontest;

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

public class MemoryGameActivity extends AppCompatActivity {
    private int numDots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        // генерируем случайное число точек
        numDots = (int) (Math.random() * 9) + 7;

        // создаем и добавляем на экран точки
        ViewGroup dotsLayout = findViewById(R.id.dots_layout);
        for (int i = 0; i < numDots; i++) {
            Circle dot = new Circle(this);
//            dot.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
//            dot.setBackgroundResource(R.color.red);
            int size = (int)getResources().getDimension(R.dimen.dot_size);
            int x = (int) (Math.random() * (dotsLayout.getWidth() - size));
            int y = (int) (Math.random() * (dotsLayout.getHeight() - size));
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //layoutParams.gravity = Gravity.CENTER;
            layoutParams.leftMargin = x;
            layoutParams.topMargin = y;
            Log.d("lolkek", String.valueOf(dotsLayout.getHeight()));
            Log.d("lolkek", String.valueOf(dotsLayout.getWidth()));
            //dot.setLayoutParams(layoutParams);
            dotsLayout.addView(dot, layoutParams);
            dot.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dotsLayout.removeView(dot);
                }
            }, 2000);
        }

        // устанавливаем обработчик для кнопки "Проверить"
        Button checkButton = findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText numDotsEditText = findViewById(R.id.num_dots_edit_text);
                String input = numDotsEditText.getText().toString();
                int guess;
                try {
                    guess = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    guess = -1;
                }
                if (guess == numDots) {
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
