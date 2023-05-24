package com.alextimofee.timingsignaljava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    AudioManager audioManager;
    Handler taskHandler = new android.os.Handler();

    TextToSpeech textToSpeech;
    Button buttonStart;
    Button buttonClear;
    ImageView shower;

    int maxVolume, currentVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStart = findViewById(R.id.button_start);
        buttonClear = findViewById(R.id.button_clear);
        shower = findViewById(R.id.shower);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        Locale locale = new Locale("RU");
        textToSpeech = new TextToSpeech(getApplicationContext(), i -> {
            if (i != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(locale);
            }
        });

        buttonStart.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Погнали", Toast.LENGTH_SHORT).show();
            startSpeak();
        });

        buttonClear.setOnClickListener(view -> {
            Toast
                .makeText(getApplicationContext(), "C лёгким паром :)", Toast.LENGTH_SHORT)
                .show();
            stopSpeak();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSpeak();
    }

    private Runnable speaking = new Runnable() {
        @Override
        public void run() {
            String hour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR));
            String minute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));

            String toSpeech = hour + ":" + minute;
            textToSpeech.speak(toSpeech, TextToSpeech.QUEUE_FLUSH, null);

            taskHandler.postDelayed(speaking, 300000);
        }
    };

    private void startSpeak() {
        shower.setVisibility(View.VISIBLE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI);
        speaking.run();
    }

    private void stopSpeak() {
        shower.setVisibility(View.GONE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != currentVolume) {
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI
            );
        }
        taskHandler.removeCallbacks(speaking);
    }
}