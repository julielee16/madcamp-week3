package com.example.madcamp_game.UI.Game;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.R;
import com.example.madcamp_game.UI.Select.SelectActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class PauseActivity extends Activity {

    Button btn_resume, btn_terminate;
    static MediaPlayer mp_click, mp_background;
    String MyNickname;
    private io.socket.client.Socket mSocket;
    ConsoleActivity CA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pause);

        mSocket = ((LoginActivity) LoginActivity.context).mSocket;
        mp_click = MediaPlayer.create(this,R.raw.sound_button);
        mp_background = ((LoginActivity) LoginActivity.context).mp;

        btn_resume = findViewById(R.id.btn_resume);
        btn_terminate = findViewById(R.id.btn_terminate);

        Intent receivedIntent = getIntent();
        MyNickname = receivedIntent.getStringExtra("Nickname");

        CA = (ConsoleActivity) ConsoleActivity.activity;

        btn_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp_click.start();
                sendResume(MyNickname);
            }
        });

        btn_terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTerminate(MyNickname);
            }
        });
    }

    void sendTerminate(String ID) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("Nickname",MyNickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("terminate", jsonData);
        mp_click.start();
        mp_background.start();
        Intent intent = new Intent(PauseActivity.this, SelectActivity.class);
        startActivity(intent);
        CA.finish();
        finish();
    }

    void sendResume(String ID) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("Nickname",MyNickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("resume", jsonData);
        finish();
    }
}
