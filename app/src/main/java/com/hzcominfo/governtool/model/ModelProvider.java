package com.hzcominfo.governtool.model;

import androidx.databinding.BaseObservable;

/**
 * Create by Ljw on 2020/11/18 14:16
 */
public class ModelProvider {

    private static ModelProvider instance;
    public static ModelProvider getInstance() {
        return instance == null ? instance = new ModelProvider() : instance;
    }

    private WalkModel walkModel = new WalkModel();
    private WalkInfoModel walkInfoModel = new WalkInfoModel();

    public <T extends BaseObservable> T get(Class<T> modelClass) {
        if (modelClass.getName().equals(WalkModel.class.getName()))
            return (T) walkModel;
        if (modelClass.getName().equals(WalkInfoModel.class.getName()))
            return (T) walkInfoModel;
        return null;
    }
}
