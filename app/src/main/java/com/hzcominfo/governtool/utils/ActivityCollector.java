package com.hzcominfo.governtool.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljw on 2020/11/19.
 */
public class ActivityCollector {

    private static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
