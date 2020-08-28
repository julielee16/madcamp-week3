package com.example.madcamp_game.UI.Game;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.View;

import com.example.madcamp_game.R;

public class PlayerClickListener implements View.OnClickListener {

    Context context;
    String Nickname;
    String Win;
    String Lose;
    static MediaPlayer mp;

    public PlayerClickListener(Context context, String Nickname, String Win, String Lose) {
        this.context = context;
        this.Nickname = Nickname;
        this.Win = Win;
        this.Lose = Lose;
    }

    public void onClick(View v) {

        mp = MediaPlayer.create(context, R.raw.sound_button);
        mp.start();
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("Nickname", Nickname);
        intent.putExtra("Win",Win);
        intent.putExtra("Lose",Lose);

        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
