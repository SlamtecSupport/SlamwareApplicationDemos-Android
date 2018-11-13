package com.slamtec.www.robothealthstatus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.HealthInfo;
import com.slamtec.slamware.robot.Pose;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    HealthInfo healthInfo;
    AbstractSlamwarePlatform robotPlatform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button clearRobotHealth = (Button) findViewById(R.id.clear_robot_health);
        clearRobotHealth.setOnClickListener(this);

        /* 与底盘连接 */
        robotPlatform = DeviceManager.connect("10.0.130.71", 1445);

        /* print version info */
        try {
            Log.d(TAG, "SDK Version: " + robotPlatform.getSDKVersion());
            Log.d(TAG, "SDP Version: " + robotPlatform.getSlamwareVersion());
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (ParseInvalidException e) {
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ConnectionTimeOutException e) {
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            e.printStackTrace();
        } catch (RequestFailException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                healthInfo = robotPlatform.getRobotHealth();

                if (healthInfo.isError()) {
                    Log.d(TAG, "Error");
                } else if (healthInfo.isFatal()) {
                    Log.d(TAG, "Fatal");
                } else if (healthInfo.isWarning()) {
                    Log.d(TAG, "Warning");
                } else if (healthInfo.getHasLidarDisconnected()) {
                    Log.d(TAG, "Lidar Disconnected");
                } else if (healthInfo.getHasSystemEmergencyStop()) {
                    Log.d(TAG, "Has System Emergency Stop");
                }

                ArrayList<HealthInfo.BaseError> healthInfoErrors = healthInfo.getErrors();

                for (int i=0; i<healthInfoErrors.size(); i++) {
                    Log.d(TAG, "Message:   " + healthInfoErrors.get(i).getErrorMessage());
                    Log.d(TAG, "Level  :   " + healthInfoErrors.get(i).getErrorLevel());
                    Log.d(TAG, "ErrorType: " + healthInfoErrors.get(i).getComponentErrorType());
                    Log.d(TAG, "ErrorCode: " + healthInfoErrors.get(i).getErrorCode());
                }
            }
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (ParseInvalidException e) {
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ConnectionTimeOutException e) {
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            e.printStackTrace();
        } catch (RequestFailException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_robot_health) {

            int errorSize = healthInfo.getErrors().size();
            if (errorSize > 0) {
                for (int i=0; i<errorSize; i++) {

                    try {
                        robotPlatform.clearRobotHealth(healthInfo.getErrors().get(i).getErrorCode());
                    } catch (RequestFailException e) {
                        e.printStackTrace();
                    } catch (ConnectionFailException e) {
                        e.printStackTrace();
                    } catch (ConnectionTimeOutException e) {
                        e.printStackTrace();
                    } catch (UnauthorizedRequestException e) {
                        e.printStackTrace();
                    } catch (UnsupportedCommandException e) {
                        e.printStackTrace();
                    } catch (ParseInvalidException e) {
                        e.printStackTrace();
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
