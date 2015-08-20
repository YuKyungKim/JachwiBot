package com.soma.albert.jachwibot;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by whee6409 on 15. 8. 20.
 */
public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ALARM_LIST(" +
                "alarm_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "alarm_type INTEGER," +
                "date TEXT," +
                "day TEXT," +
                "hour INTEGER, " +
                "minute INTEGER, " +
                "isRepeat INTEGER, " +
                "memo TEXT)" +
                ";");
        db.execSQL("CREATE TABLE CALL_LIST(" +
                "call_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "call_name TEXT," +
                "call_number TEXT," +
                "period INTEGER," +
                "recent_call TEXT" +
                ");");
        db.execSQL("CREATE TABLE HOUSEWORK_LIST(" +
                "housework_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "housework_type INTEGER," +
                "last_day TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public String PrintAlarmData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from ALARM_LIST", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : alarm_name "
                    + cursor.getString(1)
                    + ", date = "
                    + cursor.getString(2)
                    + ", day = "
                    + cursor.getString(3)
                    + ", hour = "
                    + cursor.getInt(4)
                    + ", minute = "
                    + cursor.getInt(5)
                    + ", isRepeat = "
                    + cursor.getInt(6)
                    + ", memo = "
                    + cursor.getString(7)
                    + "\n";
        }

        return str;
    }

    public String PrintCallData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from CALL_LIST", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : call_name "
                    + cursor.getString(1)
                    + ", call_number = "
                    + cursor.getString(2)
                    + ", period = "
                    + cursor.getInt(3)
                    + ", recent_call = "
                    + cursor.getString(4)
                    + "\n";
        }

        return str;
    }

    public String PrintHouseworkData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from HOUSEWORK_LIST", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : housework_type "
                    + cursor.getInt(1)
                    + ", last_day = "
                    + cursor.getString(2)
                    + "\n";
        }

        return str;
    }

}
