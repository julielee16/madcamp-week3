package com.example.madcamp_game.UI.Game;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class FightRequestActivity extends Activity {

    TextView tv_counterpart;
    Button btn_accept, btn_refuse;
    String counterpart, counterpart_win, counterpart_lose;
    private io.socket.client.Socket mSocket;
    String MyNickname, MyID;
    static MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fightrequest);

        mp = MediaPlayer.create(this,R.raw.sound_button);

        tv_counterpart = findViewById(R.id.tv_counterpart);
        btn_accept = findViewById(R.id.btn_accept);
        btn_refuse = findViewById(R.id.btn_refuse);

        Intent receivedIntent = getIntent();
        counterpart = receivedIntent.getStringExtra("counterpart");
        counterpart_win = receivedIntent.getStringExtra("counterpart_win");
        counterpart_lose = receivedIntent.getStringExtra("counterpart_lose");

        tv_counterpart.setText(counterpart + " : " + "W("+ counterpart_win + ")" + " / " + "L(" + counterpart_lose + ")");

        MyNickname = ((StartActivity) StartActivity.context).Nickname;
        MyID = ((StartActivity) StartActivity.context).ID;
        mSocket = ((LoginActivity) LoginActivity.context).mSocket;

        setCancelListening();

        btn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                sendRefuse(counterpart);
            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                sendAccept(counterpart);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
            //바깥레이어 클릭시 안닫히게
            if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
                return false;
            }
            return true;
    }

    @Override
    public void onBackPressed() {
            //안드로이드 백버튼 막기
            return;
    }

    void sendRefuse(String Nickname) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("Nickname",Nickname);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("REFUSE!!!!!!!!!!!!!!!!!!!!!!!!");
        mSocket.emit("refuse", jsonData);
        finish();
    }

    void sendAccept(String Nickname) {
        JSONObject jsonData = new JSONObject();
        System.out.println("Request Accept" + Nickname);
        try {
            jsonData.put("Nickname",Nickname);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("accept", jsonData);
        sendMatchEstablished(counterpart,MyNickname);

    }

    void sendMatchEstablished(String Player1 , String Player2) {

        System.out.println("SendMatchEstablished : " + Player1 + Player2);

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("player_one",Player1);
            jsonData.put("player_two",Player2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("match", jsonData);

        Intent intent = new Intent(FightRequestActivity.this,FightLoadingActivity.class);
        intent.putExtra("Nickname",MyNickname);
        intent.putExtra("ID",MyID);
        intent.putExtra("counterpart",counterpart);
        startActivity(intent);
        finish();
    }

    private void setCancelListening() {

        mSocket.on("cancel", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String counterpart, counterpart_win, counterpart_lose;

                    JSONObject messageJson = new JSONObject(args[0].toString());
                    System.out.println("sdkjflsdjflksdjfld;sjlsad : " + messageJson);
                    if (messageJson.getString("Nickname").equals(MyNickname)) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
