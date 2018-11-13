package com.slamtec.simplecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.OperationFailException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.Pose;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private AbstractSlamwarePlatform robotPlatform;
    private IMoveAction moveAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 连接底盘 */
        robotPlatform = DeviceManager.connect("10.0.129.192", 1445);
        if(robotPlatform != null) {
            Log.d(TAG, "Slamware connect success");
        }

        Button button_stop = (Button) findViewById(R.id.button_stop);
        Button button_forward = (Button) findViewById(R.id.button_forward);
        Button button_backward = (Button) findViewById(R.id.button_backward);
        Button button_turn_left = (Button) findViewById(R.id.button_turn_left);
        Button button_turn_right = (Button) findViewById(R.id.button_turn_right);

        button_stop.setOnClickListener(this);
        button_forward.setOnClickListener(this);
        button_backward.setOnClickListener(this);
        button_turn_left.setOnClickListener(this);
        button_turn_right.setOnClickListener(this);
        button_backward.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        try{
            switch (view.getId()) {

                case R.id.button_stop:
                    if(moveAction != null) {
                        moveAction.cancel();
                    }
                    break;
                case R.id.button_forward:
                    moveAction = robotPlatform.moveBy(MoveDirection.FORWARD);
                    break;
                case R.id.button_backward:
                    moveAction = robotPlatform.moveBy(MoveDirection.BACKWARD);
                    break;
                case R.id.button_turn_left:
                    moveAction = robotPlatform.moveBy(MoveDirection.TURN_LEFT);
                    break;
                case R.id.button_turn_right:
                    moveAction = robotPlatform.moveBy(MoveDirection.TURN_RIGHT);
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