package com.slamtec.recoverlocalization;

import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.IAction;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.RecoverLocalizationOptions;

import static com.slamtec.slamware.robot.RecoverLocalizationMovement.Any;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("192.168.11.1", 1445);



        try {
            RecoverLocalizationOptions recoverLocalizationOptions = new RecoverLocalizationOptions();
            recoverLocalizationOptions.setMaxRecoverTimeInMilliSeconds(4000);
            recoverLocalizationOptions.setRecoverMovementType(Any);
            IMoveAction action = robotPlatform.recoverLocalization(new RectF(0f, 0f, 0f, 0f), recoverLocalizationOptions);

            action.waitUntilDone();
            Log.d(TAG, "onCreate: =================");

            Log.d(TAG, "onCreate: action = " + action.getStatus());


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
