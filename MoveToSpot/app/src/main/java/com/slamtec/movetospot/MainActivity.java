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
import com.slamtec.slamware.geometry.Line;
import com.slamtec.slamware.geometry.PointF;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.MoveOption;
import static com.slamtec.slamware.robot.ArtifactUsage.ArtifactUsageVirtualTrack;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.text_view);

        final AbstractSlamwarePlatform robotPlatform = DeviceManager.connect("192.168.11.1", 1445);


        try {
            IMoveAction action;
            MoveOption moveOption = new MoveOption();

            moveOption.setPrecise(true);
            moveOption.setMilestone(true);

            Log.d(TAG, "Move To");
            Location location1 = new Location(0, 1, 0);
            Location location2 = new Location(1, 0, 0);
            Location location3 = new Location(0, 0, 0);

            action = robotPlatform.moveTo(location1, moveOption, 0);
        //    textView.setText("Move to (0, 1, 0) ...");
            action.waitUntilDone();

            action = robotPlatform.moveTo(location2, moveOption, 0);
        //    textView.setText("Move to (1, 0, 0) ...");
            action.waitUntilDone();

            action = robotPlatform.moveTo(location3, moveOption, 0);
        //    textView.setText("Move to (0, 0, 0) ...");
            action.waitUntilDone();



            // draw a virtual track from (0, 0) to (2, 0), then move to (0, 0) via virtual track
            Log.d(TAG, "Virtual Track");

            Line line= new Line(new PointF(0, 0), new PointF(1, 0));

            robotPlatform.addLine(ArtifactUsageVirtualTrack, line);

            moveOption.setKeyPoints(true);
            moveOption.setPrecise(true);
            action = robotPlatform.moveTo(new Location(1, 0, 0), moveOption, 0);
        //    textView.setText("Move to (0, 1, 0) by Virtual Track ...");
            action.waitUntilDone();
            if (action.getStatus() == ActionStatus.ERROR) {
                Log.d(TAG, "Action Failed: " + action.getReason());
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
