package com.soma.albert.jachwibot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmActivity extends Activity {

    public static  final int MORNING_CALL = 0;
    private static int alarm_count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Calendar mCalendar = Calendar.getInstance();
        TimePicker mTime = (TimePicker) findViewById(R.id.timePicker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                boolean Week[] = {false, ((CheckBox) findViewById(R.id.SUN)).isChecked(),
                        ((CheckBox) findViewById(R.id.MON)).isChecked(),
                        ((CheckBox) findViewById(R.id.TUE)).isChecked(),
                        ((CheckBox) findViewById(R.id.WED)).isChecked(),
                        ((CheckBox) findViewById(R.id.THU)).isChecked(),
                        ((CheckBox) findViewById(R.id.FRI)).isChecked(),
                        ((CheckBox) findViewById(R.id.SAT)).isChecked()};

                TimePicker mTime = (TimePicker) findViewById(R.id.timePicker);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, mTime.getCurrentHour());
                calendar.set(Calendar.MINUTE, mTime.getCurrentMinute());
                int ID = alarm_set(((RadioButton) findViewById(R.id.morning_call)).isChecked(),
                        ((CheckBox) findViewById(R.id.repeat)).isChecked(), Week, calendar);
                if(ID == -1)
                {
                    Toast.makeText(this, "요일을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("Alarm_ID", ID);
                this.setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.cancel:
                this.setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.morning_call:
                CheckBox box = (CheckBox) findViewById(R.id.repeat);
                box.setChecked(true);
                box.setEnabled(false);
                repeat_click();
                break;
            case R.id.alarm:
                findViewById(R.id.repeat).setEnabled(true);
                break;
            case R.id.repeat:
                repeat_click();
                break;
        }
    }
    public void repeat_click() {
        CheckBox checkBox = (CheckBox)findViewById(R.id.repeat);
        findViewById(R.id.SUN).setEnabled(checkBox.isChecked());
        findViewById(R.id.MON).setEnabled(checkBox.isChecked());
        findViewById(R.id.TUE).setEnabled(checkBox.isChecked());
        findViewById(R.id.WED).setEnabled(checkBox.isChecked());
        findViewById(R.id.THU).setEnabled(checkBox.isChecked());
        findViewById(R.id.FRI).setEnabled(checkBox.isChecked());
        findViewById(R.id.SAT).setEnabled(checkBox.isChecked());

        if(!checkBox.isChecked()) {
            ((CheckBox)findViewById(R.id.SUN)).setChecked(false);
            ((CheckBox)findViewById(R.id.MON)).setChecked(false);
            ((CheckBox)findViewById(R.id.TUE)).setChecked(false);
            ((CheckBox)findViewById(R.id.WED)).setChecked(false);
            ((CheckBox)findViewById(R.id.THU)).setChecked(false);
            ((CheckBox)findViewById(R.id.FRI)).setChecked(false);
            ((CheckBox)findViewById(R.id.SAT)).setChecked(false);
        }
    }

    public static void alarm_cancel(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }
    private int alarm_set(boolean morning_call, boolean repeat, boolean Week[],  Calendar calendar) {
        AlarmManager am = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        int ID;
        if(morning_call){
            ID = MORNING_CALL;
        }
        else{
            ID = alarm_count;
        }

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("Class", this.getIntent().getSerializableExtra("Class"));
        intent.putExtra("Repeat", repeat);
        intent.putExtra("Week", Week);
        intent.putExtra("ID", ID);

        String Week_str = "";
        for(int i = 0; i < Week.length; i++) {
            Week_str += String.valueOf(Week[i]);
            if(i < Week.length - 1) {
                Week_str += "/";
            }
        }
        final DBManager dbManager = new DBManager(getBaseContext(),"jachwibot.db", null, 1);

        dbManager.insert("insert into ALARM_LIST values(null," +
                String.valueOf(ID) + "," +
                "'" + String.valueOf(repeat) + "'," +
                "'" + Week_str + "'," +
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + "," +
                String.valueOf(calendar.get(Calendar.MINUTE)) +
                ");");

        AlarmComponent test = dbManager.selectAlarmDataById(0);
        Log.d("DB Data", String.valueOf(test.getMin()));

        /*
        PendingIntent sender = PendingIntent.getBroadcast(getBaseContext(), ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if(repeat){
            boolean Result = false;
            for(int i = 0; i < Week.length; i++){
                Result |= Week[i];
            }
            if(Result){
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
            }
            else {
                return -1;
            }
        }
        else{
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
        */

        return ID;
    }
}
