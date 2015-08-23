package com.soma.albert.jachwibot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

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
                "alarm_type INTEGER PRIMARY KEY," +
                "alarm_name TEXT," +
                "isRepeat TEXT, " +
                "week TEXT," +
                "hour INTEGER, " +
                "minute INTEGER " +
                ");");
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

    public void insert(String table_name, ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(table_name, null, contentValues);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String table_name, ContentValues contentValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(table_name, contentValues, whereClause + "=?", whereArgs);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String table_name, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table_name, whereClause + "=?", whereArgs);
        db.close();
    }

    public AlarmComponent selectAlarmDataById(int alarmId) {
        AlarmComponent alarmComponent = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from ALARM_LIST WHERE alarm_type = '" + alarmId + "';", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            alarmComponent = new AlarmComponent(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
        }
        cursor.close();
        return alarmComponent;
    }

    public ArrayList<AlarmComponent> selectAllAlarmData() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<AlarmComponent> alarmCompList = new ArrayList<AlarmComponent>();

        Cursor cursor = db.rawQuery("select * from ALARM_LIST;", null);
        while (cursor.moveToNext()) {
            AlarmComponent alarmComponent = new AlarmComponent(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
            alarmCompList.add(alarmComponent);
        }
        cursor.close();
        return alarmCompList;
    }

    public Call selectCallDataById(int callId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from CALL_LIST WHERE call_id = '" + callId + "';", null);
        Call call = null;
        call = new Call(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getInt(3), cursor.getString(4));
        return call;
    }

    public ArrayList<Call> selectAllCallData() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Call> callList = null;

        Cursor cursor = db.rawQuery("select * from CALL_LIST;", null);
        while (cursor.moveToNext()) {
            Call call = null;
            call = new Call(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getString(4));
            callList.add(call);
        }

        return callList;
    }

    public HouseworkComponent selectHouseworkById(int houseworkId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from HOUSEWORK_LIST WHERE housework_id = '" + houseworkId + "';", null);
        HouseworkComponent houseworkComponent = null;
        houseworkComponent = new HouseworkComponent(cursor.getInt(0), cursor.getInt(1), cursor.getString(2));
        return houseworkComponent;
    }

    public ArrayList<HouseworkComponent> selectAllHoseworkData() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HouseworkComponent> houseworkList = new ArrayList<HouseworkComponent>();

        Cursor cursor = db.rawQuery("select * from HOUSEWORK_LIST;", null);
        while (cursor.moveToNext()) {
            HouseworkComponent houseworkComponent = null;
            houseworkComponent = new HouseworkComponent(cursor.getInt(0), cursor.getInt(1), cursor.getString(2));
            Log.i("add", houseworkComponent.toString());
            houseworkList.add(houseworkComponent);
        }

        return houseworkList;
    }
}
