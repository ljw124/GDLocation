package com.hzcominfo.governtool.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.utils.ActivityCollector;
import com.hzcominfo.governtool.utils.ToastUtils;
import java.util.List;
import static com.hzcominfo.governtool.utils.CommonUtils.getVersionName;

public class WelcomeActivity extends AppCompatActivity {

    private TimeCount timeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_welcome);

        TextView tvVersion = findViewById(R.id.tv_version);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvVersion.setText("V-" + getVersionName(WelcomeActivity.this));
        tvTitle.setText(getResources().getString(R.string.app_name));

//        timeCount = new TimeCount(1000, 1000);
//        timeCount.start();
        initPermission();
    }

    /**
     * 动态权限申请
     */
    private void initPermission() {
        PermissionUtils.permission(
                PermissionConstants.STORAGE,
                PermissionConstants.CAMERA,
                PermissionConstants.MICROPHONE,
//                PermissionConstants.PHONE,
                PermissionConstants.LOCATION
        ).callback(new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(@NonNull List<String> granted) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                ToastUtils.showToast("请开启必要的权限");
                PermissionUtils.launchAppDetailsSettings();
            }
        }).request();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        try{
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //计时器，执行定时任务
    public class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {  // 计时过程
        }

        @Override
        public void onFinish() {// 计时完毕
            timeCount.cancel();
            goHome();
        }

        private void goHome() {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
