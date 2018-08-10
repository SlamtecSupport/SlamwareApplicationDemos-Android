package com.slamtec.sdk_reference_android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Map;
import com.slamtec.slamware.robot.MapKind;
import com.slamtec.slamware.robot.MapType;
import com.slamtec.slamware.robot.MoveOption;
import com.slamtec.slamware.robot.Pose;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.BitmapFactory.decodeByteArray;
import static android.graphics.ColorSpace.Model.RGB;
import static com.slamtec.slamware.robot.MapType.BITMAP_8BIT;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    AbstractSlamwarePlatform robotPlatform;


    private String deviceId;

    Button button_forward;
    Button button_backward;
    Button button_turn_left;
    Button button_turn_right;
    TextView current_location_x;
    TextView current_location_y;
    TextView current_location_yaw;

    TextView current_battery_percentage;
    TextView actionStatus;
    TextView firmwareVesion;
    TextView sdkVersion;

    EditText targetX;
    EditText targetY;




    private int i = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 1000);// 间隔100ms
        }

        void update() {
            try {

                /* 刷新Pose */
                Pose pose = robotPlatform.getPose();
                current_location_x.setText(Float.toString(pose.getX()));
                current_location_y.setText(Float.toString(pose.getY()));
                current_location_yaw.setText(Float.toString(pose.getYaw()));

                /* 刷新电量 */
                int percentage = robotPlatform.getBatteryPercentage();
                current_battery_percentage.setText(Integer.toString(percentage));


//                System.out.println(" get map from slamware ==========");
//                Log.d(TAG, " get map from slamware ==========");

                /* 获取地图并刷新 */
                com.slamtec.slamware.robot.Map map;
                int mapWidth =0;
                int mapHeight = 0;

                RectF knownArea = robotPlatform.getKnownArea(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP);
                map = robotPlatform.getMap(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP, knownArea);
                mapWidth = map.getDimension().getWidth();
//                System.out.println("mapWidth = " + mapWidth);
                mapHeight = map.getDimension().getHeight();
//                System.out.println("mapHeight = " + mapHeight);

                Bitmap bitmap = Bitmap.createBitmap(mapWidth, mapHeight, ARGB_8888);

                for (int posY = 0; posY < mapHeight; ++posY) {
                    for (int posX = 0; posX < mapWidth; ++posX) {
                        // get map pixel
                        byte[] data = map.getData();
                        byte mapValue_8bit = (byte) (data[posX + posY * mapWidth]);

                        // fill the bitmap data
                        bitmap.setPixel(posX, posY, mapValue_8bit<<24 | mapValue_8bit<<16 | mapValue_8bit<<8);
                    }
                }

                BitmapDrawable bmpDraw=new BitmapDrawable(bitmap);
                ImageView imageView = (ImageView)findViewById(R.id.slamware_map);
                imageView.setImageDrawable(bmpDraw);
//                System.out.println("ImageView Drawable");
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
            } catch (Exception e) {
                e.printStackTrace();
            }



        }
    };

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.postDelayed(runnable, 10);


        robotPlatform = DeviceManager.connect("10.0.130.71", 1445);

        if(robotPlatform != null) {

            Log.d(TAG, "Slamware connect success");

            try {
                Pose pose = robotPlatform.getPose();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                deviceId = robotPlatform.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        System.out.println("deviceId = " + deviceId);

        current_location_x = (TextView) findViewById(R.id.location_x);
        current_location_y = (TextView) findViewById(R.id.location_y);
        current_location_yaw = (TextView) findViewById(R.id.location_yaw);
        current_battery_percentage = (TextView) findViewById(R.id.current_batteryPercentage);
        actionStatus = (TextView) findViewById(R.id.action_status);


//        firmwareVesion = (TextView) findViewById(R.id.firmware_vesion);
//        sdkVersion = (TextView) findViewById(R.id.sdk_version);


        // go forward
        button_forward = (Button) findViewById(R.id.button_forward);
        button_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    robotPlatform.moveBy(MoveDirection.FORWARD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        targetX = (EditText) findViewById(R.id.target_x);
        targetY = (EditText) findViewById(R.id.target_y);

        Button button = (Button) findViewById(R.id.to_point);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(targetX.length()==0 || targetY.length()==0) {
                    System.out.println("请输入目标点坐标");
                //    Toast.makeText(this, "请输入目标点坐标", Toast.LENGTH_LONG).show();
                } else {

                    try {
                        float x = Float.parseFloat(targetX.getText().toString());
                        float y = Float.parseFloat(targetY.getText().toString());

                        System.out.println("x = " + x);
                        System.out.println("y = " + y);

                        IMoveAction action;
                        MoveOption moveOption = new MoveOption();

                        moveOption.setPrecise(true);
                        moveOption.setMilestone(true);

                        Log.d(TAG, "Move To");
                        Location location1 = new Location(x, y, 0);


                        action = robotPlatform.moveTo(location1, moveOption, 0);
                        //    textView.setText("Move to (0, 1, 0) ...");
                        action.waitUntilDone();
                        System.out.println(action.getStatus());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }



            }
        });


        // go backward
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    robotPlatform.moveBy(MoveDirection.BACKWARD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // turn left
        button_turn_left = (Button) findViewById(R.id.button_turn_left);
        button_turn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    robotPlatform.moveBy(MoveDirection.TURN_LEFT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        // turn right
        button_turn_right = (Button) findViewById(R.id.button_turn_right);
        button_turn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    robotPlatform.moveBy(MoveDirection.TURN_RIGHT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable); //停止刷新
        super.onDestroy();
    }


}
