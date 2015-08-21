package com.soma.albert.jachwibot;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.format.DateFormat;

import java.text.Format;
import java.util.Date;

/**
 * Created by josh on 15. 8. 21..
 */
public class Calendar {

    private Cursor mCursor = null;
    private Context context;
    private static final String[] COLS = new String[]
            {CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};

    public Calendar(Context current){
        context=current;
    }
    public String get_current_schedule() {//null값이 리턴되면 그날에 일정이 없음. string은 그날의 첫 일정


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

            try {
                title = mCursor.getString(0);


                start = mCursor.getLong(1);


            } catch (Exception e) {
//ignore
            }

            if (df.format(start) == s) {
                return ((title + " on " + df.format(start) + " at " + tf.format(start)).toString());
            }
            if (currentdate < start) {
                break;
            }
            mCursor.moveToNext();
        }
        return (null);
    }

}