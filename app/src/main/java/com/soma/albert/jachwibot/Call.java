package com.soma.albert.jachwibot;

/**
 * Created by whee6409 on 15. 8. 21.
 */
public class Call {
    private int callId;
    private String callName;
    private String number;
    private int period; // 일단위 주기
    private String recentCall;

    public Call() {
        this.period = period;
        this.callId = callId;
        this.callName = callName;
        this.number = number;
        this.recentCall = recentCall;
    }

    public Call(int callId, String callName, String number, int period, String recentCall) {
        this.callId = callId;
        this.callName = callName;
        this.number = number;
        this.period = period;
        this.recentCall = recentCall;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getRecentCall() {
        return recentCall;
    }

    public void setRecentCall(String recentCall) {
        this.recentCall = recentCall;
    }
}
