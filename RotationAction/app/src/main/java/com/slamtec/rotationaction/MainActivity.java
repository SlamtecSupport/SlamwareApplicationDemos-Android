package com.slamtec.rotationaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.OperationFailException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.MoveOption;
import com.slamtec.slamware.robot.Rotation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private IMoveAction action;
    private AbstractSlamwarePlatform robotPlatform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        robotPlatform = DeviceManager.connect("10.0.130.71", 1445);

        Button buttonClockwise = (Button) findViewById(R.id.button_clockwise);
        Button buttonAnticlockwise = (Button) findViewById(R.id.button_anticlockwise);
        Button buttonRotateToYaw = (Button) findViewById(R.id.button_rotate_to);
        buttonClockwise.setOnClickListener(this);
        buttonAnticlockwise.setOnClickListener(this);
        buttonRotateToYaw.setOnClickListener(this);

    }

    private float setPI(int i) {
        return (float)(i*Math.PI/180);
    }

    @Override
    public void onClick(View view) {

        try {
            /* 如果当前正在运动，先停止 */
            if(action != null) {
                action.cancel();
            }

            switch (view.getId()) {
                /* Anticlockwise Rotate 180 */
                case R.id.button_anticlockwise:
                    action = robotPlatform.rotate(new Rotation(setPI(-180), 0, 0));
                    Log.d(TAG, "");
                    break;

                /* Clockwise Rotate 180 */
                case R.id.button_clockwise:
                    action = robotPlatform.rotate(new Rotation(setPI(180), 0, 0));
                    break;

                /* Rotate to 270 */
                case R.id.button_rotate_to:
                    action = robotPlatform.rotateTo(new Rotation(setPI(270), 0, 0));
                    break;

                default:
                    break;
            }
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