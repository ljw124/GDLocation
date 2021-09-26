package com.hzcominfo.governtool.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;
import java.util.List;

/**
 * Create by Ljw on 2020/11/17 16:33
 */
@Entity
public class MarkerBean implements Serializable {

    @Id(autoincrement = true)
    private Long id;

    static final long serialVersionUID = 43L;

    String latLngs; //经纬度
    String loacationInfo; //位置信息
    @Convert(columnType = String.class, converter = StringConverter.class)
    List<String> photos; //照片视频
    String describe; //描述信息
    @Convert(columnType = String.class, converter = StringConverter.class)
    List<String> voices; //语音
    @Convert(columnType = String.class, converter = StringConverter.class)
    List<String> voiceTime; //语音时长
    String recordTime; //标记时间
    String markerName; //标记名称
    Boolean isNew; //新增后是否没有预览过
    Long markerId; //外键，用于和PolylineBean建立对应关系
    @Generated(hash = 769332688)
    public MarkerBean(Long id, String latLngs, String loacationInfo,
            List<String> photos, String describe, List<String> voices,
            List<String> voiceTime, String recordTime, String markerName,
            Boolean isNew, Long markerId) {
        this.id = id;
        this.latLngs = latLngs;
        this.loacationInfo = loacationInfo;
        this.photos = photos;
        this.describe = describe;
        this.voices = voices;
        this.voiceTime = voiceTime;
        this.recordTime = recordTime;
        this.markerName = markerName;
        this.isNew = isNew;
        this.markerId = markerId;
    }
    @Generated(hash = 1490708183)
    public MarkerBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getLatLngs() {
        return this.latLngs;
    }
    public void setLatLngs(String latLngs) {
        this.latLngs = latLngs;
    }
    public String getLoacationInfo() {
        return this.loacationInfo;
    }
    public void setLoacationInfo(String loacationInfo) {
        this.loacationInfo = loacationInfo;
    }
    public List<String> getPhotos() {
        return this.photos;
    }
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
    public String getDescribe() {
        return this.describe;
    }
    public void setDescribe(String describe) {
        this.describe = describe;
    }
    public List<String> getVoices() {
        return this.voices;
    }
    public void setVoices(List<String> voices) {
        this.voices = voices;
    }
    public List<String> getVoiceTime() {
        return this.voiceTime;
    }
    public void setVoiceTime(List<String> voiceTime) {
        this.voiceTime = voiceTime;
    }
    public String getRecordTime() {
        return this.recordTime;
    }
    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }
    public String getMarkerName() {
        return this.markerName;
    }
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
    public Boolean getIsNew() {
        return this.isNew;
    }
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }
    public Long getMarkerId() {
        return this.markerId;
    }
    public void setMarkerId(Long markerId) {
        this.markerId = markerId;
    }
}
