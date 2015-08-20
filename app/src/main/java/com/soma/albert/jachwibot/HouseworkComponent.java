package com.soma.albert.jachwibot;

/**
 * Created by whee6409 on 15. 8. 14.
 */
public class HouseworkComponent {
    private int houseworkId;
    private String houseworkText;
    private String lastDay;

    public HouseworkComponent() {
        this.houseworkId = 0;
        this.houseworkText = null;
        this.lastDay = null;
    }

    public HouseworkComponent(int houseworkId, String houseworkText, String lastDay) {
        this.houseworkId = houseworkId;
        this.houseworkText = houseworkText;
        this.lastDay = lastDay;
    }

    public int getHouseworkId() {
        return houseworkId;
    }

    public void setHouseworkId(int houseworkId) {
        this.houseworkId = houseworkId;
    }

    public String getHouseworkText() {
        return houseworkText;
    }

    public void setHouseworkText(String houseworkText) {
        this.houseworkText = houseworkText;
    }

    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }
}
