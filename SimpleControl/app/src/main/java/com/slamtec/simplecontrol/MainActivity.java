package com.slamtec.simplecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.robot.Pose;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Bundle savedInstanceState;
    private String deviceId;
    private Pose pose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("192.168.11.1", 1445);

        if(robotPlatform != null) {

            Log.d(TAG, "Slamware connect success");

            try {
                Pose pose = robotPlatform.getPose();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                deviceId = robotPlatform.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        System.out.println("deviceId = " + deviceId);


        // go forward
        Button button_forward = (Button) findViewById(R.id.button_forward);
        button_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    robotPlatform.moveBy(MoveDirection.FORWARD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // go backward
        Button button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    robotPlatform.moveBy(MoveDirection.BACKWARD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // turn left
        Button button_turn_left = (Button) findViewById(R.id.button_turn_left);
        button_turn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    robotPlatform.moveBy(MoveDirection.TURN_LEFT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        // turn right
        Button button_turn_right = (Button) findViewById(R.id.button_turn_right);
        button_turn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    robotPlatform.moveBy(MoveDirection.TURN_RIGHT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


}