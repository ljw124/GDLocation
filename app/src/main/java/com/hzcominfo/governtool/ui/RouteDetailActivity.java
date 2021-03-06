package com.hzcominfo.governtool.ui;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.hzcominfo.governtool.MyApplication;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.bean.PolylineBean;
import com.hzcominfo.governtool.databinding.ActivityRouteDetailBinding;
import com.hzcominfo.governtool.db.DaoSession;
import com.hzcominfo.governtool.db.MarkerBeanDao;
import com.hzcominfo.governtool.model.ModelProvider;
import com.hzcominfo.governtool.model.WalkModel;
import com.hzcominfo.governtool.utils.ActivityCollector;
import com.hzcominfo.governtool.utils.ToastUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RouteDetailActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, View.OnClickListener {

    private ActivityRouteDetailBinding binding;
    private WalkModel model;
    private MapView mMapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private UiSettings mUiSettings;
    private LatLng currentLatLng;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private PolylineBean polylineBean;
    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_detail);
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        binding.setModel(ModelProvider.getInstance().get(WalkModel.class));
        model = binding.getModel();
        mMapView = binding.map;
        mMapView.onCreate(savedInstanceState);
        //????????????Activity?????????????????????
        polylineBean = (PolylineBean)getIntent().getSerializableExtra("PolylineBean");

        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.showIndoorMap(true); //??????????????????
        aMap.setOnMyLocationChangeListener(this); //??????????????????
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17)); // ??????????????????
        setLocationStyle(); //??????????????????
        uiSettings(); // ??????????????????

        //??????????????????
        if (null != polylineBean){
            model.walkName.set(polylineBean.getPathName());
            model.recordTime.set(polylineBean.getRecordTime());
            model.walkTime.set(getStringTime(polylineBean.getTime()));
            model.walkDistance.set(polylineBean.getDistance());
            //??????????????????
            addPolylinesSoild(polylineBean.getLatLngs());
            //??????????????????marker
            addMarker();
        }

        binding.ibLocation.setOnClickListener(this);
        binding.ibReturn.setOnClickListener(this);
        binding.llRouteInfo.setOnClickListener(this);
    }

    //??????????????????
    private void setLocationStyle(){
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE); //????????????
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW); //????????????(??????????????????????????????????????????????????????)
//        myLocationStyle.interval(2000);
        myLocationStyle.strokeWidth(0);
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
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
     * ??????????????????
     * @param location
     */
    @Override
    public void onMyLocationChange(Location location) {
        if(location != null) {
            Bundle bundle = location.getExtras();
            if(bundle != null && bundle.getInt(MyLocationStyle.ERROR_CODE) == 0) {
                //????????????????????????????????????
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                ToastUtils.showToast("????????????");
            }
        } else {
            ToastUtils.showToast("????????????");
        }
    }

    /**
     * ??????????????????marker
     */
    private void addMarker() {
        QueryBuilder<MarkerBean> markerBeans = daoSession.queryBuilder(MarkerBean.class)
                .where(MarkerBeanDao.Properties.MarkerId.eq(polylineBean.getId()));
        List<MarkerBean> markers = markerBeans.list();
        if (markers != null && markers.size() > 0){
            for (MarkerBean markerBean: markers) {
                String[] latLngs = markerBean.getLatLngs().split(",");
                if (latLngs.length == 2) {
                    LatLng latLng = new LatLng(Double.parseDouble(latLngs[0]), Double.parseDouble(latLngs[1]));
                    MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_taged))
                            .position(latLng)
                            .draggable(false);
                    aMap.addMarker(markerOption);
                    aMap.setOnMarkerClickListener(markerClickListener);
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    private void addPolylinesSoild(List<String> polylineBeans) {
        if (null == polylineBeans || polylineBeans.size() == 0) return;
        List<LatLng> latLngs = new ArrayList<LatLng>();
        for (String latLng: polylineBeans) {
            String[] split = latLng.split(",");
            if (split.length == 2) {
                latLngs.add(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
            }
        }
        aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 0, 255, 0)));
        //????????????????????? Marker
        addMarkers(polylineBeans);
    }

    /**
     * ????????????????????????????????? Marker
     * @param polylineBeans
     */
    private void addMarkers(List<String> polylineBeans) {
        if (null == polylineBeans || polylineBeans.size() == 0) return;
        //?????????????????????
        String[] startPoly = polylineBeans.get(0).split(",");
        if (startPoly.length<2) return;
        startLatLng = new LatLng(Double.parseDouble(startPoly[0]), Double.parseDouble(startPoly[1]));
        //?????????????????????
        String[] endPoly = polylineBeans.get(polylineBeans.size()-1).split(",");
        endLatLng = new LatLng(Double.parseDouble(endPoly[0]), Double.parseDouble(endPoly[1]));
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_start_marker))
                .position(startLatLng)
                .title(model.walkName.get()) //??????????????????
                .setFlat(true)
                .draggable(false); //????????????????????????
        aMap.addMarker(markerOption);
        MarkerOptions markerOption1 = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_stop_marker))
                .position(endLatLng)
                .title(model.walkName.get()) //??????????????????
                .setFlat(true) //??????marker??????????????????
                .draggable(false); //????????????????????????
        aMap.addMarker(markerOption1);

        aMap.setOnMarkerClickListener(markerClickListener);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveMap();
            }
        }, 1000);
    }

    /**
     * Marker ????????????
     */
    private AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // ?????? true ?????????????????????????????????????????????false
        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng position = marker.getPosition();
            Intent intent = new Intent(RouteDetailActivity.this, MarkerEditActivity.class);
            intent.putExtra("latitude", position.latitude);
            intent.putExtra("longitude", position.longitude);
            intent.putExtra("class", "RouteDetailActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
            return true;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_location:
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
                break;
            case R.id.ib_return:
                //????????????
                polylineBean.setIsNew(false);
                daoSession.update(polylineBean);
                Intent intent = new Intent(RouteDetailActivity.this, WalkInfoActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
                break;
            case R.id.ll_route_info:
//                aMap.moveCamera(CameraUpdateFactory.changeLatLng(startLatLng));
                moveMap();
                break;
        }
    }

    /**
     * ????????????????????????????????????
     */
    private void moveMap() {
        CameraPosition cameraPosition = new CameraPosition(startLatLng, 17, 0, 30);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.moveCamera(cameraUpdate);
    }

    private String getStringTime(long t) {
        int time = (int) t;
        int hour = time/3600;
        int min = time % 3600 / 60;
        int second = time % 60;
        return String.format(Locale.CHINA,"%02d:%02d:%02d",hour,min,second);
    }

    /****************************** ?????????????????? *****************************/
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            polylineBean.setIsNew(false);
            daoSession.update(polylineBean);
            finish();
        }
        return false;
    }
}
