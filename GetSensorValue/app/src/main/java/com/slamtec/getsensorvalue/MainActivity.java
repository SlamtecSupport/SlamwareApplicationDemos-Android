package com.slamtec.getsensorvalue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.robot.ImpactSensorInfo;
import com.slamtec.slamware.robot.ImpactSensorType;
import com.slamtec.slamware.robot.ImpactSensorValue;
import com.slamtec.slamware.robot.Pose;
import com.slamtec.slamware.robot.SensorType;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Bundle savedInstanceState;
    private String deviceId;
    private Pose pose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("10.0.130.71", 1445);

        if(robotPlatform != null) {

            Log.d(TAG, "Slamware connect success");

            System.out.println("Slamware connect success");
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

            try {


                List<ImpactSensorInfo> lists  = robotPlatform.getSensors();

                for(int i=0; i<lists.size(); i++) {
                    System.out.println("========================================");

                    int SensorId = lists.get(i).getSensorId();
                    System.out.println("SensorId = " + SensorId);

                    ImpactSensorValue SensorValue = robotPlatform.getSensorValue(SensorId);
                    Log.d(TAG, "onCreate: SensorValue = " + SensorValue);
                    try {
                        float value = SensorValue.getValue();
                        System.out.println("SensorValue.Value = " + value);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onCreate: Exception");
                    }

                    try {
                        float time = SensorValue.getTime();
                        System.out.println("SensorValue.time = " + time);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onCreate: Exception");
                    }

                    System.out.println("pose.x = " + lists.get(i).getPose().getX());
                    System.out.println("pose.y = " + lists.get(i).getPose().getY());
                    System.out.println("pose.yaw = " + lists.get(i).getPose().getYaw());

                    ImpactSensorType type = lists.get(i).getType();
                    switch (type) {
                        case Analog:
                            System.out.println("TYPE = Analog");
                            break;
                        case Digital:
                            System.out.println("TYPE = Digital");
                            break;
                    }
                    SensorType coreSensorType = lists.get(i).getKind();

                    System.out.println("coreSensorType = " + coreSensorType);

                    float refreshFreq = lists.get(i).getRefreshFreq();
                    System.out.println("refreshFreq = " + refreshFreq);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("deviceId = " + deviceId);

    }



}
