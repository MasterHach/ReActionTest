package com.alex.reactiontest.fragments;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterFragment extends Fragment {
    private EditText emailTextView, passwordTextView;
    private Button Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private TextView loginIntent;

    private Button googleBtn;
    private long childCount;
    public NavController controller;
    public NavOptions options;

    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> signInLauncher;


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
        googleBtn = view.findViewById(R.id.google_button_reg);
        controller = NavHostFragment.findNavController(this);
        options = new NavOptions.Builder()
                .build();

        google_sing_in();



        loginIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                controller.popBackStack();
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

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
                controller.popBackStack();
                controller.navigate(R.id.mainMenuFragment, lol, options);
            }
        });

    }

    private void google_sing_in() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        signInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                firebaseAuthWithGoogle(account);
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("first step", ",,");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users");
                            String lolkek = user.getUid();
                            User google_user = new User();
                            google_user.uid = lolkek;
                            google_user.nickname = "Anonymous";
                            google_user.email = account.getEmail();
                            google_user.best_score = 0;
                            google_user.total_games = 0;
                            google_user.positive_games = 0;
                            Thread something = new Thread() {
                                @Override
                                public void run() {
                                    User this_user = new User();
                                    Log.d("not kaif", String.valueOf(this_user));
                                    try {
                                        this_user = ContainerActivity.userDao.getUserByUid(lolkek);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (this_user == null) {

                                        ContainerActivity.userDao.insert(google_user);
                                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                childCount = snapshot.getChildrenCount();
                                                myRef.child(lolkek).setValue(google_user);
                                                Log.d("snapka", String.valueOf(childCount));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }

                                }
                            };
                            something.start();

                        }
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
                            controller.popBackStack();
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