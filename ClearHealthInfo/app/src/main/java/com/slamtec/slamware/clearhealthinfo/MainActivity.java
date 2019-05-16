package com.slamtec.slamware.clearhealthinfo;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.internal.SlamwareLogUtils;
import com.slamtec.slamware.robot.HealthInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AbstractSlamwarePlatform mRobotPlatform;
    private String mLogStorePath;
    private ProgressDialog mFetchLogDialog;
    private Button mButtonFetchAllLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect to slanware
        mRobotPlatform = DeviceManager.connect("192.168.11.1", 1445);
        if (mRobotPlatform == null) {
            Toast.makeText(this, "Connect Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearHealthInfo(View view) {
        try {

            HealthInfo robotHealth = mRobotPlatform.getRobotHealth();
            ArrayList<HealthInfo.BaseError> errors = robotHealth.getErrors();
            for (int i = 0; i < errors.size(); i++) {
                mRobotPlatform.clearRobotHealth(errors.get(i).getErrorCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


