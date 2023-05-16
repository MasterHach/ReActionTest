package com.alex.reactiontest.fragments;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.reactiontest.ContainerActivity;
import com.alex.reactiontest.R;
import com.alex.reactiontest.entities.User;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterFragment extends Fragment {
    private EditText emailTextView, passwordTextView;
    private Button Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private TextView loginIntent;
    private long childCount;
    public NavController controller;
    public NavOptions options;
    Bundle lol;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.nice_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = view.findViewById(R.id.edit_email);
        passwordTextView = view.findViewById(R.id.edit_password);
        Btn = view.findViewById(R.id.login_button);
        progressbar = view.findViewById(R.id.progressBar);
        loginIntent = view.findViewById(R.id.go_to_login);
        controller = NavHostFragment.findNavController(this);
        options = new NavOptions.Builder()
                .build();



        loginIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                controller.navigate(R.id.loginFragment2, lol, options);
            }
        });
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });

    }
    private void registerNewUser()
    {

        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String lolkek = currentUser.getUid();
                            User user = new User();
                            user.uid = lolkek;
                            user.nickname = "Anonymous";
                            user.email = email;
                            user.best_score = 0;
                            user.total_games = 0;
                            user.positive_games = 0;
                            new Thread() {
                                @Override
                                public void run() {
                                    ContainerActivity.userDao.insert(user);
                                }
                            }.start();

                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    childCount = snapshot.getChildrenCount();
                                    myRef.child(lolkek).setValue(user);
                                    Log.d("snapka", String.valueOf(childCount));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            Log.d("user count", String.valueOf(childCount));
                            Log.d("user count", String.valueOf(childCount));
                            Toast.makeText(getContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);
                            // if the user created intent to login activity
                            controller.navigate(R.id.mainMenuFragment, lol, options);
                        }
                        else {

                            // Registration failed
                            Toast.makeText(
                                            getContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}