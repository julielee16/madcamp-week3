package com.example.madcamp_game.UI.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.R;
import com.example.madcamp_game.UI.Select.SelectActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class PlayerActivity extends Activity {

    String Nickname, Win, Lose, message, MyNickname, MyID;
    private io.socket.client.Socket mSocket;
    ImageView pvp_load;
    PlayerListActivity PLA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pvpload);

        PLA = (PlayerListActivity) PlayerListActivity.activity;

        pvp_load = (ImageView)findViewById(R.id.pvp_loading);
        Glide.with(this).load(R.raw.pvp_loading).into(pvp_load);

        Intent receivedIntent = getIntent();
        Nickname = receivedIntent.getStringExtra("Nickname");
        Win = receivedIntent.getStringExtra("Win");
        Lose = receivedIntent.getStringExtra("Lose");

        mSocket = ((LoginActivity) LoginActivity.context).mSocket;
        MyNickname = ((StartActivity) StartActivity.context).Nickname;
        MyID = ((StartActivity) StartActivity.context).ID;

        System.out.println("PlayerActivity Works");

        setPvpListening();
        setRefuseListening();
        setAcceptListening();
        sendFight(Nickname, MyNickname);
    }

    public void mOnClose(View v){
        //데이터 전달하기
        sendCancel(Nickname);
    }

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

    private void setPvpListening() {
        // listen for game finish

        mSocket.on("fight", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String counterpart, counterpart_win, counterpart_lose;

                    JSONObject messageJson = new JSONObject(args[0].toString());

                    if (messageJson.getString("Receiver").equals(MyNickname)) {

                        System.out.println("Listening" + messageJson.getString("Receiver"));

                        counterpart = messageJson.getString("Sender");
                        counterpart_win = messageJson.getString("Win");
                        counterpart_lose = messageJson.getString("Lose");
                        System.out.println("PVP listen : " + messageJson);

                        Intent intent = new Intent(PlayerActivity.this, FightRequestActivity.class);
                        intent.putExtra("counterpart", counterpart);
                        intent.putExtra("counterpart_win", counterpart_win);
                        intent.putExtra("counterpart_lose", counterpart_lose);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void sendFight(String Nickname, String MyNickname) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("Receiver",Nickname);
            jsonData.put("Sender", MyNickname);
            jsonData.put("Win",Win);
            jsonData.put("Lose",Lose);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("fight", jsonData);
    }

    void sendCancel(String Nickname) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("Nickname",Nickname);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("cancel", jsonData);
        finish();
    }

    private void setRefuseListening() {
        // listen for game finish

        mSocket.on("refuse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String counterpart, counterpart_win, counterpart_lose;

                    JSONObject messageJson = new JSONObject(args[0].toString());
                    message = messageJson.getString("Nickname");

                    if (message.equals(MyNickname)) {
                        System.out.println("Refuse Listened");
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "대결 신청이 거절되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setAcceptListening() {

        mSocket.on("accept", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String counterpart, counterpart_win, counterpart_lose;

                    JSONObject messageJson = new JSONObject(args[0].toString());
                    message = messageJson.getString("Nickname");

                    if (message.equals(MyNickname)) {
                        System.out.println("accept Listened");
                        Intent intent = new Intent(PlayerActivity.this, FightLoadingActivity.class);
                        intent.putExtra("Nickname",MyNickname);
                        intent.putExtra("ID", MyID);
                        intent.putExtra("counterpart",Nickname);
                        startActivity(intent);
                        PLA.finish();
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
