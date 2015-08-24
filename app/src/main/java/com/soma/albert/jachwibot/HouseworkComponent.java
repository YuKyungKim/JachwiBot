package com.soma.albert.jachwibot;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by whee6409 on 15. 8. 14.
 */
public class HouseworkComponent {
    private int houseworkId;
    private int houseworkType;
    private String lastDay;

    public HouseworkComponent() {
        this.houseworkId = 0;
        this.houseworkType = 0;
        this.lastDay = null;
    }

    public HouseworkComponent(int houseworkId, int houseworkText, String lastDay) {
        this.houseworkId = houseworkId;
        this.houseworkType = houseworkText;
        this.lastDay = lastDay;
    }

    public int getHouseworkId() {
        return houseworkId;
    }

    public void setHouseworkId(int houseworkId) {
        this.houseworkId = houseworkId;
    }

    public int getHouseworkType() {
        return houseworkType;
    }

    public void setHouseworkType(int houseworkType) {
        this.houseworkType = houseworkType;
    }

    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }

    @Override
    public String toString() {
        return "HouseworkComponent{" +
                "houseworkId=" + houseworkId +
                ", houseworkType=" + houseworkType +
                ", lastDay='" + lastDay + '\'' +
                '}';
    }

    public static String CalcDay(String lastDate) {
        Calendar todayCal = Calendar.getInstance();
        Calendar lastdayCal = Calendar.getInstance();

        StringTokenizer strTk = new StringTokenizer(lastDate, ".");
        int lastYear = Integer.parseInt(strTk.nextToken());
        int lastMonth = Integer.parseInt(strTk.nextToken());
        int lastDay = Integer.parseInt(strTk.nextToken());

        lastdayCal.set(lastYear, lastMonth - 1, lastDay);

        long delta = todayCal.getTimeInMillis() - lastdayCal.getTimeInMillis();

        lastdayCal.setTimeInMillis(delta);

        int date = lastdayCal.get(Calendar.DAY_OF_YEAR) + 1;

        long gap = (delta / 86400000);

        if(gap == 0) {
            return "오늘";
        } else if(gap == 1) {
            return "어제";
        } else {
            return "" + gap + "일 전";
        }

    }
}
