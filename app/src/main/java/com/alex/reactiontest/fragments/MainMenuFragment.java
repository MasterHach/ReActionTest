package com.alex.reactiontest.fragments;
import com.alex.reactiontest.ContainerActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.reactiontest.R;
import com.alex.reactiontest.entities.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainMenuFragment extends Fragment {

    private TextView game_percentage;
    private TextView reaction_count;
    private TextView nickname;
    public NavController controller;
    public NavOptions options;
    Bundle bundle;
    SharedPreferences mSettings;
    DatabaseReference myRef;
    String finalUsers_uid;
    DecimalFormat df = new DecimalFormat("#.#");

    private ConnectivityManager.NetworkCallback networkCallback;
    private Snackbar snackbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nice_main_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> this_user = ContainerActivity.userDao.getAllUsers();
                for (int i = 0; i < this_user.size(); i++) {
                    Log.d("user x", "" + this_user.get(i).email
                            + " " + this_user.get(i).nickname
                            + " " + this_user.get(i).best_score
                            + " " + this_user.get(i).total_games
                            + " " + this_user.get(i).positive_games
                            + " " + this_user.get(i).last_update
                            + " " + this_user.get(i).uid);
                }
            }
        }).start();

        snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", v -> snackbar.dismiss());
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // Internet connection is available
                snackbar.dismiss();
            }
            @Override
            public void onLost(@NonNull Network network) {
                // Internet connection is lost
                snackbar.show();
            }
        };
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);

        game_percentage = view.findViewById(R.id.best_percent);
        reaction_count = view.findViewById(R.id.best_score);
        nickname = view.findViewById(R.id.nickname);
        Button button_logout = view.findViewById(R.id.exit);
        Button playMemory = view.findViewById(R.id.memory_game);
        Button playReaction = view.findViewById(R.id.reaction_game);
        Button changeNick = view.findViewById(R.id.change_nick);
        Button achievements = view.findViewById(R.id.trophy_btn);

        achievements.setOnClickListener(v -> controller.navigate(R.id.bestPlayersFragment, bundle, options));

        mSettings = requireActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();

        controller = NavHostFragment.findNavController(this);
        options = new NavOptions.Builder()
                .build();

        FirebaseDatabase database = FirebaseDatabase
                .getInstance("https://reaction-bc351-default-rtdb.europe-west1.firebasedatabase.app");
        myRef = database.getReference("users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String users_uid;

        if (currentUser != null) {
            editor.putBoolean("is_logged", true);
            users_uid = currentUser.getUid();
            editor.putString("logged_uid", users_uid);
            editor.apply();
        } else {
            users_uid = mSettings.getString("logged_uid", ".");
        }
        finalUsers_uid = users_uid;
        Log.d("this uid", finalUsers_uid);

        User this_user = get_user_from_room(finalUsers_uid);
        User fire_user = get_user_from_firebase(finalUsers_uid);

        if (this_user == null) {
            add_user_to_room(fire_user);
        }

        if (!check_actual_update(this_user, fire_user)) {
            update_firebase_user(this_user);
        } else {
            update_room_user(fire_user, finalUsers_uid);
        }
        set_data_on_fragment(this_user);

        changeNick.setOnClickListener(view1 -> showDialog());
        playMemory.setOnClickListener(view12 -> controller.navigate(R.id.memoryGameFragment, bundle, options));
        playReaction.setOnClickListener(view13 -> controller.navigate(R.id.reactionGameFragment, bundle, options));
        button_logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "You have been logged out",
                            Toast.LENGTH_LONG).show();
            editor.putBoolean("is_logged", false);
            editor.apply();
            controller.navigate(R.id.loginFragment2, bundle, options);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the network callback
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkCallback);
        // Dismiss the Snackbar if it's shown
        snackbar.dismiss();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_nickname);

        EditText change_this_nick = dialog.findViewById(R.id.change_this_nickname);
        Button ok = dialog.findViewById(R.id.button_ok);

        ok.setOnClickListener(v -> {
            String nick = change_this_nick.getText().toString();
            if (nick.length() > 15 || nick.length() < 2 ) {
                Toast.makeText(getContext(), "Nick mut have from 2 to 15 symbols"
                        + nick.length(), Toast.LENGTH_SHORT).show();
            } else {
                set_nickname(nick);
                myRef.child(finalUsers_uid).child("nickname").setValue(nick);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    // set nickname to room and fragment
    public void set_nickname(String nick) {
        new Thread(() -> {
            Date date = new Date();
            ContainerActivity.userDao.updateNick(finalUsers_uid, nick);
            ContainerActivity.userDao.setLastUpdate(finalUsers_uid, date.getTime());
            requireActivity().runOnUiThread(() -> nickname.setText(nick));
        }).start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    // load data from firebase to room
    public User get_user_from_firebase(String user_uid) {
        final User[] fire_user = {new User()};
         myRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 fire_user[0] = snapshot.child(user_uid).getValue(User.class);
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {
                 //add logs
             }
         });
         return fire_user[0];
    }
    // check room and firebase, choose latest data
    public boolean check_actual_update(User room_user, User fire_user) {
        return room_user.last_update <= fire_user.last_update;
    }
    public void update_firebase_user(User this_user) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.child(this_user.uid).child("nickname")
                        .setValue(this_user.nickname);
                myRef.child(this_user.uid).child("best_score")
                        .setValue(this_user.best_score);
                myRef.child(this_user.uid).child("positive_games")
                        .setValue(this_user.positive_games);
                myRef.child(this_user.uid).child("total_games")
                        .setValue(this_user.total_games);
                Date date = new Date();
                myRef.child(this_user.uid).child("last_update")
                        .setValue(date.getTime());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //add logs
            }
        });
    }
    public void add_user_to_room(User new_user) {
        new Thread(() -> {
            try {
                ContainerActivity.userDao.insert(new_user);
            } catch (Exception e) {
                // add logs
            }
        }).start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public User get_user_from_room(String user_uid) {
        final User[] this_user = new User[1];
        new Thread(() -> {
            try {
                this_user[0] = ContainerActivity.userDao.getUserByUid(user_uid);
            } catch (Exception e) {
               // add logs
            }
        }).start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this_user[0];
    }
    public void set_data_on_fragment(User this_user) {
        String winRate = calculate_winRate(this_user.total_games, this_user.positive_games);
        String winRateText = getString(R.string.winrate_text, winRate) + "%";
        String bestScoreText = getString(R.string.best_score_text, String.valueOf(this_user.best_score));
        game_percentage.setText(winRateText);
        reaction_count.setText(bestScoreText);
        nickname.setText(this_user.nickname);
    }

    public String calculate_winRate(int total_games, int positive_games) {
        String formattedValue = "0.0";
        double percent;
        if (total_games != 0) {
            percent = (double) positive_games / total_games * 100;
            formattedValue = df.format(percent);
        }
        return formattedValue;
    }

    public void update_room_user(User fire_user, String user_uid) {
        new Thread(() -> {
            try {
                ContainerActivity.userDao.updateBestScore(user_uid, fire_user.best_score);
                ContainerActivity.userDao.updateTotalScore(user_uid, fire_user.total_games);
                ContainerActivity.userDao.updatePositiveScore(user_uid, fire_user.positive_games);
                ContainerActivity.userDao.updateNick(user_uid, fire_user.nickname);
                ContainerActivity.userDao.setLastUpdate(user_uid, fire_user.last_update);
            } catch (Exception e) {
                // add logs
            }
        }).start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}