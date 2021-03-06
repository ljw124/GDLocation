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
    private MyLocationStyle myLocationStyle; //????????????
    private UiSettings mUiSettings; //????????????UiSettings??????

    private static final int DISTANCE_ERROR = 20; //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    private static final int DISTANCE_ERROR_DRIVE = 30; //??????????????????
    private LatLng currentLatLng; //?????????????????????
    private LatLng lastLatLng; //?????????????????????
    private double totalDistance = 0; //?????????
    private DaoSession daoSession;
    private PolylineBean polylineBean;
    private List<String> latLngs;
    private boolean isStart = false;
    private TimerTask timerTask;
    private Timer timer;
    private int mCountTime = 0;
    private boolean isFirstLocation = true;
    private static final int TAKE_PICTURE = 0;
    private ArrayList<String> photos; //?????????????????????
    private int listIndex = 0; //????????????
    private LocationService service; //????????????
    private boolean isOne = true; //?????????????????????????????????
    private ArrayList<LatLng> currentLatLngs; //?????????????????????marker
    private LatLng markerLatLng; //????????????????????????marker????????????
    private Long markerId; //????????????marker?????????id
    private SilentTakePhoto silentTakePhoto; //????????????
    private Polyline tracedPolyline = null;
    //??????????????????
    private boolean isAutoPhoto = false;
    private boolean isWalk = true;
    private int photoInterval = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isWalk){ //???????????????
                //?????????????????????
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
        //????????????marker?????????id
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
        aMap.showIndoorMap(true); //??????????????????
        aMap.setOnMyLocationChangeListener(this); //??????????????????
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); // ??????????????????
        setLocationStyle(); //??????????????????
        uiSettings(); // ??????????????????

        binding.ibBack.setOnClickListener(this);
        binding.ibLocation.setOnClickListener(this);
        binding.ibControl.setOnClickListener(this);
        binding.btnFinish.setOnClickListener(this);
        binding.llMarker.setOnClickListener(this);
        binding.llVoice.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);

        //???????????????
        polylineBean = new PolylineBean();
        latLngs = new ArrayList<>();
        binding.ibControl.setBackgroundResource(R.mipmap.ic_stop);

        currentLatLngs = new ArrayList<>();
        //???????????????Marker - ?????????????????????????????????marker
//        loadMarker();

        //??????????????????
        Intent intent = new Intent("LocationService");
        intent.setPackage("com.hzcominfo.governtool");
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        //??????GPS????????????
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

        //????????????????????????
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
            ToastUtils.showToast("?????????????????????");
            LocationService.LocationServiceBinder binder = (LocationService.LocationServiceBinder) iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ToastUtils.showToast("?????????????????????");
        }
    };

    //????????????
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
     * ?????????????????????????????????
     * @param latitude
     * @param longitude
     */
    private void manageLocation(double latitude, double longitude){
        //????????????????????????????????????
        if (currentLatLng == null) {
            currentLatLng = new LatLng(latitude, longitude);
        }
        lastLatLng = currentLatLng;
        currentLatLng = new LatLng(latitude, longitude);
        float movedDistance = AMapUtils.calculateLineDistance(currentLatLng, lastLatLng);
        if (movedDistance > DISTANCE_ERROR || movedDistance == 0) {
            return;
        }
        //??????????????????
        aMap.addPolyline(new PolylineOptions().add(lastLatLng, currentLatLng).width(10).color(Color.argb(255, 0, 255, 0)));
        //????????????
        totalDistance += movedDistance/1000;
        DecimalFormat df = new DecimalFormat("#.000");
        totalDistance = Double.valueOf(df.format(totalDistance));
        tvDistance.setText(totalDistance+"");
        //??????????????????
        String latLng1 = lastLatLng.latitude + "," + lastLatLng.longitude;
        latLngs.add(latLng1);
        String latLng2 = currentLatLng.latitude + "," + currentLatLng.longitude;
        latLngs.add(latLng2);
        //????????????????????????marker
        if (isFirstLocation){
            isFirstLocation = false;
            //????????????????????????marker
            addStartMarkers();
        }
    }

    /**
     * ???????????????????????????????????????
     * @param rectifications ????????????????????????
     */
    private void manageTraceLocation(List<LatLng> rectifications){
        //????????????
        if(rectifications == null) return;
        if(tracedPolyline == null) {
            tracedPolyline = aMap.addPolyline(new PolylineOptions()
                    .width(10).color(Color.argb(255, 0, 255, 0)));
        }
        tracedPolyline.setPoints(rectifications);
        //??????????????????latLngs???
        for (LatLng latLng: rectifications) {
            //????????????????????????????????????
            if (currentLatLng == null) {
                currentLatLng = new LatLng(latLng.latitude, latLng.longitude);
            }
            lastLatLng = currentLatLng;
            currentLatLng = new LatLng(latLng.latitude, latLng.longitude);
            float movedDistance = AMapUtils.calculateLineDistance(currentLatLng, lastLatLng);
            if (movedDistance > DISTANCE_ERROR_DRIVE || movedDistance == 0) {
                return;
            }
            //????????????
            totalDistance += movedDistance/1000;
            DecimalFormat df = new DecimalFormat("#.000");
            totalDistance = Double.valueOf(df.format(totalDistance));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvDistance.setText(totalDistance+"");
                }
            });
            //??????????????????
            String latLng1 = lastLatLng.latitude + "," + lastLatLng.longitude;
            latLngs.add(latLng1);
            String latLng2 = currentLatLng.latitude + "," + currentLatLng.longitude;
            latLngs.add(latLng2);
            //????????????????????????marker
            if (isFirstLocation){
                isFirstLocation = false;
                //????????????????????????marker
                addStartMarkers();
            }
        }
    }

    /**
     * ?????????????????????
     */
    public void initPhoto(Intent intent){
        // ??????????????????????????????????????????
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
     * ??????????????????????????????????????????
     */
    private void loadLine() {
        //??????activity??????????????????????????????????????????
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
        //??????????????????
        List<LatLng> latLngList = new ArrayList<>();
        for (String latLng: latLngs) {
            String[] split = latLng.split(",");
            if (split.length == 2) {
                latLngList.add(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
            }
            //????????????
            aMap.addPolyline(new PolylineOptions().addAll(latLngList).width(10).color(Color.argb(255, 0, 255, 0)));
            //????????????
            addStartMarkers();
        }
    }

    //??????????????????
    private void setLocationStyle(){
        myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE); //????????????
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW); //????????????(??????????????????????????????????????????????????????)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER); //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        myLocationStyle.interval(2000); //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        myLocationStyle.strokeWidth(0); //?????????????????????????????????????????????
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0)); //?????????????????????????????????????????????
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);//?????????true?????????????????????????????????false??????????????????????????????????????????????????????false???
    }

    // ??????????????????
    private void uiSettings(){
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false); //????????????????????????
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM); //???????????????????????????
        mUiSettings.setCompassEnabled(false); //?????????
        mUiSettings.setMyLocationButtonEnabled(false); //???????????????????????????
        mUiSettings.setScaleControlsEnabled(false);//?????????????????????????????????
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//??????logo??????
    }

    /**
     * ???????????????????????????
     *
     * @param location ????????????????????????
     */
    @Override
    public void onMyLocationChange(Location location) {
        if(location != null) {
            Bundle bundle = location.getExtras();
            if(bundle != null && bundle.getInt(MyLocationStyle.ERROR_CODE) == 0) {
                //????????????????????????????????????
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
//                ToastUtils.showToast("????????????");
            }
        } else {
//            ToastUtils.showToast("????????????");
        }
    }

    /**
     * ?????????????????? Marker
     */
    private void addStartMarkers() {
        if (null == latLngs || latLngs.size() == 0) return;
        //??????????????????????????????
        String[] firstPoly = latLngs.get(0).split(",");
        LatLng latlng =  new LatLng(Double.parseDouble(firstPoly[0]), Double.parseDouble(firstPoly[1]));
        MarkerOptions markerOption1 = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_start_marker))
                .position(latlng)
                .draggable(false);
        aMap.addMarker(markerOption1);
    }

    /**
     * ?????? Marker
     * type: 0-?????????1-??????
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
            //?????????????????????????????????????????????????????????????????????
            if (null != queryBuilder && queryBuilder.list().size()>0) { //???????????????????????????
                List<String> hasPhotos = queryBuilder.list().get(0).getPhotos();
                if (null != hasPhotos && hasPhotos.size()>0){ //marker??????????????????
                    List<String> lists = Arrays.asList(hasPhotos).get(0);
                    photos = new ArrayList<>();
                    listIndex = 0;
                    for (int i=0; i<lists.size(); i++) {
                        photos.add(i, lists.get(i));
                        listIndex ++;
                    }
                } else { //marker?????????????????????
                    photos = new ArrayList<>();
                    listIndex = 0;
                    photos.add(listIndex, "");
                    listIndex ++;
                }
                return;
            }
            //???marker??????????????????????????????
            photos = new ArrayList<>();
            listIndex = 0;
            photos.add(listIndex, "");
            listIndex ++;
            //Marker ??????????????????
            MarkerBean markerBean = new MarkerBean();
            markerBean.setLatLngs(latLng);
            markerBean.setRecordTime(getCurrentTime());
            markerBean.setIsNew(true);
            markerBean.setMarkerId(markerId);
            daoSession.insertOrReplace(markerBean);
        }
    }

    /**
     * ??????????????????????????????????????????Marker
     * todo ??????????????????????????????marker?????????????????????????????????????????????(?????????)
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
     * Marker ????????????
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
                aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); // ??????????????????
                break;
            case R.id.ib_control:
                isStart = !isStart;
                if (isStart) { // ????????????????????????
                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE); //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????1???1????????????
                    aMap.setMyLocationStyle(myLocationStyle);
                    if (isAutoPhoto){
                        silentTakePhoto(); //????????????
                    }
                    service.startLocation(isWalk);
                    if (!isWalk){ //??????
                        //?????????????????????
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
                } else { // ????????????????????????
                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    aMap.setMyLocationStyle(myLocationStyle);
                    if (silentTakePhoto != null){
                        silentTakePhoto.stopTakePhoto(); //??????????????????
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
                new XPopup.Builder(WalkLineActivity.this).asInputConfirm("????????????", "?????????????????????????????????",
                    new OnInputConfirmListener() {
                        @Override
                        public void onConfirm(String text) {
                            if (StringUtils.isNotEmpty(text)){
                                //????????????????????????
                                polylineBean.setLatLngs(latLngs);
                                polylineBean.setDistance(totalDistance);
                                polylineBean.setPathName(text);
                                polylineBean.setTime((long)mCountTime);
                                polylineBean.setRecordTime(getCurrentTime());
                                polylineBean.setIsNew(true);
                                daoSession.insert(polylineBean);
                                //??????????????????
                                walkModel.walkDistance.set(totalDistance);
                                walkModel.walkName.set(text);
                                walkModel.walkTime.set(mCountTime + "");
                                walkModel.recordTime.set(getCurrentTime());
                                goWalkFinishPage();
                            } else {
                                ToastUtils.showCenterToast("???????????????????????????????????????");
                            }
                        }
                }).show();
                break;
            case R.id.ll_marker:
                if (null == currentLatLng) {
                    ToastUtils.showCenterToast("???????????????????????????");
                    return;
                }
                addMarker(currentLatLng, 0);
                break;
            case R.id.ll_voice:
                if (null == currentLatLng) {
                    ToastUtils.showCenterToast("???????????????????????????");
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
                        ToastUtils.showToast("????????????????????????");
                        PermissionUtils.launchAppDetailsSettings();
                    }
                }).request();
                break;
            case R.id.btn_photo:
                if (null == currentLatLng) {
                    ToastUtils.showCenterToast("???????????????????????????");
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
                            .asBottomList("", new String[]{"??????(???????????????)", "???????????????", "??????"},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if (position == 0){ //??????
                                            Intent intent1 = new Intent(WalkLineActivity.this, TakePhotoActivity.class);
                                            intent1.putExtra("class", "WalkLineActivity");
                                            startActivity(intent1);
                                        } else if (position == 1){ //???????????????
//                                            Intent intent = new Intent(WalkLineActivity.this, MultiImageSelectorActivity.class);// whether show camera
//                                            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);// max select image amount
//                                            intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
//                                            startActivityForResult(intent, TAKE_PICTURE);
                                            ImageSelector.builder()
                                                .useCamera(false) // ????????????????????????
                                                .setSingle(false)  //??????????????????
                                                .setMaxSelectCount(9) // ??????????????????????????????????????????0?????????????????????
                                                .setSelected(new ArrayList<String>()) // ???????????????????????????????????????
                                                .canPreview(true) //????????????????????????????????????true
                                                .start(WalkLineActivity.this, TAKE_PICTURE); // ????????????
                                        }
                                    }
                                })
                            .show();
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        ToastUtils.showToast("?????????????????????");
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
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK && data != null) { //??????????????????
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
        //??????????????????????????????????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadMarker();
//                loadLine();
            }
        }).start();
    }

    /**
     * ????????????
     * ??????2????????????????????????
     * ??????3???????????????????????????
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
                if (markerBeans != null && markerBeans.size() > 0){ //?????????????????????????????????marker
                    addMarker(currentLatLng, 1);
                    markerBean = markerBeans.get(0);
                    List<String> hasPhotos = markerBeans.get(0).getPhotos();
                    if (null != hasPhotos && hasPhotos.size()>0) { //marker??????????????????
                        List<String> lists = Arrays.asList(hasPhotos).get(0);
                        for (int i=0; i<lists.size(); i++) {
                            photos.add(i, lists.get(i));
                            listIndex ++;
                        }
                    }
                } else { //???????????????????????????marker???????????????marker
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

    /****************************** ?????????????????? *****************************/
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