package com.slamtec.getpowerstatus;

import android.bluetooth.BluetoothClass;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.SlamwareCorePlatform;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.robot.DockingStatus;
import com.slamtec.slamware.robot.Pose;
import com.slamtec.slamware.robot.PowerStatus;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView chargingStatus = (TextView) findViewById(R.id.changing_status);
        TextView dcConnected = (TextView) findViewById(R.id.dc_connected);
        TextView batteryPercentage = (TextView) findViewById(R.id.battery_percentage);
        TextView dockStatus = (TextView) findViewById(R.id.dock_status);
        TextView sleepMode = (TextView) findViewById(R.id.sleep_mode);


        AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("192.168.11.1", 1445);

        try {
            Pose pose = robotPlatform.getPose();
        } catch(Exception e) {
            e.printStackTrace();
        }


        try {


            /* 获取电源相关信息 */
            PowerStatus powerStatus = robotPlatform.getPowerStatus();

            /* 是否正在充电 */
            if(powerStatus.isCharging() == true) {
                chargingStatus.setText("正在充电");
            } else {
                chargingStatus.setText("未在充电");
            }

            /* 是否DC connected */
            if(powerStatus.isDCConnected() == true) {
                dcConnected.setText("已连接");
            } else {
                dcConnected.setText("未连接");
            }

            /* 剩余电池电量 */
            batteryPercentage.setText(powerStatus.getBatteryPercentage() + "%");

            /* 是否回到充电桩 */
            if(powerStatus.getDockingStatus() == DockingStatus.OnDock) {
                dockStatus.setText("已回桩");
            } else if (powerStatus.getDockingStatus() == DockingStatus.NotOnDock) {
                dockStatus.setText("未回桩");
            } else {
                dockStatus.setText("未知状态");
            }

            /* 睡眠状态 */
            switch (powerStatus.getSleepMode()) {
                case Awake:
                    sleepMode.setText("Awake");
                    break;
                case WakingUp:
                    sleepMode.setText("WakingUp");
                    break;
                case Asleep:
                    sleepMode.setText("Asleep");
                    break;
                case Unknown:
                    sleepMode.setText("Unknown");
                    break;
            }

            System.out.println("Robot Platform is Charging:        " + powerStatus.isCharging());
            System.out.println("Robot Platform is DC connected:    " + powerStatus.isDCConnected());
            System.out.println("Robot Platform Battery Percentage: " + powerStatus.getBatteryPercentage());

        } catch(Exception e) {
                e.printStackTrace();

        }
    }
}
