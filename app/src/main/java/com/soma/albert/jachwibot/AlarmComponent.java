package com.soma.albert.jachwibot;

/**
 * Created by whee6409 on 15. 8. 13.
 */
public class AlarmComponent {
    private int alarmId;
    private String alarmText;
    private String date;
    private String day;
    private int hour;
    private int min;
    private int isRepeat;
    private String text;

    public AlarmComponent() {
        this.alarmId = 0;
        this.alarmText = null;
        this.date = null;
        this.day = null;
        this.hour = 0;
        this.min = 0;
        this.isRepeat = 0;
        this.text = null;
    }

    public AlarmComponent(int alarmId, String alarmText, String date, String day, int hour, int min, int isRepeat, String text) {
        this.alarmId = alarmId;
        this.alarmText = alarmText;
        this.date = date;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.isRepeat = isRepeat;
        this.text = text;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmText() {
        return alarmText;
    }

    public void setAlarmText(String alarmText) {
        this.alarmText = alarmText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

    public int isRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(int isRepeat) {
        this.isRepeat = isRepeat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
