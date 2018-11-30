package com.slamtec.www.virtualtrackwithoa;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.slamtec.slamware.geometry.Line;
import com.slamtec.slamware.geometry.PointF;
import com.slamtec.slamware.robot.LaserPoint;
import com.slamtec.slamware.robot.LaserScan;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.MoveOption;
import com.slamtec.slamware.robot.Pose;

import java.util.Vector;

import static com.slamtec.slamware.action.ActionStatus.ERROR;
import static com.slamtec.slamware.robot.ArtifactUsage.ArtifactUsageVirtualTrack;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 与底盘连接 */
        final AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("10.16.2.160", 1445);

        try {

            //draw a 6 meter virtual track
            Pose pose = robotPlatform.getPose();
            Line line = new Line(new PointF(pose.getX(), pose.getY()), new PointF(pose.getX()+6, pose.getY()));
            robotPlatform.addLine(ArtifactUsageVirtualTrack, line);

            IMoveAction action = robotPlatform.getCurrentAction();
            if (action != null)
                action.cancel();

            MoveOption moveOption = new MoveOption();
            moveOption.setTrackWithOA(true);
            moveOption.setKeyPoints(true);
            action = robotPlatform.moveTo(new Location(7, 0, 0), moveOption, 0);

            action.waitUntilDone();
            if (action.getStatus() == ERROR)
                Log.d(TAG, "Action Failed");

        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (ParseInvalidException e) {
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
        } catch (OperationFailException e) {
            e.printStackTrace();
        }
    }
}
