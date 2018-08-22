package com.slamtec.sdk_reference_android;

import android.app.Activity;
import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
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
import static android.graphics.Bitmap.Config.RGB_565;
import static android.graphics.BitmapFactory.decodeByteArray;
import static android.graphics.ColorSpace.Model.RGB;
import static com.slamtec.slamware.robot.MapType.BITMAP_8BIT;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    public static AbstractSlamwarePlatform robotPlatform;

    public static IMoveAction moveAction;

    private com.slamtec.slamware.robot.Map map;

    ImageView imageView;

    private String deviceId;


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
            handler.postDelayed(this, 100);// 间隔33ms
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

                /* 获取地图并刷新 */
                int mapWidth =0;
                int mapHeight = 0;

                RectF knownArea = robotPlatform.getKnownArea(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP);
                map = robotPlatform.getMap(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP, knownArea);
                mapWidth = map.getDimension().getWidth();
                mapHeight = map.getDimension().getHeight();

                Bitmap bitmap = Bitmap.createBitmap(mapWidth, mapHeight, ARGB_8888);

                for (int posY = 0; posY < mapHeight; ++posY) {
                    for (int posX = 0; posX < mapWidth; ++posX) {
                        // get map pixel
                        byte[] data = map.getData();

                        // (-128, 127) to (0, 255)

                        int rawColor = data[posX + posY * mapWidth];

                        rawColor += 127;

                        // fill the bitmap data
                        bitmap.setPixel(posX, posY, rawColor | rawColor<<8 | rawColor<<16 | 0xC0<<24);

                    }
                }

                BitmapDrawable bmpDraw=new BitmapDrawable(bitmap);

                imageView.setImageDrawable(bmpDraw);

                /* 更新机器人运动状态 */
                if (moveAction!=null && moveAction.isEmpty()) {
                    actionStatus.setText("IDLE");
                } else {
                    switch (moveAction.getStatus()) {
                        case WAITING_FOR_START:
                            actionStatus.setText("WAITING_FOR_START");
                            break;
                        case RUNNING:
                            actionStatus.setText("RUNNING");
                            break;
                        case ERROR:
                            actionStatus.setText("ERROR");
                            break;
                        case FINISHED:
                            actionStatus.setText("FINISHED");
                            break;
                        case PAUSED:
                            actionStatus.setText("PAUSED");
                            break;
                        case STOPPED:
                            actionStatus.setText("STOPPED");
                            break;

                    }

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
            } catch (Exception e) {
                e.printStackTrace();
//                Toast.makeText(MainActivity.this,  "发生异常，程序退出", Toast.LENGTH_SHORT).show();
            }
        }
    };



    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.postDelayed(runnable, 50);

        imageView = (ImageView)findViewById(R.id.slamware_map);

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

        targetX = (EditText) findViewById(R.id.target_x);
        targetY = (EditText) findViewById(R.id.target_y);

        Button button = (Button) findViewById(R.id.to_point);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(targetX.length()==0 || targetY.length()==0) {
                    Toast.makeText(MainActivity.this, "请输入目标点坐标", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        float x = Float.parseFloat(targetX.getText().toString());
                        float y = Float.parseFloat(targetY.getText().toString());

                        MoveOption moveOption = new MoveOption();
                        moveOption.setPrecise(true);
                        moveOption.setMilestone(true);

                        Log.d(TAG, "Move To");

                        moveAction = robotPlatform.moveTo(new Location(x, y, 0), moveOption, 0);
//                        action.waitUntilDone();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable); //停止刷新
        robotPlatform.disconnect();
        super.onDestroy();
    }


}
