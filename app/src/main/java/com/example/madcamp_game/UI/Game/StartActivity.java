package com.example.madcamp_game.UI.Game;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.madcamp_game.ForcedTerminationService;
import com.example.madcamp_game.LoadingActivity;
import com.example.madcamp_game.Login.NicknameActivity;
import com.example.madcamp_game.R;
import com.example.madcamp_game.UI.Select.SelectActivity;
import com.example.madcamp_game.UI.Status.StatusActivity;

public class StartActivity extends AppCompatActivity {

    TextView tv_start;
    Animation anim;
    Button btn_start;
    ImageButton btn_status;
    ImageView iv_start;
    String Win, Lose, Profile;

    public String ID, Nickname;
    public static Context context;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        context = this;

        tv_start = findViewById(R.id.tv_start);
        btn_start = findViewById(R.id.btn_start);
        btn_status = findViewById(R.id.btn_status);

        mp = MediaPlayer.create(this,R.raw.sound_button);

        Intent receivedIntent = getIntent();
        // MyID
        ID = receivedIntent.getStringExtra("ID");
        Nickname = receivedIntent.getStringExtra("Nickname");
        Win = receivedIntent.getStringExtra("Win");
        Lose = receivedIntent.getStringExtra("Lose");
        Profile = receivedIntent.getStringExtra("Profile");

        startService(new Intent(StartActivity.this , ForcedTerminationService.class));

        System.out.println("StartActivity : " + ID + Nickname + Win + Lose);

        btn_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mp.start();
                Intent intent = new Intent(StartActivity.this, SelectActivity.class);
                intent.putExtra("ID",ID);
                intent.putExtra("Nickname",Nickname);
                intent.putExtra("Win",Win);
                intent.putExtra("Lose",Lose);

                startActivity(intent);
            }
        });

        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mp.start();
                Intent intent = new Intent(StartActivity.this, StatusActivity.class);
                intent.putExtra("Profile",Profile);
                intent.putExtra("Nickname",Nickname);
                intent.putExtra("Win",Win);
                intent.putExtra("Lose",Lose);

                startActivity(intent);
            }
        });

        iv_start = (ImageView)findViewById(R.id.iv_start);
        Glide.with(this).load(R.raw.start_loop).into(iv_start);

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(800);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        tv_start.startAnimation(anim);

    }


}
