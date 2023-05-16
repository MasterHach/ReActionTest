package com.alex.reactiontest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import com.alex.reactiontest.ContainerActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alex.reactiontest.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoadFragment extends Fragment {
    private ProgressBar progressBar;
    public NavController controller;

    public boolean is_logged = false;

    Bundle lol;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_load, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lol = savedInstanceState;
        progressBar = view.findViewById(R.id.progressBar);
        controller = NavHostFragment.findNavController(this);


        SharedPreferences mSettings = getActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        is_logged = mSettings.getBoolean("is_logged", false);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("logged_uid", ".");
        editor.apply();



        new LoadingTask().execute();
    }

    private class LoadingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Имитируем загрузку с помощью цикла
            for (int i = 0; i < 65; i++) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Обновляем ProgressBar каждые 50 мс
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Устанавливаем прогресс загрузки
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Закрываем экран загрузки и переходим на главный экран
            controller.popBackStack();
            NavOptions options = new NavOptions.Builder()
                    .build();
            controller.navigate(R.id.loginFragment2, lol, options);
//            if (is_logged) {
//                controller.navigate(R.id.mainMenuFragment, lol, options);
//            } else {
//                controller.navigate(R.id.loginFragment2, lol, options);
//            }
        }
    }


}