package com.hzcominfo.governtool.utils;

import androidx.annotation.NonNull;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import java.util.List;

/**
 * Create by Ljw on 2020/12/4 11:20
 */
public class PermissionUtil {

    private static PermissionUtil instance;
    private IPermission iPermission;

    public void getIPermission(IPermission iPermission){
        this.iPermission = iPermission;
    }

    public static PermissionUtil getInstance(){
        if (instance == null){
            synchronized (PermissionUtil.class){
                if (instance == null){
                    instance = new PermissionUtil();
                }
            }
        }
        return instance;
    }

    private PermissionUtil(){
        PermissionUtils.permission(
                PermissionConstants.STORAGE,
                PermissionConstants.CAMERA,
                PermissionConstants.MICROPHONE,
//                PermissionConstants.PHONE,
                PermissionConstants.LOCATION
        ).callback(new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(@NonNull List<String> granted) {
                instance = null;
                iPermission.isPermission(true);
            }

            @Override
            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                instance = null;
                iPermission.isPermission(false);
            }
        }).request();
    }

    public interface IPermission{
        void isPermission(boolean isPer);
    }
}
