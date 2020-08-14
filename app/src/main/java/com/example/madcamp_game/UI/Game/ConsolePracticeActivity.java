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
import com.example.madcamp_game.UI.Select.SelectActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class ConsolePracticeActivity extends AppCompatActivity {

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

        activity = ConsolePracticeActivity.this;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // stop background sound
        mp = ((LoginActivity) LoginActivity.context).mp;
        mp.pause();

        // set Socket
        mSocket = ((LoginActivity) LoginActivity.context).mSocket;

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

    }

    private class StepDetectListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.values[0] == 1.0f) {

                if (!direction.equals("default")) {
                    tv_test.setText(direction);
//                    sendDirection(direction);
                    Toast.makeText(ConsolePracticeActivity.this,"Move "+ direction,Toast.LENGTH_SHORT).show();
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
            }
        });

        btn_crouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Press Crouch");
                tv_test.setText("Crouch");
            }
        });

        btn_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Press Tease");
                tv_test.setText("Tease");
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Press pause");
                Intent intentPause = new Intent(ConsolePracticeActivity.this, SelectActivity.class);
                startActivity(intentPause);
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
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}
