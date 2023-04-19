package com.alex.reactiontest;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.util.FFMPEGDownloader;
import be.tarsos.dsp.util.fft.FFT;


public class MainActivity extends AppCompatActivity {
    private TextView this_text;
    private TextView mem_count;
    private Button button_logout;
    private Button playMemory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this_text = findViewById(R.id.this_text);
        button_logout = findViewById(R.id.btn_out);
        playMemory = findViewById(R.id.btn_mem_game);
        mem_count = findViewById(R.id.text_mem_count);
        View popupGroup = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        PopupWindow popupWindow = new PopupWindow(popupGroup, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String lolkek = currentUser.getUid();
        playMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemoryGameActivity.class);
                startActivity(intent);
            }
        });
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hlo, World!");
        this_text.setText("lolkek");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long lolkek2 = snapshot.child(lolkek).child("memory_game_counter").getValue(Long.class);
                mem_count.setText("Ваш счетчик выйгранных игр на память: " + lolkek2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mem_count.setText("Ваш счетчик выйгранных игр на память: 0");
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),
                                "You have been logged out",
                                Toast.LENGTH_LONG)
                        .show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}




//        new AndroidFFMPEGLocator(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                FFMPEGDownloader downloader = new FFMPEGDownloader();
//                //downloader.ffmpegBinary();
//                String tmpDir = System.getProperty("java.io.tmpdir");
//                Log.d("tmpDir", tmpDir);
//                String file = "G:\\shashki\\ReActionTest\\app\\src\\main\\res\\raw\\music.mp3";
//                AudioDispatcher adp =  AudioDispatcherFactory.fromPipe(file, 44100, 4096, 0);
//                OnsetHandler handler = new OnsetHandler() {
//                    @Override
//                    public void handleOnset(double v, double v1) {
//                        System.out.println(v + " " + v1);
//                    }
//                };
//                AudioProcessor processor = new PercussionOnsetDetector(44100, 2048, handler,20,1);
//                adp.addAudioProcessor(processor);
//                adp.run();
//            }
//        }).start();
//        new AndroidFFMPEGLocator(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                File externalStorage = Environment.getExternalStorageDirectory();
//                File sourceFile = new File(externalStorage.getAbsolutePath() , "/440.mp3");
//                final int bufferSize = 4096;
//                final int fftSize = bufferSize / 2;
//                final int sampleRate = 44100;
//                AudioDispatcher audioDispatcher;
//                audioDispatcher = AudioDispatcherFactory.fromPipe(sourceFile.getAbsolutePath(), sampleRate, bufferSize, 0);
//                audioDispatcher.addAudioProcessor(new AudioProcessor() {
//                    FFT fft = new FFT(bufferSize);
//                    final float[] amplitudes = new float[fftSize];
//                    @Override
//                    public boolean process(AudioEvent audioEvent) {
//                        float[] audioBuffer = audioEvent.getFloatBuffer();
//                        fft.forwardTransform(audioBuffer);
//                        fft.modulus(audioBuffer, amplitudes);
//                        for (int i = 0; i < amplitudes.length; i++) {
//                            Log.d("lol", String.format("Amplitude at %3d Hz: %8.3f", (int) fft.binToHz(i, sampleRate) , amplitudes[i]));
//                        }
//                        return true;
//                    }
//                    @Override
//                    public void processingFinished() {
//                    }
//                });
//                audioDispatcher.run();
//            }
//        }).start();