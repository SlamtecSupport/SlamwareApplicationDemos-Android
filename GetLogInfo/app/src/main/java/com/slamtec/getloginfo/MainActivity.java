package com.slamtec.getloginfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // connect to the robot.
            String ip = "10.16.2.160";
//            String ip = "192.168.11.1";
            int port = 1445;
            Log.i("MyCorePlatform", "start to connect.");
            com.slamtec.slamware.AbstractSlamwarePlatform corePltfm = com.slamtec.slamware.discovery.DeviceManager.connect(ip, port);
            if (null == corePltfm)
            {
                Log.e("MyCorePlatform", "Failed to connect.");
                return;
            }
            Log.i("MyCorePlatform", "Connected");

            // Create Customer Log Receiver for receiving logs;
            // If the connection is lost, the old log receiver will not work, a new one should be created.
            com.slamtec.slamware.log.customer.ICustomerLogReceiver logReceiver = corePltfm.createCustomerLogReceiver();
            if (null == logReceiver)
            {
                Log.e("MyLogReceiver", "Failed to create.");
                return;
            }
            Log.i("MyLogReceiver", "Created");

            com.slamtec.slamware.log.customer.CustomerLogReceiverInitArg initArg = null;
            // if you want to resume the last receive status, load the saved status data like the following codes:
            /*
            {
                initArg = new com.slamtec.slamware.log.customer.CustomerLogReceiverInitArg();
                initArg.setLastRecvStatus(lastRecvStatus); // lastRecvStatus is the last receive status data you saved before
            }
            */

            com.slamtec.slamware.log.customer.ResultCode resCode = logReceiver.init(initArg);
            if (com.slamtec.slamware.log.customer.ResultCode.Ok != resCode)
            {
                Log.e("MyLogReceiver", "Failed to init, resCode: " + resCode.name());
                return;
            }

            boolean isWorking = true;
            while (isWorking)
            {
                // some other operations of your application

                int maxCntToRead = 0; // set to zero to use the default value of the robot.
                com.slamtec.slamware.utils.StdPair<com.slamtec.slamware.log.customer.ResultCode, com.slamtec.slamware.log.customer.ReadResult> pairRes
                        = logReceiver.recvLogs(maxCntToRead);
                if (com.slamtec.slamware.log.customer.ResultCode.Ok == pairRes.getFirst())
                {
                    showMyLogsResult_(pairRes.getSecond());
                }
                else
                {
                    Log.e("MyLogs", ("ResultCode: " + pairRes.getFirst().name()));
                }

                // some other operations of your application
                Thread.sleep(100);
            }

            // you may save it for the next time to continue to receive logs if you want.
            /*
            {
                com.slamtec.slamware.log.customer.CustomerLogReceiverLastRecvStatus lastRecvStatus = logReceiver.getLastRecvStatus();
                // now you can save lastRecvStatus somewhere for the next time
            }
            */
        }
        catch (Exception e) {
            Log.e("MyException", e.toString());
        }

    }

    private void showMyLogsResult_(com.slamtec.slamware.log.customer.ReadResult tRes) {

        java.util.ArrayList<com.slamtec.slamware.log.LogData> logsList = tRes.getLogs();
        int iLogsCnt = (null != logsList ? logsList.size() : 0);
        Log.i("MyLogs", ("LogsCnt: " + iLogsCnt));
        if (iLogsCnt < 1)
            return;

        for (int i = 0; i < iLogsCnt; ++i)
        {
            Log.i("MyLogs", ("logs[" + i + "]"));
            com.slamtec.slamware.log.LogData logDat = logsList.get(i);
            if (null == logDat)
            {
                // it should not be null here, there may be some problems.
                Log.e("MyLogs", "null log data.");
                continue;
            }

            boolean bIsJsonLog = logDat.getIsJsonLog();
            if (bIsJsonLog)
            {
                // if it is json Log, you can parse the string of log to a JSON object.
                // ALL customer logs are json log.
            }

            String strMsg = ("LogSource: " + logDat.getLogSource() + "\n");
            strMsg += ("LogLevel: " + logDat.getLogLevel().name() + "\n");
            strMsg += ("IsJsonLog: " + bIsJsonLog + "\n");
            strMsg += ("StringOfLog: " + logDat.getStringOfLog() + "\n");
            Log.i("MyLogs", strMsg);
        }
    }

}
