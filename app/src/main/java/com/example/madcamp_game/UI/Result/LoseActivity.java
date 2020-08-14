package com.example.madcamp_game.UI.Result;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.madcamp_game.R;
import com.example.madcamp_game.UI.Game.ConsoleActivity;
import com.example.madcamp_game.UI.Select.SelectActivity;

public class LoseActivity extends Activity {

    ConsoleActivity CA;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lose);

        CA = (ConsoleActivity) ConsoleActivity.activity;

        mp = MediaPlayer.create(this,R.raw.sound_lose);
        mp.start();
    }

    //확인 버튼 클릭
    public void mOnClose(View v){

        Intent intent = new Intent(LoseActivity.this, SelectActivity.class);
        startActivity(intent);
        CA.finish();
        mp.stop();
        finish();
    }
}
