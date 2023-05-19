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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

public class MainMenuFragment extends Fragment {

    private TextView game_percentage;
    private TextView reaction_count;
    private TextView nickname;
    private Button button_logout;
    private Button playMemory;
    private Button changeNick;
    private Button playReaction;
    private Button achievements;

    public NavController controller;
    public NavOptions options;
    Bundle lol;
    SharedPreferences mSettings;

    DatabaseReference myRef;
    String finalUsers_uid;

    Thread something;

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

        snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
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
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);


        game_percentage = view.findViewById(R.id.best_percent);
        reaction_count = view.findViewById(R.id.best_score);
        nickname = view.findViewById(R.id.nickname);
        button_logout = view.findViewById(R.id.exit);
        playMemory = view.findViewById(R.id.memory_game);
        playReaction = view.findViewById(R.id.reaction_game);
        changeNick = view.findViewById(R.id.change_nick);
        achievements = view.findViewById(R.id.trophy_btn);

        mSettings = getActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();

        controller = NavHostFragment.findNavController(this);
        options = new NavOptions.Builder()
                .build();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String users_uid = ".";

        if (currentUser != null) {
            editor.putBoolean("is_logged", true);
            users_uid = currentUser.getUid();
            Log.d("current uid if internet", users_uid);
            //add_to_room(users_uid);
            editor.putString("logged_uid", users_uid);
            editor.apply();
            String a = mSettings.getString("logged_uid", ".");
            Log.d("super puper uid", a);
        } else {
            users_uid = mSettings.getString("logged_uid", ".");
            Log.d("super puper uid 2", users_uid);
        }
        finalUsers_uid = users_uid;

        User user = new User();
        if (ContainerActivity.isNetworkConnected()) {
            Log.d("is internet", "yes");
            Thread get_data_fire = new Thread() {
                @Override
                public void run() {


                    myRef.child(finalUsers_uid).get()
                            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot snapshot) {
                                    int total_int = 0;
                                    int positive_int = 0;
                                    int best_int = 0;
                                    Log.d("not kaif 2", "");
                                    DataSnapshot best_score = snapshot.child("best_score");
                                    String this_nickname = snapshot.child("nickname").getValue(String.class);
                                    String this_email = snapshot.child("email").getValue(String.class);
                                    DataSnapshot total_games = snapshot.child("total_games");
                                    if (total_games.getValue() != null) {
                                        total_int = total_games.getValue(Integer.class);
                                    }
                                    DataSnapshot positive_games = snapshot.child("positive_games");
                                    if (positive_games.getValue() != null) {
                                        positive_int = positive_games.getValue(Integer.class);
                                    }
                                    if (best_score.getValue() != null) {
                                        best_int = best_score.getValue(Integer.class);
                                    }

                                    user.uid = finalUsers_uid;
                                    user.nickname = this_nickname;
                                    user.email = this_email;
                                    user.best_score = best_int;
                                    user.total_games = total_int;
                                    user.positive_games = positive_int;

                                    something = new Thread() {
                                        @Override
                                        public void run() {
                                            User this_user = ContainerActivity.userDao.getUserByUid(finalUsers_uid);
                                            if (this_user == null) {
                                                Log.d("not kaif", user.uid);
                                                ContainerActivity.userDao.insert(user);

                                            }
                                            set_data_from_room(finalUsers_uid);

                                        }
                                    };
                                    something.start();
                                    //Log.d("my user", String.valueOf(user.uid));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("no user", "erfref");
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            set_data_from_room(finalUsers_uid);
                                        }
                                    }.start();
                                }
                            });


                }
            };
            get_data_fire.start();
        } else {
            new Thread() {
                @Override
                public void run() {
                    set_data_from_room(finalUsers_uid);
                }
            }.start();
        }
        changeNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        playMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.memoryGameFragment, lol, options);
            }
        });
        playReaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.reactionGameFragment, lol, options);
            }
        });
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(),
                                "You have been logged out",
                                Toast.LENGTH_LONG)
                        .show();
                editor.putBoolean("is_logged", false);
                editor.apply();
                controller.navigate(R.id.loginFragment2, lol, options);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> this_user = ContainerActivity.userDao.getAllUsers();
                for (int i = 0; i < this_user.size(); i++) {
                    Log.d("user x", "" + this_user.get(i).email
                            + " " + this_user.get(i).nickname
                            + " " + this_user.get(i).best_score
                            + " " + this_user.get(i).total_games
                            + " " + this_user.get(i).positive_games);
                }
            }
        }).start();

        //set_data_from_firebase();
        //set_data_from_room(finalUsers_uid);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Unregister the network callback
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkCallback);

        // Dismiss the Snackbar if it's shown
        snackbar.dismiss();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_nickname);

        EditText change_this_nick = (EditText) dialog.findViewById(R.id.change_this_nickname);
        Button ok = (Button) dialog.findViewById(R.id.button_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = change_this_nick.getText().toString();
                if (nick.length() > 15 || nick.length() < 2 ) {
                    Toast.makeText(getContext(), "Nick mut have from 2 to 15 symbols" + nick.length(), Toast.LENGTH_SHORT).show();
                } else {
//                    if (ContainerActivity.isNetworkConnected()) {
//                        change_nickname_by_firebase(nick);
//                    } else {
//                        change_nickname_by_room(nick);
//                    }
                    change_nickname_by_room(nick);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void change_nickname_by_room(String nick) {
        final String[] nick_from_room = new String[1];
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.child(finalUsers_uid).child("nickname").setValue(nick);
                nickname.setText(nick);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        new Thread() {
            @Override
            public void run() {
                ContainerActivity.userDao.updateNick(finalUsers_uid, nick);
                nick_from_room[0] = ContainerActivity.userDao.getUserByUid(finalUsers_uid).nickname;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nickname.setText(nick_from_room[0]);

                    }
                });
            }
        }.start();
    }

    public void set_data_from_room(String this_uid) {
        Log.d("uid" , this_uid);
                User this_user = ContainerActivity.userDao.getUserByUid(this_uid);
                String this_nick = this_user.nickname;
                int this_best_score = this_user.best_score;
                int this_positive_games = this_user.positive_games;
                int this_total_games = this_user.total_games;

                DecimalFormat df = new DecimalFormat("#.#");
                String formattedValue = "0.0";
                double percent = 0.0;
                if (this_total_games != 0) {
                    percent = (double) this_positive_games / this_total_games * 100;
                    formattedValue = df.format(percent);
                }
                String finalFormattedValue = formattedValue;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nickname.setText(this_nick);
                        game_percentage.setText("Memory game win rate: " + finalFormattedValue + "%");
                        reaction_count.setText("Best reaction game score: " + this_best_score);

                    }
                });


    }

    public void add_to_room(String users_uid) {
        String my_uid = users_uid;

        User user = new User();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.child(users_uid).child("uid").setValue(users_uid);
                int total_int = 0;
                int positive_int = 0;
                int best_int = 0;
                Log.d("not kaif 2", "");
                DataSnapshot best_score = snapshot.child(my_uid).child("best_score");
                String this_nickname = snapshot.child(my_uid).child("nickname").getValue(String.class);
                String this_email = snapshot.child(my_uid).child("email").getValue(String.class);
                DataSnapshot total_games = snapshot.child(my_uid).child("total_games");
                if (total_games.getValue() != null) {
                    total_int = total_games.getValue(Integer.class);
                }
                DataSnapshot positive_games = snapshot.child(my_uid).child("positive_games");
                if (positive_games.getValue() != null) {
                    positive_int = positive_games.getValue(Integer.class);
                }
                if (best_score.getValue() != null) {
                    best_int = best_score.getValue(Integer.class);
                }

                user.uid = my_uid;
                user.nickname = this_nickname;
                user.email = this_email;
                user.best_score = best_int;
                user.total_games = total_int;
                user.positive_games = positive_int;

                //

                Log.d("kaif", "" + user.email);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Thread() {
            @Override
            public void run() {
                User this_user = ContainerActivity.userDao.getUserByUid(my_uid);
                if (this_user == null) {
                    Log.d("not kaif", String.valueOf(this_user));
                    //ContainerActivity.userDao.delete(user);


                }
            }
        }.start();

    }

    public void set_data_from_firebase() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_int = 0;
                int positive_int = 0;
                int best_int = 0;
                DataSnapshot best_score = snapshot.child(finalUsers_uid).child("best_score");
                String this_nickname = snapshot.child(finalUsers_uid).child("nickname").getValue(String.class);
                DataSnapshot total_games = snapshot.child(finalUsers_uid).child("total_games");
                if (total_games.getValue() != null) {
                    total_int = total_games.getValue(Integer.class);
                }
                DataSnapshot positive_games = snapshot.child(finalUsers_uid).child("positive_games");
                if (positive_games.getValue() != null) {
                    positive_int = positive_games.getValue(Integer.class);
                }
                if (best_score.getValue() != null) {
                    best_int = best_score.getValue(Integer.class);
                }
                DecimalFormat df = new DecimalFormat("#.#");
                String formattedValue = "0.0";
                double percent = 0.0;
                if (total_int != 0) {
                    percent = (double) positive_int / total_int * 100;
                    formattedValue = df.format(percent);
                }
                Log.d("goog nick", "" + this_nickname + best_score + total_games + "" + finalUsers_uid);
                game_percentage.setText("Memory game win rate: " + formattedValue + "%");
                reaction_count.setText("Best reaction game score: " + best_int);
                nickname.setText(this_nickname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //    public void change_nickname_by_firebase(String nick) {
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                myRef.child(finalUsers_uid).child("nickname").setValue(nick);
//                nickname.setText(nick);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}