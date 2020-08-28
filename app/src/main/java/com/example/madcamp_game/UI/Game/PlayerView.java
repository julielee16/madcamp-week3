package com.example.madcamp_game.UI.Game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.madcamp_game.R;

public class PlayerView extends LinearLayout {

    TextView tv_Nickname;
    TextView tv_WinLose;

    public PlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.player,this,true);

        tv_Nickname = (TextView) findViewById(R.id.tv_Nickname);
        tv_WinLose = (TextView) findViewById(R.id.tv_WinLose);
    }

    public void setNickname(String name) {
        tv_Nickname.setText(name);
    }
    public void setWinLose(String mobile) {
        tv_WinLose.setText(mobile);
    }

}
