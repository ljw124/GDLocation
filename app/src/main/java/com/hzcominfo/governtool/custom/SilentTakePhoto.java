package com.hzcominfo.governtool.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.hzcominfo.governtool.utils.CommonUtils.getTimeRandomStr;

/**
 * Create by Ljw on 2020/12/3 14:34
 * 定时静默拍照
 */
public class SilentTakePhoto {
    private FrameLayout cameraFrame;
    private Camera mCamera;
    private static final int TAKE_PHOTO = 0;
    private Context context;
    private static SilentTakePhoto mInstance;
    private int intervalTime = 5000; //拍照间隔
    private String photoPath; //照片路径
    private ITakePhotoResults photoResults;

    public void setITakePhotoResults(ITakePhotoResults photoResults){
        this.photoResults = photoResults;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case TAKE_PHOTO:
                    takePhoto();
                    if (handler != null)
                        handler.sendEmptyMessageDelayed(TAKE_PHOTO, intervalTime);
                    break;
            }
        }
    };

    public static SilentTakePhoto getInstance(Context context, FrameLayout cameraFrame, int intervalTime){
        if (mInstance == null) {
            synchronized (SilentTakePhoto.class){
                if (mInstance == null){
                    mInstance = new SilentTakePhoto(context, cameraFrame, intervalTime);
                }
            }
        }
        return mInstance;
    }

    private SilentTakePhoto(Context context, FrameLayout cameraFrame, int intervalTime){
        this.context = context;
        this.cameraFrame = cameraFrame;
        this.intervalTime = intervalTime * 1000;
    }

    public void startTakePhoto(){
        initCamera();
        if (handler != null)
            handler.sendEmptyMessageDelayed(TAKE_PHOTO, intervalTime);
    }

    public void stopTakePhoto(){
        if (handler != null){
            handler.removeMessages(TAKE_PHOTO);
        }
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    private void initCamera(){
        //初始化相机并打开摄像头
        int numberOfCameras = Camera.getNumberOfCameras();// 获取摄像头个数
        //遍历摄像头信息
        for (int cameraId = 0; cameraId < numberOfCameras; cameraId++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) { //后置摄像头-CAMERA_FACING_BACK；前置摄像头-CAMERA_FACING_FRONT
                mCamera = Camera.open(cameraId);//打开摄像头
            }
        }
        //预览照片
        CameraPreview mPreview = new CameraPreview(context, mCamera);
        cameraFrame.addView(mPreview);
    }

    private void takePhoto(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //得到照相机的参数
                Camera.Parameters parameters = mCamera.getParameters();
                //图片的格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                //预览的大小是多少
                parameters.setPreviewSize(800, 400);
                //设置对焦模式，自动对焦
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                //开始预览
                mCamera.startPreview();
                //对焦成功后，自动拍照
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            //获取照片
                            mCamera.takePicture(null, null, mPictureCallback);
                        }
                    }
                });
            }
        }).start();
    }

    //获取照片中的接口回调
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //拍照后的图片旋转90重新保存
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.setRotate(90, bitmap.getWidth()/2, bitmap.getHeight()/2);
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapData = bos.toByteArray();
            //文件夹路径
            String mFilePath = context.getExternalMediaDirs()[0].getAbsolutePath() + File.separator + "/photo";
            File dir = new File(mFilePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            //文件名
            String fileName = getTimeRandomStr() + ".jpeg";
            File file = new File(dir, fileName);
            photoPath = file.getAbsolutePath();
            Log.e("photoPath", photoPath);
            //图片全路径
            File tempFile = new File(photoPath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tempFile);
                fos.write(bitmapData);
                photoResults.photoPath(photoPath);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //实现连续拍多张的效果
                mCamera.startPreview();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public interface ITakePhotoResults{
        void photoPath(String path);
    }
}
