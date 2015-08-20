package com.soma.albert.jachwibot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.smartrobot.android.RobotActivity;


public class MainActivity extends RobotActivity implements View.OnClickListener{

    private static final int ALARM_REQUEST = 10;
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.albertConnectBtn:
                Intent serviceIntent = new Intent(this, LuncherService.class);
                startService(serviceIntent);
                break;
            case R.id.Alarm_set:
                Intent intent = new Intent(this, AlarmActivity.class);
                intent.putExtra("Class", this.getClass());
                startActivityForResult(intent, ALARM_REQUEST);
                break;
            case R.id.Alarm_Cancel:
                AlarmActivity.alarm_cancel(this, AlarmActivity.MORNING_CALL);
                AlarmActivity.alarm_cancel(this, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ALARM_REQUEST:
                if (resultCode == RESULT_OK) {
                    int Alarm_data = data.getIntExtra("Alarm_ID", -1);
                    Log.d("Alarm", "ID = " + String.valueOf(Alarm_data));
                }
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("Alarm", "ID = " + String.valueOf(intent.getIntExtra("ID", -1)) +
                " Repeat = " + String.valueOf(intent.getBooleanExtra("Repeat", false)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button albertConnectBtn = (Button) findViewById(R.id.albertConnectBtn);
        albertConnectBtn.setOnClickListener(this);
        findViewById(R.id.Alarm_set).setOnClickListener(this);
        findViewById(R.id.Alarm_Cancel).setOnClickListener(this);
    }
}
