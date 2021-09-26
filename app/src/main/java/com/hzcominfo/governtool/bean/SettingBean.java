package com.hzcominfo.governtool.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Create by Ljw on 2020/12/7 11:00
 */
@Entity
public class SettingBean {

    @Id(autoincrement = true)
    private Long id;

    private Boolean isAutoPhoto;

    private String photoInterval;

    private boolean isWalk;

    @Generated(hash = 127617642)
    public SettingBean(Long id, Boolean isAutoPhoto, String photoInterval,
            boolean isWalk) {
        this.id = id;
        this.isAutoPhoto = isAutoPhoto;
        this.photoInterval = photoInterval;
        this.isWalk = isWalk;
    }

    @Generated(hash = 1969935259)
    public SettingBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsAutoPhoto() {
        return this.isAutoPhoto;
    }

    public void setIsAutoPhoto(Boolean isAutoPhoto) {
        this.isAutoPhoto = isAutoPhoto;
    }

    public String getPhotoInterval() {
        return this.photoInterval;
    }

    public void setPhotoInterval(String photoInterval) {
        this.photoInterval = photoInterval;
    }

    public boolean getIsWalk() {
        return this.isWalk;
    }

    public void setIsWalk(boolean isWalk) {
        this.isWalk = isWalk;
    }
}
