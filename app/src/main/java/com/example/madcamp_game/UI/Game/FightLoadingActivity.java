package com.example.madcamp_game.UI.Game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.madcamp_game.R;

public class FightLoadingActivity extends AppCompatActivity {

    String ID,counterpart;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_loading);

        Intent received_intent = getIntent();
        ID = received_intent.getStringExtra("ID");
        counterpart = received_intent.getStringExtra("counterpart");

        ImageView splashGif = (ImageView)findViewById(R.id.fight_loading);
        Glide.with(this).load(R.raw.fight_loading2).into(splashGif);
        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run(){
                Intent intent = new Intent(getBaseContext(), ConsoleActivity.class);
                intent.putExtra("ID",ID);
                intent.putExtra("counterpart",counterpart);
                startActivity(intent);
                finish();
            }
        }, 3500);
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}
