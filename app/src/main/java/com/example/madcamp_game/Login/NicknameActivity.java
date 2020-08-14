package com.example.madcamp_game.Login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.madcamp_game.R;
import com.example.madcamp_game.UI.Game.StartActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class NicknameActivity extends Activity {

    EditText et_nickname;
    String nickname="", check="";
    String ID, message, Profile;
    Button btn_presenceCheck;
    private io.socket.client.Socket mSocket;

    static MediaPlayer mp;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nickname);

        //UI 객체생성
        et_nickname = findViewById(R.id.et_nickname);
        btn_presenceCheck = findViewById(R.id.btn_presenceCheck);

        mp = MediaPlayer.create(this,R.raw.sound_background);
        mp.setLooping(true);
        mp.start();

        Intent receivedIntent = getIntent();
        ID = receivedIntent.getStringExtra("ID");
        Profile = receivedIntent.getStringExtra("Profile");

        mSocket = ((LoginActivity) LoginActivity.context).mSocket;
        setListening();

        btn_presenceCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                System.out.println("press button");
                nickname = et_nickname.getText().toString();
                if (nickname.equals("")) {
                    Toast.makeText(getApplicationContext(),"닉네임을 입력해 주세요",Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Okok");
                    sendCheck(nickname); }
            }
        });

    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기

        if (check.equals("")) {
            Toast.makeText(getApplicationContext(),"닉네임 중복확인을 해주세요",Toast.LENGTH_SHORT).show();
        } else {
            if (check.equals("able")) {

                sendCreate(nickname,ID);

            } else if (check.equals("disable")) {
                Toast.makeText(getApplicationContext(),"닉네임 중복확인을 해주세요",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    void sendCheck(String Nickname) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("Nickname",Nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("bEFORE EMIT");
        System.out.println(jsonData);

        mSocket.emit("check_exist", jsonData);
    }

    private void setListening() {
        // listen for game finish
        mSocket.on("check_exist", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    message = messageJson.getString("result");
                } catch (JSONException e) { e.printStackTrace(); }

                System.out.println("check_exist : " + message);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.equals("1")) {
                            Toast.makeText(NicknameActivity.this, "닉네임이 이미 존재합니다", Toast.LENGTH_SHORT).show();
                            check = "disable";
                        } else if (message.equals("0")) {
                            Toast.makeText(NicknameActivity.this, "사용 가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                            check = "able";
                        }
                    }
                });

            }
        });
    }

    void sendCreate(String Nickname, String ID) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("Nickname",Nickname);
            jsonData.put("ID",ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("create_Nickname", jsonData);

        System.out.println("Create : send data created");

        Intent intent = new Intent(NicknameActivity.this, StartActivity.class);

        intent.putExtra("ID", ID);
        intent.putExtra("Nickname", nickname);
        intent.putExtra("Win", "0");
        intent.putExtra("Lose", "0");
        intent.putExtra("Current", "1");

        startActivity(intent);

        finish();

    }


}
