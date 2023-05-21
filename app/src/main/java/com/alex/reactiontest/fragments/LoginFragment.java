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
import android.view.MenuItem;
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

import java.util.List;

import javax.xml.transform.Templates;

public class LoginFragment extends Fragment {
    private EditText email_field, password_field;
    private FirebaseAuth mAuth;
    public NavController controller;
    public NavOptions options;
    private Bundle bundle;
    private ProgressBar progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.nice_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = savedInstanceState;
        mAuth = FirebaseAuth.getInstance();
        email_field = view.findViewById(R.id.edit_email);
        password_field = view.findViewById(R.id.edit_password);
        Button login_button = view.findViewById(R.id.login_button);
        Button google_button = view.findViewById(R.id.google_button_reg);
        progressbar = view.findViewById(R.id.progressBar);
        TextView go_to_register = view.findViewById(R.id.go_to_register);

        controller = NavHostFragment.findNavController(this);
        options = new NavOptions.Builder()
                .build();
        go_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                controller.popBackStack();
                controller.navigate(R.id.registerFragment, bundle, options);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginUserAccount();

            }
        });
        google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signInWithGoogle();
                controller.popBackStack();
                controller.navigate(R.id.mainMenuFragment, bundle, options);
            }
        });
    }

    private void loginUserAccount()
    {
        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = email_field.getText().toString();
        password = password_field.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
            return;
        }
        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Login successful!!",
                                Toast.LENGTH_LONG).show();
                        // hide the progress bar
                        progressbar.setVisibility(View.GONE);
                        // if sign-in is successful
                        // intent to home activity
                        controller.popBackStack();
                        controller.navigate(R.id.mainMenuFragment, bundle, options);

                    }
                    else {
                        // sign-in failed
                        Toast.makeText(getContext(), "Login failed!!",
                                        Toast.LENGTH_LONG).show();
                        // hide the progress bar
                        progressbar.setVisibility(View.GONE);
                    }
                }
            });
    }
}