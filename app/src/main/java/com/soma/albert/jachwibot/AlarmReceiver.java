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
        boolean Week[] = intent.getBooleanArrayExtra("Week");
        Calendar calendar = Calendar.getInstance();
        if (Week[calendar.get(Calendar.DAY_OF_WEEK)]) {
            Intent TempIntent = new Intent(context, (Class)intent.getSerializableExtra("Class"));
            TempIntent.putExtra("Repeat", intent.getBooleanExtra("Repeat", false));
            TempIntent.putExtra("ID", intent.getIntExtra("ID", -1));
            TempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(TempIntent);
        }
    }
}