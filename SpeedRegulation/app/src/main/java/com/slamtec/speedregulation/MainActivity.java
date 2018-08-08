package com.slamtec.speedregulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.SlamwareCorePlatform;
import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.OperationFailException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.sdp.SlamwareSdpPlatform;
import com.slamtec.slamware.robot.Location;

import static com.slamtec.slamware.robot.SystemParameters.SYSPARAM_ROBOT_SPEED;
import static com.slamtec.slamware.robot.SystemParameters.SYSVAL_ROBOT_SPEED_HIGH;
import static com.slamtec.slamware.robot.SystemParameters.SYSVAL_ROBOT_SPEED_LOW;
import static com.slamtec.slamware.robot.SystemParameters.SYSVAL_ROBOT_SPEED_MEDIUM;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlamwareCorePlatform robotPlatform = SlamwareCorePlatform.connect("192.168.11.1", 1445);

        IMoveAction action;
        try {
            action = robotPlatform.getCurrentAction();
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

        Location location1 = new Location(0, 1, 0);
        Location location2 = new Location(-1, 0, 0);
        Location location3 = new Location(0, 0, 0);

        while (true) {

            try {

                action = robotPlatform.moveTo(location1, false, true);

                if (action.getStatus() == ActionStatus.ERROR) {
                    Log.d(TAG, "onCreate: Acvtion Faild, " + action.getReason());
                }
                robotPlatform.setSystemParameter(SYSPARAM_ROBOT_SPEED, SYSVAL_ROBOT_SPEED_HIGH);
                Log.d(TAG, "onCreate: Robot is moving to " + "(" + location1.getX() + ", " + location1.getY() + ")");

                action.waitUntilDone();


                action = robotPlatform.moveTo(location2, false, true);

                if (action.getStatus() == ActionStatus.ERROR) {
                    Log.d(TAG, "onCreate: Acvtion Faild, " + action.getReason());
                }
                robotPlatform.setSystemParameter(SYSPARAM_ROBOT_SPEED, SYSVAL_ROBOT_SPEED_LOW);
                action.waitUntilDone();


                action = robotPlatform.moveTo(location3, false, true);

                if (action.getStatus() == ActionStatus.ERROR) {
                    Log.d(TAG, "onCreate: Acvtion Faild, " + action.getReason());
                }
                robotPlatform.setSystemParameter(SYSPARAM_ROBOT_SPEED, SYSVAL_ROBOT_SPEED_MEDIUM);
                action.waitUntilDone();



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
            } catch (OperationFailException e) {
                e.printStackTrace();
            }


        }


    }
}
