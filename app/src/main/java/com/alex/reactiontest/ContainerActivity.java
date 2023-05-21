package com.alex.reactiontest;

import static androidx.fragment.app.FragmentManagerKt.commit;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.alex.reactiontest.dao.ErrorDao;
import com.alex.reactiontest.dao.UserDao;
import com.alex.reactiontest.database.AppDatabase;
import com.alex.reactiontest.fragments.LoadFragment;
import com.alex.reactiontest.fragments.LoginFragment;

public class ContainerActivity extends AppCompatActivity {

    AppDatabase db;
    public static UserDao userDao;
    public static ErrorDao errorDao;

    public static ConnectivityManager cm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        db = AppDatabase.create(this, false);
        userDao = db.userDao();
        errorDao = db.errorDao();

    }

    @Override
    protected void onStart() {
        super.onStart();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    public static boolean isNetworkConnected() {
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }



}
