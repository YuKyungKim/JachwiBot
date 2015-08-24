package com.soma.albert.jachwibot;

/**
 * Created by whee6409 on 15. 8. 13.
 */
public class AlarmComponent {
    private int alarmtype;
    private String alarmname;
    private boolean isRepeat;
    private String week;
    private int hour;
    private int min;

    public AlarmComponent() {
        this.alarmtype = -1;
        this.alarmname = null;
        this.isRepeat = true;
        this.week = null;
        this.hour = 0;
        this.min = 0;
    }

    public AlarmComponent(int alarmtype, String alarmname, String isRepeat, String week, int hour, int min) {
        this.alarmtype = alarmtype;
        this.alarmname = alarmname;
        this.isRepeat = Boolean.valueOf(isRepeat);
        this.week = week;
        this.hour = hour;
        this.min = min;
    }

    public int getAlarmtype() {
        return alarmtype;
    }
    public void setAlarmtype(int alarmtype) {
        this.alarmtype = alarmtype;
    }

    public String getAlarmname() {
        return alarmname;
    }
    public void setAlarmname(String alarmname) {
        this.alarmname = alarmname;
    }

    public boolean getisRepeat() {
        return isRepeat;
    }
    public void setisRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public String getWeek() {
        return week;
    }
    public void setWeek(String week) {
        this.week = week;
    }

    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }
    public void setMin(int min) {
        this.min = min;
    }
}
