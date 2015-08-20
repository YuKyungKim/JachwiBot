package com.soma.albert.jachwibot;

/**
 * Created by whee6409 on 15. 8. 13.
 */
public class AlarmComponent {
    private int alarmId;
    private String alarmText;

    public AlarmComponent() {
        this.alarmId = 0;
        this.alarmText = null;
    }

    public AlarmComponent(int alarmId, String alarmText) {
        this.alarmId = alarmId;
        this.alarmText = alarmText;
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
}
