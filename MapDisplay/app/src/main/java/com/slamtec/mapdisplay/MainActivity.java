package com.slamtec.mapdisplay;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.MapKind;
import com.slamtec.slamware.robot.MapType;
import com.slamtec.slamware.robot.Pose;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private com.slamtec.slamware.robot.Map map;

    ImageView imageView;
    Bitmap bitmap;

    private String deviceId;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 33);// 间隔33ms
        }

        /* 获取地图并刷新 */
        void update() {
            try {
                int mapWidth = 0;
                int mapHeight = 0;

                /*
                 * 此程序仅做演示显示地图及地图平移缩放之用，固定时间间隔加载完整的地图，
                 * 在地图过大时会有卡顿现象,请用户自行将地图分为若干个区域分别刷新。
                 */

                RectF knownArea = robotPlatform.getKnownArea(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP);
                map = robotPlatform.getMap(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP, knownArea);
                mapWidth = map.getDimension().getWidth();
                mapHeight = map.getDimension().getHeight();

                bitmap = Bitmap.createBitmap(mapWidth, mapHeight, ARGB_8888);

                for (int posY = 0; posY < mapHeight; ++posY) {
                    for (int posX = 0; posX < mapWidth; ++posX) {
                        // get map pixel
                        byte[] data = map.getData();

                        // (-128, 127) to (0, 255)
                        int rawColor = data[posX + posY * mapWidth];
                        rawColor += 127;

                        // fill the bitmap data
                        bitmap.setPixel(posX, posY, rawColor | rawColor << 8 | rawColor << 16 | 0xC0 << 24);

                    }
                }

                BitmapDrawable bmpDraw = new BitmapDrawable(bitmap);

                imageView.setImageDrawable(bmpDraw);

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

        imageView = (ImageView) findViewById(R.id.slamware_map);

        imageView.setOnTouchListener(new TouchListener());

        if (robotPlatform != null) {

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
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable); //停止刷新
        robotPlatform.disconnect();
        super.onDestroy();
    }

    private final class TouchListener implements View.OnTouchListener {

        private int mode = 0;
        private static final int MODE_DRAG = 1;
        private static final int MODE_ZOOM = 2;
        private PointF startPoint = new PointF();
        private Matrix matrix = new Matrix();
        private Matrix currentMatrix = new Matrix();
        private float startDis;
        private PointF midPoint;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    mode = MODE_DRAG;
                    currentMatrix.set(imageView.getImageMatrix());
                    startPoint.set(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE:

                    if (mode == MODE_DRAG) {
                        float dx = event.getX() - startPoint.x;
                        float dy = event.getY() - startPoint.y;

                        matrix.set(currentMatrix);
                        matrix.postTranslate(dx, dy);
                    } else if (mode == MODE_ZOOM) {
                        float endDis = distance(event);
                        if (endDis > 10f) {
                            float scale = endDis / startDis;
                            matrix.set(currentMatrix);
                            matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE_ZOOM;

                    startDis = distance(event);

                    if (startDis > 10f) {
                        midPoint = mid(event);
                        currentMatrix.set(imageView.getImageMatrix());
                    }
                    break;
            }
            imageView.setImageMatrix(matrix);
            return true;
        }


        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        private PointF mid(MotionEvent event) {
            float midX = (event.getX(1) + event.getX(0)) / 2;
            float midY = (event.getY(1) + event.getY(0)) / 2;
            return new PointF(midX, midY);
        }
    }

}

