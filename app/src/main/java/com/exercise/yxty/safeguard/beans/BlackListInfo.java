package com.exercise.yxty.safeguard.beans;

/**
 * Created by Administrator on 2016/1/31.
 */
public class BlackListInfo {

    private String number;
    private String mode;

    public BlackListInfo(String number, String mode) {
        this.number = number;
        this.mode = mode;
    }

    public BlackListInfo() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return number+" : "+mode ;
    }
}
