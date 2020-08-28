package com.example.madcamp_game.UI.Game;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.R;
import com.example.madcamp_game.UI.Select.SelectActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class PausedActivity extends Activity {

    private io.socket.client.Socket mSocket;
    MediaPlayer mp_background;
    String counterpart;
    ConsoleActivity CA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_paused);
        CA = (ConsoleActivity) ConsoleActivity.activity;

        mSocket = ((LoginActivity) LoginActivity.context).mSocket;
        mp_background = ((LoginActivity) LoginActivity.context).mp;

        ImageView splashGif = (ImageView )findViewById(R.id.iv_pause);
        Glide.with(this).load(R.raw.pause_gif).into(splashGif);

        Intent receivedIntent = getIntent();
        counterpart = receivedIntent.getStringExtra("Nickname");

        mp_background.start();
        setResumeListening(counterpart);
        setTerminateListening(counterpart);

    }

    private void setResumeListening(final String Nickname) {

        mSocket.on("resume", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    if (messageJson.getString("Nickname").equals(Nickname)) {
                        mp_background.pause();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setTerminateListening(final String Nickname) {

        mSocket.on("terminate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    if (messageJson.getString("Nickname").equals(Nickname)) {
                        CA.finish();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
