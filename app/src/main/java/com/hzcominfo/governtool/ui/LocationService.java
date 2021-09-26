package com.hzcominfo.governtool.ui;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceStatusListener;
import com.hzcominfo.governtool.utils.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements AMapLocationListener, TraceStatusListener {

    private final IBinder mBinder = new LocationServiceBinder();
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private double latitude; //定位纬度
    private double longitude; //定位经度
    //用于轨迹纠偏
    private LBSTraceClient mTraceClient; //轨迹纠偏
    private ArrayList<String> traceLatLngs = new ArrayList<>(); //轨迹纠偏后的经纬度，每2s定位一次，每隔5个点合并请求一次纠偏并回调
    private List<LatLng> traceLatLng = new ArrayList<>();
    private ArrayList<String> lastTraceLatLngs = new ArrayList<>(); //上次轨迹纠偏后的经纬度
    //用于统计activity离线后的轨迹
    private ArrayList<String> latLngs = new ArrayList<>(); //当activity退出后，要把定位的经纬度保存起来
    private boolean isActivityLeave = false; //activity是否退出了
    private static final int DISTANCE_ERROR = 20; //异常距离，如果超过这个距离，则说明移动距离异常,避免定位抖动造成的误差
    private LatLng currentLatLng; //当前定位经纬度
    private LatLng lastLatLng; //上次定位经纬度
    private double totalDistance = 0; //总距离
    private TimerTask timerTask;
    private Timer timer;
    private int mCountTime = 0; //离线记录时间

    //轨迹纠偏后的回调
    private ITraceLocation iTraceLocation;
    public void getTraceLocation(ITraceLocation iTraceLocation){
        this.iTraceLocation = iTraceLocation;
    }

    public class LocationServiceBinder extends Binder{
        LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLocation();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LocationService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void initLocation() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置返回地址信息，默认为true
        mLocationOption.setNeedAddress(true);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //设置定位监听
        mlocationClient.setLocationListener(this);

        //初始化轨迹纠偏API
        mTraceClient = LBSTraceClient.getInstance(this);
    }

    /**
     * 定位结果回调
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) { //定位成功回调信息，设置相关消息
                latitude = aMapLocation.getLatitude();//获取纬度
                longitude = aMapLocation.getLongitude();//获取经度
                if (isActivityLeave){
                    String latLng = latitude + "," + longitude;
                    if (latLngs.contains(latLng)) return;
                    latLngs.add(latLng);

                    //首次定位时设置当前经纬度
//                    if (currentLatLng == null) {
//                        currentLatLng = new LatLng(latitude, longitude);
//                    }
//                    lastLatLng = currentLatLng;
//                    currentLatLng = new LatLng(latitude, longitude);
//                    float movedDistance = AMapUtils.calculateLineDistance(currentLatLng, lastLatLng);
//                    if (movedDistance > DISTANCE_ERROR || movedDistance == 0) {
//                        return;
//                    }
//                    //更新距离
//                    totalDistance += movedDistance/1000;
//                    DecimalFormat df = new DecimalFormat("#.000");
//                    totalDistance = Double.valueOf(df.format(totalDistance));
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                ToastUtils.showCenterToast("location Error:" + aMapLocation.getErrorInfo());
            }
        }
    }

    /**
     * 轨迹纠偏结果回调
     */
    @Override
    public void onTraceStatus(List<TraceLocation> locations, List<LatLng> rectifications, String errorInfo) {
        try {
            if (LBSTraceClient.TRACE_SUCCESS.equals(errorInfo)) {
                if (rectifications.size()>0){
                    traceLatLng.clear();
                    for (int i = rectifications.size()-5; i <= rectifications.size()-1; i++){
                        if (!traceLatLng.contains(rectifications.get(i))){
                            traceLatLng.add(rectifications.get(i));
                        }
                    }
                    //结果回调
                    iTraceLocation.getTranceLocation(traceLatLng);
                }
            } else {
                ToastUtils.showCenterToast("轨迹纠偏失败: " + errorInfo);
            }
        } catch (Throwable e) {
            ToastUtils.showCenterToast("轨迹纠偏出错：" + e.getMessage());
        }
    }

    private void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mCountTime ++;
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    private void stopTimer(){
        if (null != timerTask && !timerTask.cancel()) {
            timerTask.cancel();
            timer.cancel();
        }
        ToastUtils.showCenterToast("离线时长：" + mCountTime + ";离线距离：" + totalDistance);
    }

    /****************************** 对外开放获取结果信息的方法 *********************************/
    /**
     * 启动定位
     */
    public void startLocation(boolean isWalk){
        mlocationClient.startLocation();
        if (!isWalk){ //驾车纠偏
            mTraceClient.startTrace(this); //开始采集并纠偏，需要传入一个状态回调监听
        }
    }

    /**
     * 停止定位
     */
    public void stopLocation(){
        if (mlocationClient != null){
            mlocationClient.stopLocation();
        }
        mTraceClient.stopTrace();//在不需要轨迹纠偏时（如行程结束），可调用此接口结束纠偏
    }

    /**
     * 如果activity后台运行，则把此期间定位的经纬度保存到集合中
     * @param isLeave
     */
    public void setActivityLeave(boolean isLeave){
        if (isLeave){
            latLngs.clear();
            mCountTime = 0;
            totalDistance = 0;
            currentLatLng = null;
            lastLatLng = null;
//            startTimer();
        } else {
//            stopTimer();
        }
        isActivityLeave = isLeave;
    }

    //获取纬度
    public double getLatitude(){
        return latitude;
    }

    //获取经度
    public double getLongitude(){
        return longitude;
    }

    //获取activity退到后台时保存的经纬度
    public ArrayList<String> getLatLngs(){
        return latLngs;
    }

    //获取纠偏后的经纬度
    public ArrayList<String> getTraceLatLngs(){
        return traceLatLngs;
    }

    //每次获取到纠偏数据之后要清除掉当前数据
    public void clearTraceLtLngs(){
        lastTraceLatLngs = traceLatLngs;
        traceLatLngs.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null){
            mlocationClient.stopLocation();
        }
        mTraceClient.stopTrace();
    }

    public interface ITraceLocation{
        void getTranceLocation(List<LatLng> rectifications);
    }
}
