package com.hbzhou.open.flowcamera;

import java.lang.System;

/**
 * author hbzhou
 * date 2019/12/13 11:13
 */
@kotlin.Metadata(mv = {1, 1, 15}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0003H&J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\b\u001a\u00020\u0003H&J\u0010\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH&J\b\u0010\f\u001a\u00020\u0003H&\u00a8\u0006\r"}, d2 = {"Lcom/hbzhou/open/flowcamera/CaptureListener;", "", "recordEnd", "", "time", "", "recordError", "recordShort", "recordStart", "recordZoom", "zoom", "", "takePictures", "flowcamera_debug"})
public abstract interface CaptureListener {
    
    public abstract void takePictures();
    
    public abstract void recordShort(long time);
    
    public abstract void recordStart();
    
    public abstract void recordEnd(long time);
    
    public abstract void recordZoom(float zoom);
    
    public abstract void recordError();
}