package com.exercise.yxty.safeguard.beans;

/**
 * Created by Administrator on 2016/2/26.
 */
public class VirusInfo {

    String fileMd5;
    String name;
    String desc;

    public VirusInfo() {
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "VirusInfo{" +
                "fileMd5='" + fileMd5 + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
