package com.hzcominfo.governtool.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.hzcominfo.governtool.MyApplication;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.bean.PolylineBean;
import com.hzcominfo.governtool.databinding.ActivityMainBinding;
import com.hzcominfo.governtool.db.DaoSession;
import com.hzcominfo.governtool.db.MarkerBeanDao;
import com.hzcominfo.governtool.utils.ActivityCollector;
import com.hzcominfo.governtool.utils.GpsLocationUtil;
import com.hzcominfo.governtool.utils.StringUtils;
import com.hzcominfo.governtool.utils.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import org.apache.log4j.Logger;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.hzcominfo.governtool.utils.CommonUtils.getCurrentTime;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, View.OnClickListener {

    private static Logger logger = Logger.getLogger(MainActivity.class);
    private MapView mMapView = null;
    private ActivityMainBinding binding;
    private AMap aMap;
    private MyLocationStyle myLocationStyle; //定位蓝点
    private UiSettings mUiSettings; //定义一个UiSettings对象
    private LatLng currentLatLng; //当前定位经纬度
    private LatLng lastLatLng; //上次定位经纬度
    private DaoSession daoSession;
    private boolean hasNew = false; //是否有新加的marker或line
    private static final int TAKE_PICTURE = 0;
    private ArrayList<String> photos; //保存图片、视频
    private int listIndex = 0; //图片索引
    private boolean isOne = true; //第一次定位记录位置信息
    private LatLng markerLatLng; //拍照、录音时添加marker的经纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        daoSession = ((MyApplication)getApplication()).getDaoSession();

        //获取地图控件引用
        mMapView = binding.map;
        mMapView.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.showIndoorMap(true); //开启室内地图
        aMap.setOnMyLocationChangeListener(this); //定位结果监听
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); // 设置缩放比例
        setLocationStyle(); //实现定位蓝点
        uiSettings(); // 交互控件设置

        binding.llPath.setOnClickListener(this);
        binding.llMarker.setOnClickListener(this);
        binding.llVoice.setOnClickListener(this);
        binding.ibLocation.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);
        binding.ibInfo.setOnClickListener(this);
        binding.ivNew.setOnClickListener(this);
        binding.ibPerson.setOnClickListener(this);

        initData();
    }

    private void initData() {
        //获取GPS卫星个数
        new GpsLocationUtil(this, MainActivity.this, new GpsLocationUtil.ILocation() {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadMarker();//显示所有的Marker
                addPolylinesSoild();//绘制路线
            }
        }).start();
    }

    //实现定位蓝点
    private void setLocationStyle(){
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE); //定位一次
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW); //连续定位(此种模式可以让定位箭头和手机保持一致)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER); //连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动
        myLocationStyle.interval(3000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init();
        initPhoto(intent);
    }

    /**
     * 拍照返回的数据
     */
    public void initPhoto(Intent intent){
        // 获取传递过来的图片、视频路径
        String picturePath = intent.getStringExtra("picturePath");
        if (StringUtils.isEmpty(picturePath)) return;
        photos.add(listIndex, picturePath);
        listIndex ++;
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
     * 定位结果回调
     * @param location
     */
    @Override
    public void onMyLocationChange(Location location) {
        if(location != null) {
            Bundle bundle = location.getExtras();
            if(bundle != null && bundle.getInt(MyLocationStyle.ERROR_CODE) == 0) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                /* errorCode\errorInfo\locationType */
//                Log.e("amap", "经纬度：" + location.getLongitude() + "; " + location.getLatitude());
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );
                //首次定位时设置当前经纬度
                if (currentLatLng == null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
                lastLatLng = currentLatLng;
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                //第一次定位，定位蓝点移到定位位置
                if (isOne){
                    isOne = false;
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); // 设置缩放比例
                }
            } else {
//                ToastUtils.showToast("定位失败");
            }
        } else {
//            ToastUtils.showToast("定位失败");
        }
    }

    /**
     * 绘制行走路线
     */
    private void addPolylinesSoild() {
        List<LatLng> latLngs;
        List<PolylineBean> polylineBeans = daoSession.loadAll(PolylineBean.class);
        for (PolylineBean polylineBean: polylineBeans) {
            //判断是否有新增的
            if (!hasNew && polylineBean.getIsNew()!=null && polylineBean.getIsNew()) {
                hasNew = true;
                binding.ivNew.setVisibility(View.VISIBLE);
            }
            latLngs = new ArrayList<LatLng>();
            for (String latLng: polylineBean.getLatLngs()) {
                String[] split = latLng.split(",");
                if (split.length == 2) {
                    latLngs.add(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
                }
            }
            //绘制路线
            aMap.addPolyline(new PolylineOptions().
                    addAll(latLngs).width(10).color(Color.argb(255, 0, 255, 0)));
            //标记起点和终点 Marker
            if (latLngs.size() > 1) {
                addMarkers(latLngs.get(0), latLngs.get(latLngs.size() - 1));
            }
        }
    }

    /**
     * 起使位置和结束位置标记 Marker
     */
    private void addMarkers(LatLng startLatLng, LatLng endLatLng) {
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_start_marker))
                .position(startLatLng)
                .title("杭州市") //点标记的标题
                .snippet("杭州海康威视") //点标记的内容
                .draggable(false); //点标记是否可拖拽
        aMap.addMarker(markerOption);
        MarkerOptions markerOption1 = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_stop_marker))
                .position(endLatLng)
                .title("杭州市")
                .snippet("杭州海康威视")
                .draggable(false);
        aMap.addMarker(markerOption1);
    }

    /**
     * 加载标记的Marker
     */
    private void loadMarker() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.ivNew.setVisibility(View.GONE);
            }
        });
        aMap.clear(true);
        List<MarkerBean> markerBeans = daoSession.loadAll(MarkerBean.class);
        for (MarkerBean markerBean: markerBeans) {
            //判断是否有新增的
            if (!hasNew && markerBean.getIsNew()) {
                hasNew = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.ivNew.setVisibility(View.VISIBLE);
                    }
                });
            }
            String[] latLngs = markerBean.getLatLngs().split(",");
            if (latLngs.length == 2) {
                LatLng latLng = new LatLng(Double.parseDouble(latLngs[0]), Double.parseDouble(latLngs[1]));
                addMarker(latLng, 1);
            }
        }
    }

    /**
     * Marker 点击事件
     */
    private AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // 返回 true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng position = marker.getPosition();
            Intent intent = new Intent(MainActivity.this, MarkerEditActivity.class);
            intent.putExtra("latitude", position.latitude);
            intent.putExtra("longitude", position.longitude);
            intent.putExtra("class", "MainActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
            return true; // 返回:true 表示点击marker 后marker 不会移动到地图中心；返回false 表示点击marker 后marker 会自动移动到地图中心
        }
    };

    /**
     * 标记 Marker
     * type: 0-新增，1-展示
     */
    private void addMarker(LatLng currentLatLng, int type) {
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_taged))
                .position(currentLatLng)
                .setFlat(true) //设置marker平贴地图效果
                .draggable(false); //点标记是否可拖拽
        aMap.addMarker(markerOption);
        aMap.setOnMarkerClickListener(markerClickListener);

        if (type == 0) {
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
            daoSession.insertOrReplace(markerBean);
            binding.ivNew.setVisibility(View.VISIBLE);
        }
   }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_path:
                Intent intent = new Intent(MainActivity.this, WalkLineActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
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
                        Intent intent2 = new Intent(MainActivity.this, AudioRecordActivity.class);
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
                        new XPopup.Builder(MainActivity.this)
                            .asBottomList("", new String[]{"拍摄(图片或视频)", "从相册选择", "取消"},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if (position == 0){ //拍摄
                                            Intent intent1 = new Intent(MainActivity.this, TakePhotoActivity.class);
                                            intent1.putExtra("class", "MainActivity");
                                            startActivity(intent1);
                                        } else if (position == 1){ //从相册选择
                                            ImageSelector.builder()
                                                .useCamera(false) // 设置是否使用拍照
                                                .setSingle(false)  //设置是否单选
                                                .setMaxSelectCount(9) // 图片的最大选择数量，小于等于0时，不限数量。
                                                .setSelected(new ArrayList<String>()) // 把已选的图片传入默认选中。
                                                .canPreview(true) //是否可以预览图片，默认为true
                                                .start(MainActivity.this, TAKE_PICTURE); // 打开相册
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
            case R.id.ib_location:
                setLocationStyle();
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
                aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); // 设置缩放比例
                break;
            case R.id.ib_info:
            case R.id.iv_new:
                Intent intent1 = new Intent(MainActivity.this, WalkInfoActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
                break;
            case R.id.ib_person:
                Intent intent3 = new Intent(MainActivity.this, MyActivity.class);
                startActivity(intent3);
                break;
        }
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
                String latLng = currentLatLng.latitude + "," + currentLatLng.longitude;
                QueryBuilder<MarkerBean> queryBuilder = daoSession.queryBuilder(MarkerBean.class).where(MarkerBeanDao.Properties.LatLngs.eq(latLng));
                List<MarkerBean> markerBeans = queryBuilder.list();
                if (markerBeans != null && markerBeans.size() > 0){
                    MarkerBean markerBean = markerBeans.get(0);
                    markerBean.setPhotos(photos);
                    daoSession.update(markerBean);
                }
            }
        }
    }

    /****************************** 生命周期函数 *****************************/
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    /**
     * 双击返回键退出应用
     */
    private long firstTime = 0;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                ActivityCollector.finishAll();
                System.exit(0);
                logger.info("程序正常退出");
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
