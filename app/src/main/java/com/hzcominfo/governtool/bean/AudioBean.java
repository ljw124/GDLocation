package com.hzcominfo.governtool.bean;

import java.io.Serializable;

/**
 * Create by Ljw on 2020/11/26 15:25
 */
public class AudioBean implements Serializable {

    private int time;
    private String filePath;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
