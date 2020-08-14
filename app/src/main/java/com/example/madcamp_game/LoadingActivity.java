package com.example.madcamp_game;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.Login.NicknameActivity;

public class LoadingActivity extends AppCompatActivity {

    Animation animation_hide, animation_show;
    ImageView Logo;
    ImageView splashGif;
    static MediaPlayer mp_typo, mp_drop, mp_title;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mp_typo = MediaPlayer.create(this,R.raw.sound_typo);
        mp_drop = MediaPlayer.create(this,R.raw.sound_drop);
        mp_title = MediaPlayer.create(this,R.raw.sound_title);


        LinearLayout black_layout = (LinearLayout) findViewById(R.id.black_layout);
        Logo = (ImageView) findViewById(R.id.loading_fake_logo);
        splashGif = (ImageView) findViewById(R.id.loading_fake_gif);
        Glide.with(this).load(R.raw.splash_coding).into(splashGif);

        Handler mHandler_typo = new Handler();
        mHandler_typo.postDelayed(new Runnable()  {
            public void run() {
                mp_typo.start();
            }
        }, 500);

        Handler mHandler_drop = new Handler();
        mHandler_drop.postDelayed(new Runnable()  {
            public void run() {
                mp_drop.start();
            }
        }, 4900);

        Handler mHandler_title = new Handler();
        mHandler_title.postDelayed(new Runnable()  {
            public void run() {
                mp_title.start();
            }
        }, 5800);

        animation_hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_hide);
        animation_show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_show);

        Logo.setAnimation(animation_hide);
        splashGif.setAnimation(animation_hide);
        black_layout.setAnimation(animation_show);

        splashAnimation();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void splashAnimation() {
        Animation animation_text_first = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_splash_textview_first);
        Animation animation_text_second = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_splash_textview_second);
        Animation animation_text_third = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_splash_textview_third);

        TextView splashTextView_first = findViewById(R.id.loading_real_text_first);
        TextView splashTextView_second = findViewById(R.id.loading_real_text_second);
        TextView splashTextView_third = findViewById(R.id.loading_real_text_third);

        splashTextView_first.startAnimation(animation_text_first);
        splashTextView_second.startAnimation(animation_text_second);
        splashTextView_third.startAnimation(animation_text_third);

        Animation animation_image = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_splash_imageview);
        final ImageView splashImageView = findViewById(R.id.loading_real_logo);
        splashImageView.startAnimation(animation_image);
        animation_text_third.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_splash_out_top,R.anim.anim_splash_in_down);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}
