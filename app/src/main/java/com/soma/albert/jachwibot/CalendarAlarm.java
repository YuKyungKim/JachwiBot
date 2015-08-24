package com.soma.albert.jachwibot;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.Format;
import java.util.Date;

/**
 * Created by josh on 15. 8. 21..
 */
public class CalendarAlarm {

    private Cursor mCursor = null;
    private Context context;
    private static final String[] COLS = new String[]
            {CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};

    public CalendarAlarm(Context current){
        context=current;
    }
    public String get_first_schedule() {//null값이 리턴되면 그날에 일정이 없음. string은 그날의 첫 일정


        mCursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI, COLS, null, null, null);
        mCursor.moveToFirst();


        String title = "N/A";


        Long start = 0L;

        Format df = DateFormat.getDateFormat(this.context);
        Format tf = DateFormat.getTimeFormat(this.context);

        Date d = new Date();
        CharSequence s = df.format(d.getTime());
        long currentdate = d.getTime();
        while (!mCursor.isLast()) {
            mCursor.moveToNext();
            Log.d("Calendar", df.format(start).toString());
            try {
                title = mCursor.getString(0);
                start = mCursor.getLong(1);


            } catch (Exception e) {
//ignore
            }

            if (df.format(start).toString().equals(s.toString())) {
                return ((title + "가 " + tf.format(start) + "에 있습니다.").toString());
            }
        }
        return (null);
    }

    public String get_last_schedule() {//null값이 리턴되면 그날에 일정이 없음. string은 그날의 첫 일정

        mCursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI, COLS, null, null, null);
        mCursor.moveToFirst();


        String title = "N/A";


        Long start = 0L;

        Format df = DateFormat.getDateFormat(this.context);
        Format tf = DateFormat.getTimeFormat(this.context);

        Date d = new Date();
        CharSequence s = df.format(d.getTime());
        long currentdate = d.getTime();
        long last_time=0;
        String r_title, r_time;
        while (!mCursor.isLast()) {
            mCursor.moveToNext();
            Log.d("Calendar", df.format(start).toString());
            try {
                title = mCursor.getString(0);
                start = mCursor.getLong(1);


            } catch (Exception e) {
//ignore
            }

            if (df.format(start).toString().equals(s.toString()) && last_time < start)
            {
                r_title=title;
                r_time=tf.format(start);
                last_time=start;
            }
        }
        if(last_time!=0) {
            return (("마지막 일정 "+title + "가 " + tf.format(start) + "에 있습니다.").toString());
        }
        else {
            return (null);
        }
    }
}