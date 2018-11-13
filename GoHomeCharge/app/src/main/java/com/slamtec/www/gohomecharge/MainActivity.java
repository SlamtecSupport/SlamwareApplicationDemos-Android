package com.slamtec.www.gohomecharge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.slamtec.slamware.AbstractSlamwarePlatform;
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

import static com.slamtec.slamware.action.ActionStatus.FINISHED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    final int maxGoHomeTimes = 3;
    private IMoveAction action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("10.0.130.71", 1445);

        try {
            int goHomeCount = 0;

            do {
                action = robotPlatform.goHome();
                action.waitUntilDone();
                if (action.getStatus() == FINISHED)
                    break;
                Log.d(TAG, "go home to charge times count: " + goHomeCount);
                goHomeCount++;
            } while (goHomeCount <= maxGoHomeTimes);

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


