package com.soma.albert.jachwibot;

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
}
