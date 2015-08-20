package com.soma.albert.jachwibot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.roboid.robot.Robot;
import org.smartrobot.android.SmartRobot;

public class LuncherService extends Service{

    SmartRobot robot;

    public boolean waitstatus = true;
    private static final String TAG = "LauncherService";

    public LuncherService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = getPackageManager().getLaunchIntentForPackage("org.smartrobot.android.launcher");
        startActivity(intent);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent myapp = new Intent(getApplicationContext(), MainActivity.class);
        myapp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myapp);

        this.stopSelf();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
