package com.slamtec.sdk_reference_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.slamtec.slamware.discovery.DeviceManager;

import static com.slamtec.sdk_reference_android.MainActivity.robotPlatform;

public class LoginActivity extends AppCompatActivity {

    private EditText ip_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ip_address = (EditText) findViewById(R.id.ip_address);

        String digits = "0123456789.";
        ip_address.setInputType(InputType.TYPE_CLASS_NUMBER);
        ip_address.setKeyListener(DigitsKeyListener.getInstance(digits));

        Button buttonLogin = (Button) findViewById(R.id.button_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ip_address.length() == 0 || ip_address.length() == 0) {
                    //    System.out.println("请输入机器人IP地址");
                    Toast.makeText(LoginActivity.this, "请输入机器人IP地址", Toast.LENGTH_LONG).show();
                } else {

                    String ip = ip_address.getText().toString();


                    try {
                        robotPlatform = DeviceManager.connect(ip, 1445);

                        if (robotPlatform == null) {
                            Toast.makeText(LoginActivity.this, "连接失败，请输入正确的IP地址", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(LoginActivity.this, "连接成功", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "连接失败，请输入正确的IP地址", Toast.LENGTH_SHORT).show();

                    }





                }
            }


        });
    }
}
