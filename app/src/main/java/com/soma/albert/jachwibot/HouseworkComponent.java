package com.soma.albert.jachwibot;

/**
 * Created by whee6409 on 15. 8. 14.
 */
public class HouseworkComponent {
    private int houseworkId;
    private String houseworkText;

    public HouseworkComponent() {
        this.houseworkId = 0;
        this.houseworkText = null;
    }

    public HouseworkComponent(int houseworkId, String houseworkText) {
        this.houseworkId = houseworkId;
        this.houseworkText = houseworkText;
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
}
