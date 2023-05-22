package com.alex.reactiontest.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alex.reactiontest.ContainerActivity;
import com.alex.reactiontest.R;
import com.alex.reactiontest.entities.User;

import java.util.Random;

public class ReactionGameFragment extends Fragment {

    private Button btnRestart;
    private TextView tvCounter;
    private TextView tvGameOver;
    private FrameLayout frameLayout;

    private TextView tvTemp;

    Thread something;

    SharedPreferences mSettings;

    private int counter = 0;
    private boolean gameOver = false;

    // private CountDownTimer countDownTimer;
    private long dotAppearInterval = 1500;
    private int temp = 1;

    private final int dotSizeMin = 100;
    private final int dotSizeMax = 200;
    private final float dotAlphaMin = 0.2f;
    private final float dotAlphaMax = 0.8f;

    private Handler handler = new Handler();
    private Handler temp_handler = new Handler();
    private Runnable dotRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reaction_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRestart = view.findViewById(R.id.btnRestart);
        tvCounter = view.findViewById(R.id.tvCounter);
        tvGameOver = view.findViewById(R.id.tvGameOver);
        frameLayout = view.findViewById(R.id.frameLayout);
        tvTemp = view.findViewById(R.id.tvTemp);

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRestart.setEnabled(false);
                tvGameOver.setVisibility(View.GONE);
                startDotAnimation();
                temp_handler.postDelayed(runnable, 10000);
                gameOver = false;
                counter = 0;
                temp = 1;
                dotAppearInterval = 1500;
                tvCounter.setText("Count: " + counter);
                tvTemp.setText("Temp: " + temp);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(dotRunnable);
        //countDownTimer.cancel();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dotAppearInterval -= dotAppearInterval / 5;
            temp += 1;
            tvTemp.setText("Temp: " + temp);
            temp_handler.postDelayed(runnable, 10000);
        }
    };

    private void startDotAnimation() {
        dotRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d("game over", String.valueOf(gameOver));
                if (!gameOver) {
                    createDot();
                    handler.postDelayed(this, dotAppearInterval);

                } else {
                    endGame();
                }

            }
        };
        handler.postDelayed(dotRunnable, 500);
    }

//    private void startDotAnimation() {
//        countDownTimer = new CountDownTimer(Long.MAX_VALUE, dotBetweenInterval) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                Log.d("dot clicked before", String.valueOf(gameOver));
//                if (!gameOver) {
//                    //dotClicked = false;
//                    //Log.d("dot clicked before", String.valueOf(dotClicked));
//                    createDot();
//                    //Log.d("dot clicked after", String.valueOf(dotClicked));
//                } else {
//                    Log.d("dot clicked after", String.valueOf(gameOver));
//                    endGame();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                //Log.d("dot clicked after", String.valueOf(gameOver));
////                if (!dotClicked) {
////                    endGame();
////                }
//            }
//        };
//        countDownTimer.start();
//    }

    private void createDot() {
        gameOver = true;
        View dotView = new View(requireContext());
        dotView.setBackgroundResource(R.drawable.dot_background);
        int dotSize = dotSizeMax;
        dotView.setAlpha(dotAlphaMax);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dotSize, dotSize);
        layoutParams.leftMargin = getRandomInt(0, frameLayout.getWidth() - dotSize);
        layoutParams.topMargin = getRandomInt(0, frameLayout.getHeight() - dotSize);
        dotView.setOnClickListener(v -> {
                frameLayout.removeView(v);
                gameOver = false;
                incrementCounter();
        });
        frameLayout.addView(dotView, layoutParams);
        dotView.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(dotAppearInterval)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        frameLayout.removeView(dotView);
                    }
                })
                .start();
    }

    private void incrementCounter() {
        counter++;
        tvCounter.setText("Count: " + counter);
    }

    private int getRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private float getRandomFloat(float min, float max) {
        Random random = new Random();
        return random.nextFloat() * (max - min) + min;
    }

    private void endGame() {
        //countDownTimer.cancel();
        Thread something = new Thread() {
            @Override
            public void run() {
                SharedPreferences mSettings = getActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);

                String this_uid = mSettings.getString("logged_uid", ".");
                //Log.d("my uid", this_uid);
                int best_score = ContainerActivity.userDao.getUserByUid(this_uid).best_score;

                if (counter > best_score) {
                    ContainerActivity.userDao.updateBestScore(this_uid, counter);
                }

            }
        };
        something.start();
        tvGameOver.setVisibility(View.VISIBLE);
        btnRestart.setEnabled(true);
    }
}
