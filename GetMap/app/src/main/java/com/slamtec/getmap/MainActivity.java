package com.slamtec.getmap;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.OperationFailException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.Map;
import com.slamtec.slamware.robot.MapKind;
import com.slamtec.slamware.robot.MapType;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    AbstractSlamwarePlatform robotPlatform;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 与底盘连接 */
        robotPlatform = DeviceManager.connect("10.0.129.192", 1445);
        if(robotPlatform != null) {
            System.out.println("Slamware connect success");
            Log.d(TAG, "Slamware connect success");
        } else {
            Log.d(TAG, "Slamware connect failed");
            System.out.println("Slamware connect failed");
        }

        try {
            List<MapType> mapTypes = robotPlatform.getAvailableMaps();
            MapType mapType = mapTypes.get(0);
            Map map = robotPlatform.getMap(mapType, MapKind.EXPLORE_MAP, new RectF(1.3f,1.1f,2.5f,2.5f));


            Log.d(TAG, "onCreate: Width = " + map.getDimension().getWidth() + "Height = " + map.getDimension().getHeight());
            Log.d(TAG, "onCreate: x =" + map.getResolution().getX() + "y = " + map.getResolution().getY());
            Log.d(TAG, "onCreate: x = " + map.getOrigin().getX() + "y = " + map.getOrigin().getY());


        } catch (OperationFailException e) {
            e.printStackTrace();
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ConnectionTimeOutException e) {
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            e.printStackTrace();
        } catch (RequestFailException e) {
            e.printStackTrace();
        }
    }
}