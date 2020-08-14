package com.example.madcamp_game.UI.Game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madcamp_game.Login.LoginActivity;
import com.example.madcamp_game.R;
import com.example.madcamp_game.Tracking.Value;
import com.example.madcamp_game.UI.Result.DrawActivity;
import com.example.madcamp_game.UI.Result.LoseActivity;
import com.example.madcamp_game.UI.Result.WinActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class ConsoleActivity extends AppCompatActivity {

    ImageButton btn_moveup, btn_movedown, btn_moveleft, btn_moveright, btn_attack, btn_defense, btn_random, btn_jump, btn_crouch, btn_pause;
    TextView tv_test;
    boolean moveCheck = false;

    public static Activity activity;

    Value TopToBottom = new Value("TopToBottom",30.1027, 1.3637, 5.1845, 177.7795, 33.0033 );
    Value BottomToTop = new Value("BottomToTop", -18.5643, 4.1833, -3.4058, -145.7248, 107.1361 );
    Value LeftToRightMiddle = new Value("LeftToRightMiddle",8.9690, 8.8203, 3.6654, 88.7715, 204.3686);
    Value RightToLeftMiddle = new Value("RightToLeftMiddle",18.5537 , 4.7504 , -4.2382 , 79.9313 , 29.2915);
    Value LeftTopToRightBottom = new Value("LeftTopToRightBottom", 22.4824, 6.4723, 2.5205, 215.1589, 172.2065);
    Value LeftBottomToRightTop = new Value( "LeftBottomToRightTop",-4.3535, 8.9803, 3.4353, -70.9617, 201.8227);
    Value RightTopToLeftBottom = new Value("RightTopToLeftBottom", 17.3927, -3.8410, 4.7309, 133.6053, -64.0468);
    Value KillSelf = new Value("KillSelf", 6.2222,4.8357,5.8194,32.0535,21.5930);

    ArrayList<Value> AttackValues = new ArrayList<Value>();
    ArrayList<Value> DefenseValues = new ArrayList<Value>();

    private SensorManager sensorManger;
    private Sensor stepDetectorSensor;
    StepDetectListener mStepLis;
    String direction = "default";

    private io.socket.client.Socket mSocket;
    String message;

    SensorManager mSensorManager;
    SensorEventListener mAccLis;
    Sensor mAccelometerSensor;

    double xb,yb,zb,xzb,yzb;
    double xa,ya,za,xza,yza;
    double xc , yc, zc, xzc, yzc;
    double accX, accY, accZ, angleXZ, angleYZ;
    double min_difference, temp_difference;
    Value min_diff_value, measured_value;
    String check = "1";

    double determiner;
    String random_motion;
    String ID, MyNickname, counterpart;
    MediaPlayer mp;
    Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        Intent receivedintent = getIntent();
        ID = receivedintent.getStringExtra("ID");
        MyNickname = ((StartActivity) StartActivity.context).Nickname;
        counterpart = receivedintent.getStringExtra("counterpart");

        activity = ConsoleActivity.this;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // stop background sound
        mp = ((LoginActivity) LoginActivity.context).mp;
        mp.pause();

        // set Socket
        mSocket = ((LoginActivity) LoginActivity.context).mSocket;
        setListening();

        // Make ArrayList for Motion
        addAttackValues(AttackValues);
        addDefenseValues(DefenseValues);

        // Find Views that will be used in this Activity
        findViews();

        // set Sensors : Step Sensor / Accelerometer Sensor
        setSensors();

        // set Buttons for control
        setButtons_Touch();
        setButtons_Click();

        // set Terminate Listen
        setPauseListening(counterpart);

        // set Hit Lisen
        setHitListening(ID);

        // set Result Listen
        setResultListening(ID);
        setDrawListening(ID);

    }

    private class StepDetectListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.values[0] == 1.0f) {

                if (!direction.equals("default")) {
                    tv_test.setText(direction);
//                    sendDirection(direction);
                    Toast.makeText(ConsoleActivity.this,"Move "+ direction,Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class AccelerometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            accX = event.values[0];
            accY = event.values[1];
            accZ = event.values[2];

            angleXZ = Math.atan2(accX,  accZ) * 180/Math.PI;
            angleYZ = Math.atan2(accY,  accZ) * 180/Math.PI;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    void addAttackValues(ArrayList<Value> arr) {
        arr.add(KillSelf);
        arr.add(LeftToRightMiddle);
        arr.add(RightToLeftMiddle);
        arr.add(LeftTopToRightBottom);
        arr.add(LeftBottomToRightTop);
        arr.add(RightTopToLeftBottom);
    }

    void addDefenseValues(ArrayList<Value> arr) {
        arr.add(TopToBottom);
        arr.add(BottomToTop);
    }

    void findViews() {
        tv_test = (TextView) findViewById(R.id.tv_test);
        btn_moveup = (ImageButton) findViewById(R.id.btn_moveup);
        btn_movedown = (ImageButton) findViewById(R.id.btn_movedown);
        btn_moveleft = (ImageButton) findViewById(R.id.btn_moveleft);
        btn_moveright = (ImageButton) findViewById(R.id.btn_moveright);
        btn_attack = (ImageButton) findViewById(R.id.btn_attack);
        btn_defense = (ImageButton) findViewById(R.id.btn_defense);
        btn_crouch = (ImageButton) findViewById(R.id.btn_crouch);
        btn_jump = (ImageButton) findViewById(R.id.btn_jump);
        btn_random = (ImageButton) findViewById(R.id.btn_random);
        btn_pause = (ImageButton) findViewById(R.id.btn_pause);
    }

    void setSensors() {

        sensorManger = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManger.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mStepLis = new StepDetectListener();
        sensorManger.registerListener(mStepLis, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccLis = new AccelerometerListener();
        mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI);

    }

    void setButtons_Click() {

        btn_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Press Jump");
                tv_test.setText("Jump");
                sendJump();
            }
        });

        btn_crouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Press Crouch");
                tv_test.setText("Crouch");
                sendCrouch();
            }
        });

        btn_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Press Tease");
                tv_test.setText("Tease");
                sendRandom();
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Press pause");
                sendPause(MyNickname);
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    void setButtons_Touch() {

        btn_moveup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        direction = "up";
                        sendDirection(direction);
                        tv_test.setText(direction);
                        break;

                    case MotionEvent.ACTION_UP:
                        direction = "default";
                        tv_test.setText(direction);
                        break;

                }
                return false;
            }
        });

        btn_moveright.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        direction = "right";
                        sendDirection(direction);
                        tv_test.setText(direction);
                        break;

                    case MotionEvent.ACTION_UP:
                        direction = "default";
                        tv_test.setText(direction);
                        break;

                }
                return false;
            }
        });

        btn_movedown.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        direction = "down";
                        sendDirection(direction);
                        tv_test.setText(direction);
                        break;

                    case MotionEvent.ACTION_UP:
                        direction = "default";
                        tv_test.setText(direction);
                        break;

                }
                return false;
            }
        });

        btn_moveleft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        direction = "left";
                        sendDirection(direction);
                        tv_test.setText(direction);
                        break;

                    case MotionEvent.ACTION_UP:
                        direction = "default";
                        tv_test.setText(direction);
                        break;

                }
                return false;
            }
        });

        btn_attack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (check.equals("1")) {
                            assignBeforeValues();
                            check="0";
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        findAttackAction(assignAfterValues());
                        check="1";
                        break;

                }
                return false;
            }
        });

        btn_defense.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (check.equals("1")) {
                            assignBeforeValues();
                            check="0";
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        findDefenseAction(assignAfterValues());
                        check="1";
                        break;

                }
                return false;
            }
        });



    }

    private void setListening() {
        // listen for game finish
        mSocket.on("connection", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    message = messageJson.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {tv_test.setText(message);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void sendDirection(String direction) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("direction",direction);
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("move", jsonData);
    }

    void assignBeforeValues() {
        xb = accX;
        yb = accY;
        zb = accZ;
        xzb = angleXZ;
        yzb = angleYZ;
    }

    Value assignAfterValues() {
        xa = accX;
        ya = accY;
        za = accZ;
        xza = angleXZ;
        yza = angleYZ;

        return calculateDifference();
    }

    Value calculateDifference() {
        xc = xa - xb;
        yc = ya - yb;
        zc = za - zb;
        xzc = xza - xzb;
        yzc = yza - yzb;

        measured_value = new Value("measured_value",xc,yc,zc,xzc,yzc);
        return measured_value;
    }

    void findAttackAction(Value measured_value) {

        min_difference = 1000;
        for (int i=0; i<AttackValues.size(); i++) {
            temp_difference = AttackValues.get(i).difference(measured_value);
            System.out.println(AttackValues.get(i).getName() +" : " + temp_difference);
            if (temp_difference < min_difference ) {
                min_difference = temp_difference;
                min_diff_value = AttackValues.get(i);
            }
        }
        System.out.println("Miniumum value : " + min_diff_value.getName());
        tv_test.setText(min_diff_value.getName());

        sendAttack(min_diff_value.getName());
    }

    void sendAttack(String attack) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("motion",attack);
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonData);
        mSocket.emit("attack", jsonData);
    }

    void findDefenseAction(Value measured_value) {

        min_difference = 1000;
        for (int i=0; i<DefenseValues.size(); i++) {
            temp_difference = DefenseValues.get(i).difference(measured_value);
            System.out.println(DefenseValues.get(i).getName() +" : " + temp_difference);
            if (temp_difference < min_difference ) {
                min_difference = temp_difference;
                min_diff_value = DefenseValues.get(i);
            }
        }
        tv_test.setText(min_diff_value.getName());
        sendDefense(min_diff_value.getName());
    }

    void sendDefense(String defense) {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("motion",defense);
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("defense", jsonData);
    }

    void sendJump() {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("motion","true");
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("jump", jsonData);
    }

    void sendPause(String nickname) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("Nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("pause", jsonData);

        Intent intentPause = new Intent(ConsoleActivity.this,PauseActivity.class);
        intentPause.putExtra("Nickname", MyNickname);
        startActivity(intentPause);

    }

    void sendCrouch() {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("motion","true");
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("crouch", jsonData);
    }

    void sendRandom() {
        JSONObject jsonData = new JSONObject();

        determiner = Math.random();
        if (determiner < 0.33)
            random_motion = "dance";
        else if (determiner < 0.66)
            random_motion = "hip";
        else
            random_motion = "love";


        try {
            jsonData.put("motion",random_motion);
            jsonData.put("ID",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("random", jsonData);
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    private void setPauseListening(final String Nickname) {

        mSocket.on("pause", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    if (messageJson.getString("Nickname").equals(Nickname)) {
                        Intent intentPause = new Intent(ConsoleActivity.this, PausedActivity.class);
                        intentPause.putExtra("Nickname",counterpart);
                        startActivity(intentPause);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setHitListening(final String id) {

        mSocket.on("hit", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    if (messageJson.getString("ID").equals(id)) {
                        vibrator.vibrate(500);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setResultListening(final String id) {

        mSocket.on("victory", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    if (messageJson.getString("winner").equals(id)) {
                        Intent winIntent = new Intent(ConsoleActivity.this, WinActivity.class);
                        startActivity(winIntent);
                    }

                    if (messageJson.getString("loser").equals(id)) {
                        Intent loseIntent = new Intent(ConsoleActivity.this, LoseActivity.class);
                        startActivity(loseIntent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setDrawListening(final String id) {

        mSocket.on("draw", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());

                    if (messageJson.getString("one").equals(id)) {
                        Intent drawIntent = new Intent(ConsoleActivity.this, DrawActivity.class);
                        startActivity(drawIntent);
                    }

                    if (messageJson.getString("two").equals(id)) {
                        Intent drawIntent = new Intent(ConsoleActivity.this, DrawActivity.class);
                        startActivity(drawIntent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
