package com.example.madcamp_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madcamp_game.Login.LoginActivity;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {


    final String TAG ="TAG+MainActivity";
    String url = "http://192.249.19.244:2180";

    Button btn_login,btn_logout;
    String message;
    public Button button;
    public TextView text;
    public EditText edit;
    private io.socket.client.Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.btn_connect);
        text =  (TextView) findViewById(R.id.tv_data);
        edit =  (EditText) findViewById(R.id.et_data);
        btn_login = (Button) findViewById(R.id.btn_kakaologin);
        btn_logout = (Button) findViewById(R.id.btn_kakaologout);

        text.setText("");

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            mSocket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect();
        setListening();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String message = edit.getText().toString().trim();
                edit.setText("");
                if (!message.isEmpty()) {
                    //send message
                    String jsonString = message;
                    System.out.println("jsonString : " + message);
                    try {
                        JSONObject jsonData = new JSONObject(jsonString);
                        mSocket.emit("new_message", jsonData);
                        System.out.println("emit_json : " + jsonData);
                    } catch (JSONException e) {
                        Log.d("me", "error send message " + e.getMessage());
                    }
                }
            }
        });


    } //on create

    private void setListening() {
        mSocket.on("connection", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    message = messageJson.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(message);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}