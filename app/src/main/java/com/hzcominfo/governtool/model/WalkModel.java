package com.hzcominfo.governtool.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

/**
 * Create by Ljw on 2020/11/18 15:36
 */
public class WalkModel extends BaseObservable {

    public ObservableField<String> walkTime = new ObservableField<>();
    public ObservableField<Double> walkDistance= new ObservableField<>();
    public ObservableField<String> walkName = new ObservableField<>();
    public ObservableField<String> recordTime = new ObservableField<>();
}
