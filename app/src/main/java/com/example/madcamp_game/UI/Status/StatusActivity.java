package com.example.madcamp_game.UI.Status;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madcamp_game.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class StatusActivity extends Activity {

    TextView tvStatus_Nickname, tvStatus_WinLose;
    ImageView ivStatus_Profile;

    String Profile, Nickname, Win, Lose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_status);

        tvStatus_Nickname = findViewById(R.id.tvStatus_Nickname);
        tvStatus_WinLose = findViewById(R.id.tvStatus_WinLose);
        ivStatus_Profile = findViewById(R.id.ivStatus_Profile);

        Intent receivedIntent = getIntent();
        Profile = receivedIntent.getStringExtra("Profile");
        Nickname = receivedIntent.getStringExtra("Nickname");
        Win = receivedIntent.getStringExtra("Win");
        Lose = receivedIntent.getStringExtra("Lose");

        if (! (Profile == null)) { ivStatus_Profile.setImageBitmap(getImageFromURL(Profile)); }

        tvStatus_Nickname.setText(Nickname);
        String WinLose = "W : " + Win + " / " + "L : " + Lose;
        tvStatus_WinLose.setText(WinLose);

    }

    public static Bitmap getImageFromURL(String imageURL){
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try
        {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection)url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e){
            e.printStackTrace();
        } finally{
            if(bis != null) {
                try {bis.close();} catch (IOException e) {e.printStackTrace();}
            }
            if(conn != null ) {
                conn.disconnect();
            }
        }

        return imgBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    public void mOnClose(View v) {
        //데이터 전달하기
        finish();
    }


}
