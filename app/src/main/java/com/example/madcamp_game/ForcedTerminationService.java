package com.example.madcamp_game;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.UI.Game.StartActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;

public class ForcedTerminationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    String url = "http://192.249.19.244:2180";
    String ID;
    public io.socket.client.Socket mSocket;
    MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("test", "서비스의 onCreate");
        mp = ((LoginActivity) LoginActivity.context).mp;
        setSocket();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) { //핸들링 하는 부분
        Log.e("Error","onTaskRemoved - 강제 종료 " + rootIntent);
        mp.stop();
        Toast.makeText(this, "게임이 종료 되었습니다", Toast.LENGTH_SHORT).show();
        sendAppTerminated();

        stopSelf(); //서비스 종료
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
    }
    void sendAppTerminated() {
        JSONObject jsonData = new JSONObject();
        ID = ((StartActivity) StartActivity.context).ID;

        try {
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("app_terminated", jsonData);
    }

}
