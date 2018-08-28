package com.slamtec.movetospot;

import android.app.Notification;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.ActionStatus;
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
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.MoveOption;
import static com.slamtec.slamware.robot.ArtifactUsage.ArtifactUsageVirtualTrack;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private IMoveAction action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.text_view);

        final AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("10.0.130.71", 1445);


        try {
            MoveOption moveOption = new MoveOption();

            moveOption.setPrecise(true);
            moveOption.setMilestone(true);

            Location location1 = new Location(0, 1, 0);
            Location location2 = new Location(1, 0, 0);
            Location location3 = new Location(0, 0, 0);

            action = robotPlatform.moveTo(location3, moveOption, 0);
            action.waitUntilDone();

            action = robotPlatform.moveTo(location1, moveOption, 0);
            action.waitUntilDone();

            action = robotPlatform.moveTo(location2, moveOption, 0);
            action.waitUntilDone();

            action = robotPlatform.moveTo(location3, moveOption, 0);
            action.waitUntilDone();

            Log.d(TAG, "========== Virtual Track ==========");
            /* draw a virtual track from (0, 0) to (2, 0), then move to (0, 0) via virtual track */
            robotPlatform.addLine(ArtifactUsageVirtualTrack, new Line(new PointF(0, 0), new PointF(2, 1)));

            moveOption.setKeyPoints(true);
            moveOption.setPrecise(true);
            action = robotPlatform.moveTo(new Location(1.2f, 0, 0), moveOption, 0);
            action.waitUntilDone();
            if (action.getStatus() == ActionStatus.ERROR) {
                Log.d(TAG, "Action Failed: " + action.getReason());
            }
        } catch (ConnectionTimeOutException e) {
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ParseInvalidException e) {
            e.printStackTrace();
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (RequestFailException e) {
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            e.printStackTrace();
        } catch (OperationFailException e) {
            e.printStackTrace();
        }
    }

}
