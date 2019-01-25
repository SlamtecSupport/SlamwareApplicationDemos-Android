package com.slamtec.slamware.fecthslamwarelog;

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
import com.slamtec.slamware.internal.SlamwareLogUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private AbstractSlamwarePlatform mRobotPlatform;
    private String mLogStorePath;
    private ProgressDialog mFetchLogDialog;
    private Button mButtonFetchAllLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonFetchAllLog = findViewById(R.id.btn_fetch_all_log);
        mButtonFetchAllLog.setOnClickListener(this);

        // 连接slanware
        mRobotPlatform = DeviceManager.connect("192.168.11.1", 1445);
        mLogStorePath = Environment.getExternalStorageDirectory() + "/slamtec/logs/";

        Log.d(TAG, "mRobotPlatform : " + mRobotPlatform);
        Log.d(TAG, "mLogStorePath : " + mLogStorePath);

        if (null == mRobotPlatform) {
            Toast.makeText(this, "连接失败，请尝试重新连接", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.btn_fetch_all_log == v.getId()) {

            RxPermissions rxPermission = new RxPermissions(MainActivity.this);

            rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            mFetchLogDialog = new ProgressDialog(MainActivity.this);
                            mFetchLogDialog.setMessage("正在获取Log");
                            mFetchLogDialog.setCancelable(false);
                            mFetchLogDialog.setCanceledOnTouchOutside(false);
                            mFetchLogDialog.show();

                            new Thread(new JobFetchAllDebugData()).start();

                        } else {
                            Toast.makeText(MainActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private class JobFetchAllDebugData implements Runnable {
        @Override
        public void run() {

            String pathName = SlamwareLogUtils.saveSlamwareLog(mRobotPlatform, mLogStorePath);

            Looper.prepare();
            mFetchLogDialog.dismiss();
            if (null == pathName) {
                Toast.makeText(MainActivity.this, "获取Log失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "获取log成功,文件保存在" + pathName, Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    }
}


