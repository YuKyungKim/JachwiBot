package com.soma.albert.jachwibot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class AlarmActivity extends Activity {

    public static final int MORNING_CALL = 0;

    static DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        dbManager = new DBManager(getApplicationContext(), "jachwibot.db", null, 1);
        setTitle("알람 추가");

        //현재 시간으로 설정
        Calendar mCalendar = Calendar.getInstance();
        TimePicker mTime = (TimePicker) findViewById(R.id.timePicker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                Calendar calendar = Calendar.getInstance();
                TimePicker mTime = (TimePicker) findViewById(R.id.timePicker);

                //알람 정보를 준비
                calendar.set(Calendar.HOUR_OF_DAY, mTime.getCurrentHour());
                calendar.set(Calendar.MINUTE, mTime.getCurrentMinute());
                boolean Week[] = {false, ((CheckBox) findViewById(R.id.SUN)).isChecked(),
                        ((CheckBox) findViewById(R.id.MON)).isChecked(),
                        ((CheckBox) findViewById(R.id.TUE)).isChecked(),
                        ((CheckBox) findViewById(R.id.WED)).isChecked(),
                        ((CheckBox) findViewById(R.id.THU)).isChecked(),
                        ((CheckBox) findViewById(R.id.FRI)).isChecked(),
                        ((CheckBox) findViewById(R.id.SAT)).isChecked()};

                //알람 등록
                int ID = alarm_set(((RadioButton) findViewById(R.id.morning_call)).isChecked(),
                        ((CheckBox) findViewById(R.id.repeat)).isChecked(), Week,
                        ((EditText)findViewById(R.id.name)).getText().toString(), calendar);
                switch (ID) {
                    case -1:
                        Toast.makeText(this, "요일을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    case -2:
                        Toast.makeText(this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                }

                Intent intent = new Intent();
                intent.putExtra("alarm_type", ID);
                this.setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.cancel:
                this.setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.morning_call:
                EditText editText = (EditText)findViewById(R.id.name);
                CheckBox box = (CheckBox) findViewById(R.id.repeat);

                editText.setEnabled(false);
                editText.setText("");
                box.setEnabled(false);
                box.setChecked(true);
                repeat_click();
                break;
            case R.id.alarm:
                findViewById(R.id.repeat).setEnabled(true);
                findViewById(R.id.name).setEnabled(true);
                break;
            case R.id.repeat:
                repeat_click();
                break;
        }
    }

    public void repeat_click() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.repeat);
        findViewById(R.id.SUN).setEnabled(checkBox.isChecked());
        findViewById(R.id.MON).setEnabled(checkBox.isChecked());
        findViewById(R.id.TUE).setEnabled(checkBox.isChecked());
        findViewById(R.id.WED).setEnabled(checkBox.isChecked());
        findViewById(R.id.THU).setEnabled(checkBox.isChecked());
        findViewById(R.id.FRI).setEnabled(checkBox.isChecked());
        findViewById(R.id.SAT).setEnabled(checkBox.isChecked());
        if (!checkBox.isChecked()) {
            ((CheckBox) findViewById(R.id.SUN)).setChecked(false);
            ((CheckBox) findViewById(R.id.MON)).setChecked(false);
            ((CheckBox) findViewById(R.id.TUE)).setChecked(false);
            ((CheckBox) findViewById(R.id.WED)).setChecked(false);
            ((CheckBox) findViewById(R.id.THU)).setChecked(false);
            ((CheckBox) findViewById(R.id.FRI)).setChecked(false);
            ((CheckBox) findViewById(R.id.SAT)).setChecked(false);
        }
    }

    public static void alarm_cancel(Context context, int id) {
        //Alarm Manager에서 알람을 삭제
        Intent intent = new Intent(context, AlarmReceiver.class);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);

        //DB에서 알람을 삭제
        dbManager.delete("ALARM_LIST", "alarm_type", new String[]{String.valueOf(id)});
    }

    private int alarm_set(boolean morning_call, boolean repeat, boolean Week[], String name, Calendar calendar) {
        AlarmManager am = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        int ID;

        //모닝콜이 아닌경우 새로운 알람의 ID를 구한다.
        if (!morning_call && !name.equals("")) {
            SQLiteDatabase db = dbManager.getReadableDatabase();
            Cursor cursor = db.rawQuery("select MAX(alarm_type) from ALARM_LIST", null);
            cursor.moveToFirst();
            ID = cursor.getInt(0) + 1;
            cursor.close();
        }
        else if(morning_call){
            name = "Morning Call";
            ID = MORNING_CALL;
        }
        else {
            return -2;
        }

        //Alarm Receiver에서 받을 알람 정보
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("Class", this.getIntent().getSerializableExtra("Class"));
        intent.putExtra("alarm_type", ID);
        intent.putExtra("alarm_name", name);
        intent.putExtra("isRepeat", repeat);
        intent.putExtra("week", Week);

        //DB에 추가할 알람정보를 만듬
        String Week_str = "";
        for (int i = 0; i < Week.length; i++) {
            Week_str += String.valueOf(Week[i]);
            if (i < Week.length - 1) {
                Week_str += "/";
            }
        }
        ContentValues values = new ContentValues();
        values.put("alarm_type", ID);
        values.put("alarm_name", name);
        values.put("isRepeat", String.valueOf(repeat));
        values.put("week", Week_str);
        values.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
        values.put("minute", calendar.get(Calendar.MINUTE));
        PendingIntent sender = PendingIntent.getBroadcast(getBaseContext(), ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //알람을 반복하는 경우 요일이 선택되어 있는지 확인
        if (repeat) {
            boolean Result = false;
            for (int i = 0; i < Week.length; i++) {
                Result |= Week[i];
            }
            if (Result) {
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
            } else {
                return -1;
            }
        } else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }

        //등록할 알람이 이미 DB에 저장되어 있는 경우 덮어쓴다.
        if (dbManager.selectAlarmDataById(ID) == null) {
            dbManager.insert("ALARM_LIST", values);
        } else {
            dbManager.update("ALARM_LIST", values, "alarm_type", new String[]{String.valueOf(ID)});
        }

        return ID;
    }
}
