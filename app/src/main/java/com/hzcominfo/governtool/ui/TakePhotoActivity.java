package com.hzcominfo.governtool.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.hbzhou.open.flowcamera.FlowCameraView;
import com.hbzhou.open.flowcamera.listener.ClickListener;
import com.hbzhou.open.flowcamera.listener.FlowCameraListener;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.databinding.ActivityTakePhotoBinding;
import com.hzcominfo.governtool.utils.ToastUtils;
import java.io.File;

import static com.hbzhou.open.flowcamera.FlowCameraView.BUTTON_STATE_BOTH;

/**
 * https://github.com/xionger0520/flowcamera
 * https://github.com/natario1/CameraView
 */
public class TakePhotoActivity extends AppCompatActivity {

    private ActivityTakePhotoBinding binding;
    private FlowCameraView flowCamera;
    private String picturePath;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_take_photo);
        className = getIntent().getStringExtra("class");

        flowCamera = binding.flowCamera;
        // 绑定生命周期 不用关心Camera的开启和关闭了 不绑定无法预览
        flowCamera.setBindToLifecycle(this);
        // 设置只支持单独拍照拍视频还是都支持
        // BUTTON_STATE_ONLY_CAPTURE  BUTTON_STATE_ONLY_RECORDER  BUTTON_STATE_BOTH
        flowCamera.setCaptureMode(BUTTON_STATE_BOTH);
        // 设置最大可拍摄小视频时长 S
        flowCamera.setRecordVideoMaxTime(60);
        flowCamera.setFlowCameraListener(new FlowCameraListener() {
            @Override
            public void captureSuccess(@NonNull File file) {// 拍照返回
                picturePath = file.getAbsolutePath();
                goMarkerEditPage(1);
            }

            @Override
            public void recordSuccess(@NonNull File file) {// 录制完成视频文件返回
                picturePath = file.getAbsolutePath();
                goMarkerEditPage(2);
                finish();
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) { // 操作拍照或录视频出错
                ToastUtils.showCenterToast(cause.toString());
            }
        });

        //左边按钮点击事件
        flowCamera.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    public void goMarkerEditPage(int type){
        Intent intent = null;
        switch (className){
            case "MainActivity":
                intent = new Intent(TakePhotoActivity.this, MainActivity.class);
                break;
            case "MarkerEditActivity":
                intent = new Intent(TakePhotoActivity.this, MarkerEditActivity.class);
                break;
            case "WalkLineActivity":
                intent = new Intent(TakePhotoActivity.this, WalkLineActivity.class);
                break;
        }
        intent.putExtra("picturePath", picturePath);
        intent.putExtra("type", type);
        startActivity(intent);
        finish();
    }
}
