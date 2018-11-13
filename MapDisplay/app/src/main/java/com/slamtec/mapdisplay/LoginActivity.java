package com.slamtec.mapdisplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.DeviceManager;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    EditText et_ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_ipAddress = (EditText) findViewById(R.id.ip_address);

        String digits = "0123456789.";
        et_ipAddress.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_ipAddress.setKeyListener(DigitsKeyListener.getInstance(digits));

        Button buttonLogin = (Button) findViewById(R.id.button_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_ipAddress.length() == 0 || et_ipAddress.length() == 0) {
                    Toast.makeText(LoginActivity.this, "请输入机器人IP地址", Toast.LENGTH_LONG).show();
                } else {

                    String ip = et_ipAddress.getText().toString();

                    Log.d(TAG, "onClick: ip: " + ip);


                    try {
                        robotPlatform = DeviceManager.connect("10.16.2.160", 1445);

                        if (robotPlatform == null) {
                            Toast.makeText(LoginActivity.this, "连接失败，请输入正确的IP地址", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(LoginActivity.this, "连接成功", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "onClick: " + e);
                        Toast.makeText(LoginActivity.this, "连接失败，请输入正确的IP地址", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
