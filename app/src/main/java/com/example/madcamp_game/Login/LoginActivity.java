package com.example.madcamp_game.Login;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.madcamp_game.R;
import com.example.madcamp_game.UI.Game.StartActivity;
import com.example.madcamp_game.UI.Select.SelectActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback sessionCallback;

    String url = "http://192.249.19.244:2180";
    public io.socket.client.Socket mSocket;
    public static Context context;

    String message = "";
    String ID, Profile;
    JSONObject messageJson;

    public MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSocket();
        context = this;

        mp = MediaPlayer.create(this,R.raw.sound_background);
        mp.setLooping(true);
        mp.start();

        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        System.out.println("Network");
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {

                    ID = String.valueOf(result.getId());
                    Profile = result.getProfileImagePath();
                    sendLogin(ID);

                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    void setSocket() {
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
    }

    private void setListening() {
        // listen for game finish
        mSocket.on("login_result", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    messageJson = new JSONObject(args[0].toString());
                    System.out.println(messageJson);
                    message = messageJson.getString("Nickname");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(message.equals("none")) {

                    System.out.println("Nickname Not Exists : " + message);

                    Intent intent = new Intent(LoginActivity.this, NicknameActivity.class);
                    intent.putExtra("ID", ID);
                    intent.putExtra("Profile",Profile);
                    startActivity(intent);

                } else {

                    try {

                        if (messageJson.getString("ID").equals(ID)) {
                            System.out.println("Nickname Exists : " + message);

                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            intent.putExtra("ID", messageJson.getString("ID"));
                            intent.putExtra("Nickname", messageJson.getString("Nickname"));
                            intent.putExtra("Win", messageJson.getString("Win"));
                            intent.putExtra("Lose", messageJson.getString("Lose"));
                            intent.putExtra("Profile", Profile);

                            startActivity(intent);
                        }

                    } catch (JSONException e) {e.printStackTrace(); }
                }

            }

        });
    }

    void sendLogin(String ID) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("login", jsonData);
    }

}

