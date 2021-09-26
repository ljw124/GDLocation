package com.hzcominfo.governtool.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.blankj.utilcode.util.LogUtils;
import java.util.Iterator;
import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Create by Ljw on 2020/12/2 15:19
 * https://azhon.blog.csdn.net/article/details/80629107?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-4.control&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-4.control
 * https://blog.csdn.net/leng_wen_rou/article/details/53100284?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.control&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.control
 */
public class GpsLocationUtil {

    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private LocationManager lm;
    Context context;
    Activity activity;
    private ILocation iLocation;
    private LocationManager locationManager;
    private int gpsSatelliteNum = 0; //有效卫星个数

    public GpsLocationUtil(Context context, Activity activity, ILocation iLocation){
        this.context = context;
        this.activity = activity;
        this.iLocation = iLocation;

        initPermission();
    }

    private void initPermission() {
        String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE};
        //检查定位权限
        //得到系统的位置服务，判断GPS是否激活
        lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)  != PERMISSION_GRANTED) {// 没有权限，申请权限。
                    ActivityCompat.requestPermissions(activity, LOCATIONGPS, BAIDU_READ_PHONE_STATE);
                } else {
                    //有权限，进行定位
                    getLocation();
                }
            } else {
                //有权限，进行相应的处理
                getLocation();
            }
        } else {
            Toast.makeText(context, "系统检测到未开启GPS定位服务,请开启", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取具体位置的经纬度
     */
    @SuppressLint("MissingPermission")
    public void getLocation() {
        // 获取位置管理服务
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(true);//要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        /**这段代码不需要深究，是locationManager.getLastKnownLocation(provider)自动生成的，不加会出错**/
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LogUtils.e("纬度：" + latitude + "\n经度" + longitude);
//            iLocation.location(latitude, longitude);
        } else {
            LogUtils.e("无法获取到位置信息");
        }

        //监听位置变化
        locationManager.requestLocationUpdates(provider, 0, 0, mLocationListener);
        //添加卫星状态改变监听
        locationManager.addGpsStatusListener(gpsStatusListener);
    }

    //位置变化回调
    private LocationListener mLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
//            iLocation.location(location.getLatitude(), location.getLongitude());
        }

        public void onProviderDisabled(String provider) {
            Log.d("-", "Provider now is disabled..");
        }

        public void onProviderEnabled(String provider) {
            Log.d("-", "Provider now is enabled..");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("-", "Provider onStatusChanged");
        }
    };

    /**
     * 对外提供接口
     */
    public interface ILocation{
        void location(int gpsSatelliteNum);
    }

    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    gpsSatelliteNum = 0;
                    //获取当前状态
                    @SuppressLint("MissingPermission") GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //获取所有的卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    //卫星颗数统计
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        count++;
                        GpsSatellite s = iters.next();
                        //卫星的信噪比，一般的认为，在30-40左右是理想，50以下可以接受
                        float snr = s.getSnr();
                        if (snr > 30){
                            gpsSatelliteNum ++;
                        }
                    }
                    iLocation.location(gpsSatelliteNum);
                    break;
                default:
                    break;
            }
        }
    };
}
