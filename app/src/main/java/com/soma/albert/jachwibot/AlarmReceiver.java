package com.soma.albert.jachwibot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by wlsdn on 2015-08-14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean Week[] = intent.getBooleanArrayExtra("week");
        boolean repeat = intent.getBooleanExtra("isRepeat", false);
        Calendar calendar = Calendar.getInstance();

        Intent TempIntent = new Intent(context, (Class) intent.getSerializableExtra("Class"));
        TempIntent.putExtra("alarm_type", intent.getIntExtra("alarm_type", -1));
        TempIntent.putExtra("alarm_name", intent.getStringExtra("alarm_name"));
        TempIntent.putExtra("isRepeat", repeat);
        TempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(!repeat) {
            //1회용 알람인경우 DB에서 삭제
            DBManager dbManager = new DBManager(context, "jachwibot.db", null, 1);
            dbManager.delete("ALARM_LIST", "alarm_type", new String[]{String.valueOf(intent.getIntExtra("ID", -1))});
            context.startActivity(TempIntent);
        }
        else if (Week[calendar.get(Calendar.DAY_OF_WEEK)]) {
            //오늘이 알람을 실행할 요일인지 확인
            context.startActivity(TempIntent);
        }
    }
}