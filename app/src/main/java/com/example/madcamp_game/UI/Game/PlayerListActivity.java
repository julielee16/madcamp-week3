package com.example.madcamp_game.UI.Game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.GridView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class PlayerListActivity extends AppCompatActivity {

    public static Activity activity;
    private io.socket.client.Socket mSocket;
    public String ID;
    public String Nickname;
    public static Context context;
    JSONArray message;
    ArrayList<Player> players = new ArrayList<Player>();
    String check="0";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerlist);

        mSocket = ((LoginActivity) LoginActivity.context).mSocket;
        context = this;
        setListening();
        activity = PlayerListActivity.this;

        Nickname = ((StartActivity) StartActivity.context).Nickname;
        ID = ((StartActivity) StartActivity.context).ID;

        setPvpListening(Nickname);

        System.out.print("PlayerListActivity : " + Nickname);

        sendCurrent(ID, Nickname);

    }


    void sendCurrent(String ID, String Nickname) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("Nickname",Nickname);
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("current", jsonData);
    }

    private void setListening() {
        // listen for game finish
        mSocket.on("player_array", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    message = messageJson.getJSONArray("array");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                players.clear();
                System.out.println("lisactivity : " + message);


                for (int i =0; i<message.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) message.get(i);
                        System.out.println(jsonObject);
                        if (!jsonObject.getString("Nickname").equals(Nickname)) {
                            System.out.println("AAA L : " +jsonObject.getString("Nickname"));
                            Player temp_player = new Player(jsonObject.getString("Nickname")
                                    , jsonObject.getString("Win")
                                    , jsonObject.getString("Lose"));

                            players.add(temp_player);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (check.equals("0")) {
                            ListView listView = findViewById(R.id.listview_player);
                            PlayerAdapter playerAdapter = new PlayerAdapter(getApplicationContext(), R.layout.activity_playerlist, players);
                            listView.setAdapter(playerAdapter);
                            check = "1";
                        }

                    }
                });
            }
        });
    }

    private void setPvpListening(final String nickname) {

        mSocket.on("fight", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String counterpart, counterpart_win, counterpart_lose;

                    JSONObject messageJson = new JSONObject(args[0].toString());
                    if (messageJson.getString("Receiver").equals(nickname)) {
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + messageJson.getString("Receiver"));
                        System.out.println("Listening" + messageJson.getString("Receiver"));

                        counterpart = messageJson.getString("Sender");
                        counterpart_win = messageJson.getString("Win");
                        counterpart_lose = messageJson.getString("Lose");
                        System.out.println("PVP listen : " + messageJson);

                        Intent intent = new Intent(PlayerListActivity.this, FightRequestActivity.class);
                        intent.putExtra("counterpart", counterpart);
                        intent.putExtra("counterpart_win", counterpart_win);
                        intent.putExtra("counterpart_lose", counterpart_lose);
                        System.out.println("count!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        check = "0";
        finish();
    }


}
