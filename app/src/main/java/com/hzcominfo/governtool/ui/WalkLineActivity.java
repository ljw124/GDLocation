package com.hzcominfo.governtool.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.hzcominfo.governtool.MyApplication;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.bean.PolylineBean;
import com.hzcominfo.governtool.bean.SettingBean;
import com.hzcominfo.governtool.custom.SilentTakePhoto;
import com.hzcominfo.governtool.databinding.ActivityWalkLineBinding;
import com.hzcominfo.governtool.db.DaoSession;
import com.hzcominfo.governtool.db.MarkerBeanDao;
import com.hzcominfo.governtool.model.ModelProvider;
import com.hzcominfo.governtool.model.WalkModel;
import com.hzcominfo.governtool.utils.ActivityCollector;
import com.hzcominfo.governtool.utils.GpsLocationUtil;
import com.hzcominfo.governtool.utils.StringUtils;
import com.hzcominfo.governtool.utils.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import org.greenrobot.greendao.query.QueryBuilder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import static com.hzcominfo.governtool.utils.CommonUtils.getCurrentTime;

/**
 * https://blog.csdn.net/weixin_42247440/article/details/81608098
 * https://www.jianshu.com/p/53083f782ea2
 */
public class WalkLineActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, View.OnClickListener {

    private ActivityWalkLineBinding binding;
    private WalkModel walkModel;
    private TextView tvDistance;
    private TextView tvTime;
    private MapView mMapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle; //定位蓝点
    private UiSettings mUiSettings; //定义一个UiSettings对象

    private static final int DISTANCE_ERROR = 20; //步行、骑车异常距离，如果超过这个距离，则说明移动距离异常，避免定位抖动造成的误差
    private static final int DISTANCE_ERROR_DRIVE = 30; //驾车异常距离
    private LatLng currentLatLng; //当前定位经纬度
    private LatLng lastLatLng; //上次定位经纬度
    private double totalDistance = 0; //总距离
    private DaoSession daoSession;
    private PolylineBean polylineBean;
    private List<String> latLngs;
    private boolean isStart = false;
    private TimerTask timerTask;
    private Timer timer;
    private int mCountTime = 0;
    private boolean isFirstLocation = true;
    private static final int TAKE_PICTURE = 0;
    private ArrayList<String> photos; //保存图片、视频
    private int listIndex = 0; //图片索引
    private LocationService service; //定位服务
    private boolean isOne = true; //第一次定位记录位置信息
    private ArrayList<LatLng> currentLatLngs; //本次路程添加的marker
    private LatLng markerLatLng; //拍照、录音时添加marker的经纬度
    private Long markerId; //用于记录marker的外键id
    private SilentTakePhoto silentTakePhoto; //静默拍照
    private Polyline tracedPolyline = null;
    //设置页面数据
    private boolean isAutoPhoto = false;
    private boolean isWalk = true;
    private int photoInterval = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isWalk){ //步行、骑车
                //使用原始经纬度
                manageLocation(service.getLatitude(), service.getLongitude());
            }
            handler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_line);
        binding.setModel(ModelProvider.getInstance().get(WalkModel.class));
        walkModel = binding.getModel();
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        //设置记录marker的外键id
        List<PolylineBean> polylineBeans = daoSession.loadAll(PolylineBean.class);
        if (polylineBeans.size()>0){
            markerId = polylineBeans.get(polylineBeans.size()-1).getId() + 1;
        } else {
            markerId = 1L;
        }
        mMapView = binding.map;
        mMapView.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        tvDistance = binding.tvDistance;
        tvTime = binding.tvTime;
        aMap.showIndoorMap(true); //开启室内地图
        aMap.setOnMyLocationChangeListener(this); //定位结果监听
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); // 设置缩放比例
        setLocationStyle(); //实现定位蓝点
        uiSettings(); // 交互控件设置

        binding.ibBack.setOnClickListener(this);
        binding.ibLocation.setOnClickListener(this);
        binding.ibControl.setOnClickListener(this);
        binding.btnFinish.setOnClickListener(this);
        binding.llMarker.setOnClickListener(this);
        binding.llVoice.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);

        //保存定位点
        polylineBean = new PolylineBean();
        latLngs = new ArrayList<>();
        binding.ibControl.setBackgroundResource(R.mipmap.ic_stop);

        currentLatLngs = new ArrayList<>();
        //显示所有的Marker - 不对，应该只加载本次的marker
//        loadMarker();

        //开启定位服务
        Intent intent = new Intent("LocationService");
        intent.setPackage("com.hzcominfo.governtool");
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        //获取GPS卫星个数
        new GpsLocationUtil(this, WalkLineActivity.this, new GpsLocationUtil.ILocation() {
            @Override
            public void location(int gpsSatelliteNum) {
                if (gpsSatelliteNum < 4){
                    binding.ivGps.setBackgroundResource(R.mipmap.ic_gps_error);
                } else if (gpsSatelliteNum < 8) {
                    binding.ivGps.setBackgroundResource(R.mipmap.ic_gps_warn);
                } else {
                    binding.ivGps.setBackgroundResource(R.mipmap.ic_gps_succ);
                }
            }
        });

        //获取设置页面数据
        QueryBuilder<SettingBean> settingBeans = daoSession.queryBuilder(SettingBean.class);
        List<SettingBean> list = settingBeans.list();
        if (null != list && list.size() > 0){
            SettingBean settingBean = list.get(0);
            if (settingBean.getIsAutoPhoto()){
                isAutoPhoto = true;
                photoInterval = Integer.parseInt(settingBean.getPhotoInterval());
            } else {
                isAutoPhoto = false;
                photoInterval = 0;
            }
            if (settingBean.getIsWalk()){
                isWalk = true;
            } else {
                isWalk = false;
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ToastUtils.showToast("定位服务已连接");
            LocationService.LocationServiceBinder binder = (LocationService.LocationServiceBinder) iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ToastUtils.showToast("定位服务已断开");
        }
    };

    //拍照返回
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadMarker();
//                loadLine();
                initPhoto(intent);
            }
        }).start();
    }

    /**
     * 处理服务返回的定位信息
     * @param latitude
     * @param longitude
     */
    private void manageLocation(double latitude, double longitude){
        //首次定位时设置当前经纬度
        if (currentLatLng == null) {
            currentLatLng = new LatLng(latitude, longitude);
        }
        lastLatLng = currentLatLng;
        currentLatLng = new LatLng(latitude, longitude);
        float movedDistance = AMapUtils.calculateLineDistance(currentLatLng, lastLatLng);
        if (movedDistance > DISTANCE_ERROR || movedDistance == 0) {
            return;
        }
        //绘制移动路线
        aMap.addPolyline(new PolylineOptions().add(lastLatLng, currentLatLng).width(10).color(Color.argb(255, 0, 255, 0)));
        //更新距离
        totalDistance += movedDistance/1000;
        DecimalFormat df = new DecimalFormat("#.000");
        totalDistance = Double.valueOf(df.format(totalDistance));
        tvDistance.setText(totalDistance+"");
        //保存定位的点
        String latLng1 = lastLatLng.latitude + "," + lastLatLng.longitude;
        latLngs.add(latLng1);
        String latLng2 = currentLatLng.latitude + "," + currentLatLng.longitude;
        latLngs.add(latLng2);
        //如果是起点，添加marker
        if (isFirstLocation){
            isFirstLocation = false;
            //在地图上添加起点marker
            addStartMarkers();
        }
    }

    /**
     * 处理服务返回的纠偏后的数据
     * @param rectifications 纠偏后的数据集合
     */
    private void manageTraceLocation(List<LatLng> rectifications){
        //绘制路线
        if(rectifications == null) return;
        if(tracedPolyline == null) {
            tracedPolyline = aMap.addPolyline(new PolylineOptions()
                    .width(10).color(Color.argb(255, 0, 255, 0)));
        }
        tracedPolyline.setPoints(rectifications);
        //经纬度添加到latLngs中
        for (LatLng latLng: rectifications) {
            //首次定位时设置当前经纬度
            if (currentLatLng == null) {
                currentLatLng = new LatLng(latLng.latitude, latLng.longitude);
            }
            lastLatLng = currentLatLng;
            currentLatLng = new LatLng(latLng.latitude, latLng.longitude);
            float movedDistance = AMapUtils.calculateLineDistance(currentLatLng, lastLatLng);
            if (movedDistance > DISTANCE_ERROR_DRIVE || movedDistance == 0) {
                return;
            }
            //更新距离
            totalDistance += movedDistance/1000;
            DecimalFormat df = new DecimalFormat("#.000");
            totalDistance = Double.valueOf(df.format(totalDistance));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvDistance.setText(totalDistance+"");
                }
            });
            //保存定位的点
            String latLng1 = lastLatLng.latitude + "," + lastLatLng.longitude;
            latLngs.add(latLng1);
            String latLng2 = currentLatLng.latitude + "," + currentLatLng.longitude;
            latLngs.add(latLng2);
            //如果是起点，添加marker
            if (isFirstLocation){
                isFirstLocation = false;
                //在地图上添加起点marker
                addStartMarkers();
            }
        }
    }

    /**
     * 拍照返回的数据
     */
    public void initPhoto(Intent intent){
        // 获取传递过来的图片、视频路径
        String picturePath = intent.getStringExtra("picturePath");
        if (StringUtils.isEmpty(picturePath)) return;
        if (StringUtils.isNotEmpty(picturePath)){
            photos.add(listIndex, picturePath);
            listIndex ++;
        }
        String latLng = markerLatLng.latitude + "," + markerLatLng.longitude;
        QueryBuilder<MarkerBean> queryBuilder = daoSession.queryBuilder(MarkerBean.class).where(MarkerBeanDao.Properties.LatLngs.eq(latLng));
        List<MarkerBean> markerBeans = queryBuilder.list();
        if (markerBeans != null && markerBeans.size() > 0){
            MarkerBean markerBean = markerBeans.get(0);
            markerBean.setPhotos(photos);
            daoSession.update(markerBean);
        }
    }

    /**
     * 拍照或相册选择返回后加载路线
     */
    private void loadLine() {
        //如果activity离线，则获取离线后记录的路径
        if (null != service){
            ArrayList<String> serviceLatLngs = service.getLatLngs();
            if (null != serviceLatLngs && serviceLatLngs.size() > 0){
                for (String latLng: serviceLatLngs) {
                    String[] split = latLng.split(",");
                    if (split.length == 2){
                        manageLocation(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
                    }
                }
            }
        }
        //绘制整条路径
        List<LatLng> latLngList = new ArrayList<>();
        for (String latLng: latLngs) {
            String[] split = latLng.split(",");
            if (split.length == 2) {
                latLngList.add(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
            }
            //绘制路线
            aMap.addPolyline(new PolylineOptions().addAll(latLngList).width(10).color(Color.argb(255, 0, 255, 0)));
            //添加起点
            addStartMarkers();
        }
    }

    //实现定位蓝点
    private void setLocationStyle(){
        myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE); //定位一次
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW); //连续定位(此种模式可以让定位箭头和手机保持一致)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER); //连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.strokeWidth(0); //设置定位蓝点精度圆圈的边框宽带
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0)); //设置定位蓝点精度圆圈的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);//设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    // 交互控件设置
    private void uiSettings(){
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false); //缩放按钮是否显示
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM); //设置缩放按钮的位置
        mUiSettings.setCompassEnabled(false); //指南针
        mUiSettings.setMyLocationButtonEnabled(false); //显示默认的定位按钮
        mUiSettings.setScaleControlsEnabled(false);//控制比例尺控件是否显示
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//设置logo位置
    }

    /**
     * 位置变化的监听操作
     *
     * @param location 位置变化后的位置
     */
    @Override
    public void onMyLocationChange(Location location) {
        if(location != null) {
            Bundle bundle = location.getExtras();
            if(bundle != null && bundle.getInt(MyLocationStyle.ERROR_CODE) == 0) {
                //首次定位时设置当前经纬度
                if (isOne){
                    isOne = false;
                    if (currentLatLng == null) {
                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                    lastLatLng = currentLatLng;
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                }
            } else {
//                ToastUtils.showToast("定位失败");
            }
        } else {
//            ToastUtils.showToast("定位失败");
        }
    }

    /**
     * 起使位置标记 Marker
     */
    private void addStartMarkers() {
        if (null == latLngs || latLngs.size() == 0) return;
        //定位到路线的起使位置
        String[] firstPoly = latLngs.get(0).split(",");
        LatLng latlng =  new LatLng(Double.parseDouble(firstPoly[0]), Double.parseDouble(firstPoly[1]));
        MarkerOptions markerOption1 = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_start_marker))
                .position(latlng)
                .draggable(false);
        aMap.addMarker(markerOption1);
    }

    /**
     * 标记 Marker
     * type: 0-新增，1-展示
     */
    private void addMarker(LatLng currentLatLng, int type) {
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_taged))
                .position(currentLatLng)
                .draggable(false);
        aMap.addMarker(markerOption);
        aMap.setOnMarkerClickListener(markerClickListener);

        if (type == 0) {
            currentLatLngs.add(currentLatLng);
            String latLng = currentLatLng.latitude + "," + currentLatLng.longitude;
            QueryBuilder<MarkerBean> queryBuilder = daoSession.queryBuilder(MarkerBean.class).where(MarkerBeanDao.Properties.LatLngs.eq(latLng));
            //保存图片的集合要在此处初始化，防止图片重复添加
            if (null != queryBuilder && queryBuilder.list().size()>0) { //相同的经纬度已存在
                List<String> hasPhotos = queryBuilder.list().get(0).getPhotos();
                if (null != hasPhotos && hasPhotos.size()>0){ //marker对应的有图片
                    List<String> lists = Arrays.asList(hasPhotos).get(0);
                    photos = new ArrayList<>();
                    listIndex = 0;
                    for (int i=0; i<lists.size(); i++) {
                        photos.add(i, lists.get(i));
                        listIndex ++;
                    }
                } else { //marker对应的没有图片
                    photos = new ArrayList<>();
                    listIndex = 0;
                    photos.add(listIndex, "");
                    listIndex ++;
                }
                return;
            }
            //此marker没有添加过，重新添加
            photos = new ArrayList<>();
            listIndex = 0;
            photos.add(listIndex, "");
            listIndex ++;
            //Marker 保存到数据库
            MarkerBean markerBean = new MarkerBean();
            markerBean.setLatLngs(latLng);
            markerBean.setRecordTime(getCurrentTime());
            markerBean.setIsNew(true);
            markerBean.setMarkerId(markerId);
            daoSession.insertOrReplace(markerBean);
        }
    }

    /**
     * 拍照或相册选择返回加载标记的Marker
     * todo 这里有问题：把所有的marker都加载出来了，应该只加载本次的(已修复)
     */
    private void loadMarker() {
        aMap.clear(true);
//        List<MarkerBean> markerBeans = daoSession.loadAll(MarkerBean.class);
//        for (MarkerBean markerBean: markerBeans) {
//            String[] latLngs = markerBean.getLatLngs().split(",");
//            if (latLngs.length == 2) {
//                LatLng latLng = new LatLng(Double.parseDouble(latLngs[0]), Double.parseDouble(latLngs[1]));
//                addMarker(latLng, 1);
//            }
//        }
        for (LatLng latLng: currentLatLngs) {
            addMarker(latLng, 1);
        }
    }

    /**
     * Marker 点击事件
     */
    private AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng position = marker.getPosition();
            Intent intent = new Intent(WalkLineActivity.this, MarkerEditActivity.class);
            intent.putExtra("latitude", position.latitude);
            intent.putExtra("longitude", position.longitude);
            intent.putExtra("class", "WalkLineActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
            return true;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back:
                Intent intent = new Intent(WalkLineActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
                break;
            case R.id.ib_location:
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
                aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); // 设置缩放比例
                break;
            case R.id.ib_control:
                isStart = !isStart;
                if (isStart) { // 开始记录行走路线
                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE); //连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
                    aMap.setMyLocationStyle(myLocationStyle);
                    if (isAutoPhoto){
                        silentTakePhoto(); //静默拍照
                    }
                    service.startLocation(isWalk);
                    if (!isWalk){ //驾车
                        //纠偏后的经纬度
                        service.getTraceLocation(new LocationService.ITraceLocation() {
                            @Override
                            public void getTranceLocation(List<LatLng> rectifications) {
                                manageTraceLocation(rectifications);
                            }
                        });
                    }
                    binding.ibControl.setBackgroundResource(R.mipmap.ic_start);
                    startTimer();
                    if (handler != null){
                        handler.postDelayed(runnable, 2000);
                    }
                } else { // 暂停记录行走路线
                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
                    aMap.setMyLocationStyle(myLocationStyle);
                    if (silentTakePhoto != null){
                        silentTakePhoto.stopTakePhoto(); //结束静默拍照
                    }
                    stopTimer();
                    service.stopLocation();
                    if (handler != null){
                        handler.removeCallbacks(runnable);
                    }
                }
                break;
            case R.id.btn_finish:
                if (handler != null){
                    handler.removeCallbacks(runnable);
                }
                service.stopLocation();
                isStart = !isStart;
                stopTimer();
                new XPopup.Builder(WalkLineActivity.this).asInputConfirm("路径名称", "请输入此路径对应的名称",
                    new OnInputConfirmListener() {
                        @Override
                        public void onConfirm(String text) {
                            if (StringUtils.isNotEmpty(text)){
                                //保存数据到数据库
                                polylineBean.setLatLngs(latLngs);
                                polylineBean.setDistance(totalDistance);
                                polylineBean.setPathName(text);
                                polylineBean.setTime((long)mCountTime);
                                polylineBean.setRecordTime(getCurrentTime());
                                polylineBean.setIsNew(true);
                                daoSession.insert(polylineBean);
                                //内存保存数据
                                walkModel.walkDistance.set(totalDistance);
                                walkModel.walkName.set(text);
                                walkModel.walkTime.set(mCountTime + "");
                                walkModel.recordTime.set(getCurrentTime());
                                goWalkFinishPage();
                            } else {
                                ToastUtils.showCenterToast("请输入路径名称，重新保存！");
                            }
                        }
                }).show();
                break;
            case R.id.ll_marker:
                if (null == currentLatLng) {
                    ToastUtils.showCenterToast("当前定位信息为空！");
                    return;
                }
                addMarker(currentLatLng, 0);
                break;
            case R.id.ll_voice:
                if (null == currentLatLng) {
                    ToastUtils.showCenterToast("当前定位信息为空！");
                    return;
                }
                PermissionUtils.permission(
                        PermissionConstants.MICROPHONE
                ).callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        addMarker(currentLatLng, 0);
                        Intent intent2 = new Intent(WalkLineActivity.this, AudioRecordActivity.class);
                        intent2.putExtra("latitude", currentLatLng.latitude);
                        intent2.putExtra("longitude", currentLatLng.longitude);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        ToastUtils.showToast("请开启麦克风权限");
                        PermissionUtils.launchAppDetailsSettings();
                    }
                }).request();
                break;
            case R.id.btn_photo:
                if (null == currentLatLng) {
                    ToastUtils.showCenterToast("当前定位信息为空！");
                    return;
                }
                markerLatLng = currentLatLng;
                PermissionUtils.permission(
                        PermissionConstants.CAMERA
                ).callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        addMarker(currentLatLng, 0);
                        new XPopup.Builder(WalkLineActivity.this)
                            .asBottomList("", new String[]{"拍摄(图片或视频)", "从相册选择", "取消"},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if (position == 0){ //拍摄
                                            Intent intent1 = new Intent(WalkLineActivity.this, TakePhotoActivity.class);
                                            intent1.putExtra("class", "WalkLineActivity");
                                            startActivity(intent1);
                                        } else if (position == 1){ //从相册选择
//                                            Intent intent = new Intent(WalkLineActivity.this, MultiImageSelectorActivity.class);// whether show camera
//                                            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);// max select image amount
//                                            intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
//                                            startActivityForResult(intent, TAKE_PICTURE);
                                            ImageSelector.builder()
                                                .useCamera(false) // 设置是否使用拍照
                                                .setSingle(false)  //设置是否单选
                                                .setMaxSelectCount(9) // 图片的最大选择数量，小于等于0时，不限数量。
                                                .setSelected(new ArrayList<String>()) // 把已选的图片传入默认选中。
                                                .canPreview(true) //是否可以预览图片，默认为true
                                                .start(WalkLineActivity.this, TAKE_PICTURE); // 打开相册
                                        }
                                    }
                                })
                            .show();
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        ToastUtils.showToast("请开启相机权限");
                        PermissionUtils.launchAppDetailsSettings();
                    }
                }).request();
                break;
        }
    }

    private void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
             int cnt = mCountTime;
            @Override
             public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(getStringTime(cnt++));
                    }
                });
            }
         };
        timer.schedule(timerTask,0,1000);
    }

    private void stopTimer(){
        binding.ibControl.setBackgroundResource(R.mipmap.ic_stop);
        if (null != timerTask && !timerTask.cancel()) {
            timerTask.cancel();
            timer.cancel();
        }
    }

    private String getStringTime(int cnt) {
        int hour = cnt / 3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        mCountTime = cnt;
        return String.format(Locale.CHINA,"%02d:%02d:%02d",hour,min,second);
    }

    private void goWalkFinishPage() {
        Intent intent = new Intent(WalkLineActivity.this, WalkFinishActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK && data != null) { //选择相片回调
//            List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            List<String> paths = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
            if (null != paths){
                for (String path: paths) {
                    photos.add(listIndex, path);
                    listIndex ++;
                }
                String latLng = markerLatLng.latitude + "," + markerLatLng.longitude;
                QueryBuilder<MarkerBean> queryBuilder = daoSession.queryBuilder(MarkerBean.class).where(MarkerBeanDao.Properties.LatLngs.eq(latLng));
                List<MarkerBean> markerBeans = queryBuilder.list();
                if (markerBeans != null && markerBeans.size() > 0){
                    MarkerBean markerBean = markerBeans.get(0);
                    markerBean.setPhotos(photos);
                    daoSession.update(markerBean);
                }
            }
        }
        //选择照片后重新加载标记和路线
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadMarker();
//                loadLine();
            }
        }).start();
    }

    /**
     * 静默拍照
     * 参数2：静默拍照预览区
     * 参数3：静默拍照时间间隔
     */
    private void silentTakePhoto(){
        silentTakePhoto = SilentTakePhoto.getInstance(WalkLineActivity.this, binding.flCameraFrame, photoInterval);
        silentTakePhoto.startTakePhoto();
        silentTakePhoto.setITakePhotoResults(new SilentTakePhoto.ITakePhotoResults() {
            @Override
            public void photoPath(String path) {
                if (StringUtils.isEmpty(path) && !path.endsWith("jpeg")) return;
                String latLng = currentLatLng.latitude + "," + currentLatLng.longitude;
                QueryBuilder<MarkerBean> queryBuilder = daoSession.queryBuilder(MarkerBean.class).where(MarkerBeanDao.Properties.LatLngs.eq(latLng));
                List<MarkerBean> markerBeans = queryBuilder.list();
                MarkerBean markerBean = null;
                photos = new ArrayList<>();
                listIndex = 0;
                if (markerBeans != null && markerBeans.size() > 0){ //已经存在此经纬度对应的marker
                    addMarker(currentLatLng, 1);
                    markerBean = markerBeans.get(0);
                    List<String> hasPhotos = markerBeans.get(0).getPhotos();
                    if (null != hasPhotos && hasPhotos.size()>0) { //marker对应的有图片
                        List<String> lists = Arrays.asList(hasPhotos).get(0);
                        for (int i=0; i<lists.size(); i++) {
                            photos.add(i, lists.get(i));
                            listIndex ++;
                        }
                    }
                } else { //此经纬度没有对应的marker，则保存此marker
                    addMarker(currentLatLng, 0);
                    markerBean = daoSession.queryBuilder(MarkerBean.class)
                            .where(MarkerBeanDao.Properties.LatLngs.eq(latLng))
                            .list().get(0);
                }
                photos.add(listIndex, path);
                listIndex ++;
                markerBean.setPhotos(photos);
                daoSession.update(markerBean);
            }
        });
    }

    /****************************** 生命周期函数 *****************************/
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (service != null){
            service.setActivityLeave(true);
        }
        if (handler != null){
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (service != null){
            service.setActivityLeave(false);
        }
        if (handler != null && isStart){
            handler.postDelayed(runnable, 2000);
        }
        loadLine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (service != null){
            service.stopLocation();
        }
        if (handler != null){
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            finish();
        }
        return false;
    }
}