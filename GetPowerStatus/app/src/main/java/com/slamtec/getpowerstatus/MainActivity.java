package com.slamtec.getpowerstatus;

import android.bluetooth.BluetoothClass;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.SlamwareCorePlatform;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.DockingStatus;
import com.slamtec.slamware.robot.Pose;
import com.slamtec.slamware.robot.PowerStatus;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView sdkVersion = (TextView) findViewById(R.id.sdk_version);
        TextView deviceID = (TextView) findViewById(R.id.device_id);

        TextView chargingStatus = (TextView) findViewById(R.id.changing_status);
        TextView dcConnected = (TextView) findViewById(R.id.dc_connected);
        TextView batteryPercentage = (TextView) findViewById(R.id.battery_percentage);
        TextView dockStatus = (TextView) findViewById(R.id.dock_status);
        TextView sleepMode = (TextView) findViewById(R.id.sleep_mode);


        /* 与底盘连接 */
        AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("10.0.130.71", 1445);

        /* 获取版本信息 */
        sdkVersion.setText(robotPlatform.getSDKVersion());

        try {
            /* 获取Slamware ID */
            deviceID.setText(robotPlatform.getDeviceId());

            /* 获取电源状态相关信息 */
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
                    default:
                        break;
            }

            /* print log info */
            Log.d(TAG, "======= Robot Platform Power Status =======");
            Log.d(TAG, "Robot Platform is Charging:        " + powerStatus.isCharging());
            Log.d(TAG, "Robot Platform is DC connected:    " + powerStatus.isDCConnected());
            Log.d(TAG, "Robot Platform Battery Percentage: " + powerStatus.getBatteryPercentage());
            Log.d(TAG, "Robot Platform Sleep Mode:         " + powerStatus.getSleepMode());
            Log.d(TAG, "Robot Platform Docking Status:     " + powerStatus.getDockingStatus());
            Log.d(TAG, "===========================================");

        } catch (ConnectionFailException e) {
            Log.d(TAG, "Connection Fail");
            e.printStackTrace();
        } catch (ParseInvalidException e) {
            Log.d(TAG, "Parse Invalid");
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            Log.d(TAG, "Invalid Argument");
            e.printStackTrace();
        } catch (ConnectionTimeOutException e) {
            Log.d(TAG, "Connection TimeOut");
            e.printStackTrace();
        } catch (RequestFailException e) {
            Log.d(TAG, "Request Fail");
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            Log.d(TAG, "Unauthorized Request");
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            Log.d(TAG, "Unsupported Command");
            e.printStackTrace();
        }
    }
}
