package com.example.madcamp_game.UI.Select;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.R;
import com.example.madcamp_game.UI.Game.ConsolePracticeActivity;
import com.example.madcamp_game.UI.Game.FightLoadingActivity;
import com.example.madcamp_game.UI.Game.FightRequestActivity;
import com.example.madcamp_game.UI.Game.PlayerListActivity;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class SelectActivity extends AppCompatActivity {

    ImageButton btn_logout;
    Button btn_pvp, btn_practice;

    private io.socket.client.Socket mSocket;
    String ID, Nickname, Win, Lose;
    static MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        mp = MediaPlayer.create(this,R.raw.sound_button);

        Intent receivedIntent = getIntent();
        ID = receivedIntent.getStringExtra("ID");
        Nickname = receivedIntent.getStringExtra("Nickname");
        Win = receivedIntent.getStringExtra("Win");
        Lose = receivedIntent.getStringExtra("Lose");

        mSocket = ((LoginActivity) LoginActivity.context).mSocket;

        buttonFindSetup();
        setPvpListening();

    }

    void sendID_pvp() {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("pvp", jsonData);
    }

    void buttonFindSetup() {
        btn_logout = (ImageButton) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mp.start();
                Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(SelectActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

        btn_pvp = (Button) findViewById(R.id.btn_pvp);
        btn_pvp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mp.start();
                sendID_pvp();
                Intent intent = new Intent(SelectActivity.this, PlayerListActivity.class);
                intent.putExtra("ID",ID);
                intent.putExtra("Nickname",Nickname);
                intent.putExtra("check","1");
                System.out.println("SelectActity : " + Nickname);
                startActivity(intent);
            }
        });

        btn_practice = (Button) findViewById(R.id.btn_practice);
        btn_practice.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(SelectActivity.this, ConsolePracticeActivity.class);
                intent.putExtra("ID",ID);
                intent.putExtra("Nickname",Nickname);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setPvpListening() {
        // listen for game finish

        mSocket.on("fight", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String counterpart, counterpart_win, counterpart_lose;

                    JSONObject messageJson = new JSONObject(args[0].toString());
                    System.out.println("message bada : !!!!!!!" + messageJson);
                    if (messageJson.getString("Receiver").equals(Nickname)) {

                        System.out.println("Listening" + messageJson.getString("Receiver"));

                        counterpart = messageJson.getString("Sender");
                        counterpart_win = messageJson.getString("Win");
                        counterpart_lose = messageJson.getString("Lose");
                        System.out.println("PVP listen : " + messageJson);

                        Intent intent = new Intent(SelectActivity.this, FightRequestActivity.class);
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

}
