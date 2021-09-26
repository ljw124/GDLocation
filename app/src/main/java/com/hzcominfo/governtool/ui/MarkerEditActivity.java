package com.hzcominfo.governtool.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.hzcominfo.governtool.MyApplication;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.adapter.AudioRecordAdapter;
import com.hzcominfo.governtool.adapter.BaseAdapter;
import com.hzcominfo.governtool.adapter.PhotoAdapter;
import com.hzcominfo.governtool.bean.AudioBean;
import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.custom.ImageLoader;
import com.hzcominfo.governtool.custom.audio.MediaManager;
import com.hzcominfo.governtool.databinding.ActivityMarkerEditBinding;
import com.hzcominfo.governtool.db.DaoSession;
import com.hzcominfo.governtool.db.MarkerBeanDao;
import com.hzcominfo.governtool.utils.ActivityCollector;
import com.hzcominfo.governtool.utils.StringUtils;
import com.hzcominfo.governtool.utils.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import org.greenrobot.greendao.query.QueryBuilder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.hzcominfo.governtool.utils.CommonUtils.getCurrentTime;

public class MarkerEditActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener, View.OnClickListener, BaseAdapter.OnItemClickListener<String>, AMap.OnMarkerDragListener {

    private ActivityMarkerEditBinding binding;
    private MapView mMapView = null;
    private AMap aMap;
    private UiSettings mUiSettings;
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private RecyclerView rvPhoto;
    private PhotoAdapter adapter;
    private DaoSession daoSession;
    private MarkerBean markerBean;
    private ArrayList<String> photos;
    private int listIndex = 0;
    private int type;
    private String latLng;
    private String videoPath;
    private String picturePath;
    private BasePopupView playPopupView;
    private boolean isVideo = false;
    private static final int TAKE_PICTURE = 0;
    private QueryBuilder<MarkerBean> queryBuilder;
    private AudioRecordAdapter audioRecordAdapter;
    private RecyclerView rvAudio;
    private ArrayList<AudioBean> audios;
    private BasePopupView deletePopupView;
    private int deleteVoicePosition;
    private List<String> voices; //保存到数据库中的语音集合
    private List<String> voiceTimes; //保存到数据库中的语音时长集合
    private double latitude,longitude; //传递过来的经纬度
    private boolean isAudio; //如果是录音需要在onResume中重新加载语音
    private String toClass; //哪个activity过来回到哪个activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_marker_edit);
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
        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        uiSettings(); // 交互控件设置

        binding.ibBack.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.tvAddAudio.setOnClickListener(this);

        //显示照片
        adapter = new PhotoAdapter();
        adapter.setContext(this);
        photos = new ArrayList<>();
        photos.add(listIndex, "");
        listIndex ++;
        adapter.setDataList(photos);
        rvPhoto = binding.rvPhoto;
        rvPhoto.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true));
        rvPhoto.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setItemDelete(new PhotoAdapter.IItemDelete() {
            @Override
            public void delete(int position) {
                photos.remove(position);
                listIndex --;
                adapter.setDataList(photos);
                adapter.notifyDataSetChanged();
            }
        });

        //显示语音
        audios = new ArrayList<>();
        audioRecordAdapter = new AudioRecordAdapter();
        audioRecordAdapter.setContext(this, true);
        rvAudio = binding.rvAudio;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvAudio.setLayoutManager(new LinearLayoutManager(this));
        rvAudio.setAdapter(audioRecordAdapter);
        audioRecordAdapter.setItemDelete(new AudioRecordAdapter.IItemDelete() {
            @Override
            public void delete(int position) {
                deleteVoicePosition = position;
                deletePopupView = new XPopup.Builder(MarkerEditActivity.this)
                        .asCustom(new DeletePopup(MarkerEditActivity.this))
                        .show();
            }
        });

        initData();
    }

    private void initData() {
        initMap(); //初始化地图信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMarkerData(); //初始化 Marker信息
            }
        }).start();
    }

    /**
     * 初始化地图信息
     */
    private void initMap(){
        //获取传递过来的经纬度
        toClass = getIntent().getStringExtra("class");
        latitude = getIntent().getDoubleExtra("latitude", 30.210515);
        longitude = getIntent().getDoubleExtra("longitude", 120.22263);
        latLng = latitude + "," + longitude;
        //添加 Marker
        addMarker(new LatLng(latitude, longitude));
        //将地图定位到指定位置
        CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude, longitude), 17, 0, 30);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.moveCamera(cameraUpdate);

        //根据经纬度进行逆地理编码
        toGeocodeSearch(latitude, longitude);
    }

    /**
     * 根据经纬度进行逆地理编码
     */
    private void toGeocodeSearch(double latitude, double longitude){
        geocoderSearch = new GeocodeSearch(MarkerEditActivity.this);
        geocoderSearch.setOnGeocodeSearchListener(MarkerEditActivity.this);
        LatLonPoint latlng = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(latlng, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * 初始化 Marker信息
     */
    private void initMarkerData() {
        queryBuilder = daoSession.queryBuilder(MarkerBean.class).where(MarkerBeanDao.Properties.LatLngs.eq(latLng));
        List<MarkerBean> markerBeans = queryBuilder.list();
        if (markerBeans != null && markerBeans.size() > 0){
            MarkerBean markerBean = markerBeans.get(0);
            if (StringUtils.isNotEmpty(markerBean.getDescribe())){
                binding.etDescribe.setText(markerBean.getDescribe());
            }
            if (null != markerBean.getPhotos() && markerBean.getPhotos().size()>0){
                List<List<String>> lists = Arrays.asList(markerBean.getPhotos());
                List<String> strings = lists.get(0);
                photos.clear();
                for (int i=0; i<strings.size(); i++) {
                    photos.add(i, strings.get(i));
                }
                listIndex = strings.size(); //这里要比长度多一，下次添加时直接在最后了
                adapter.setDataList(photos);
                adapter.notifyDataSetChanged();
            }
            if (null != markerBean.getVoices() && markerBean.getVoices().size()>0){
                voices = Arrays.asList(markerBean.getVoices()).get(0);
                voiceTimes = Arrays.asList(markerBean.getVoiceTime()).get(0);
                audios.clear(); //防止添加语音返回后数据重复
                for (int i=0; i<voices.size(); i++){
                    AudioBean audioBean = new AudioBean();
                    audioBean.setFilePath(voices.get(i));
                    audioBean.setTime(Integer.parseInt(voiceTimes.get(i)));
                    audios.add(audioBean);
                }
//                int height = audios.size() * 45;
//                ViewGroup.LayoutParams layoutParams = rvAudio.getLayoutParams();
//                layoutParams.height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));
//                rvAudio.setLayoutParams(layoutParams);
                audioRecordAdapter.setDataList(audios);
                audioRecordAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                initPhoto(intent);
            }
        }).start();
    }

    public void initPhoto(Intent intent){
        // 获取传递过来的图片、视频路径
        String picturePath = intent.getStringExtra("picturePath");
        type = intent.getIntExtra("type", -1);
        if (StringUtils.isNotEmpty(picturePath)){
            if (type == 1){ //照片
                photos.add(listIndex, picturePath);
                listIndex ++;
                adapter.setDataList(photos);
                adapter.notifyDataSetChanged();
            } else if (type == 2) { //视频
                photos.add(listIndex, picturePath);
                listIndex ++;
                adapter.setDataList(photos);
                adapter.notifyDataSetChanged();
            }
        }
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
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress();
                binding.tvPositionInfo.setText(addressName);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("对不起，没有搜索到相关数据！");
                    }
                });
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showError(rCode);
                }
            });
        }
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * 标记 Marker
     */
    private void addMarker(LatLng currentLatLng) {
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_position))
                .position(currentLatLng)
                .draggable(true);
        aMap.addMarker(markerOption);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_save:
                List<MarkerBean> markerBeans = queryBuilder.list();
                if (markerBeans != null && markerBeans.size() > 0){
                    markerBean = markerBeans.get(0);
                    markerBean.setLatLngs(latLng);
                    markerBean.setLoacationInfo(addressName);
                    markerBean.setDescribe(binding.etDescribe.getText().toString().trim());
                    markerBean.setPhotos(photos);
                    markerBean.setRecordTime(getCurrentTime());
                    daoSession.update(markerBean);

                    ToastUtils.showCenterToast("保存成功");
                    Intent intent = null;
                    switch (toClass){
                        case "MainActivity":
                            intent = new Intent(MarkerEditActivity.this, MainActivity.class);
                            break;
                        case "RouteDetailActivity":
                            intent = new Intent(MarkerEditActivity.this, RouteDetailActivity.class);
                            break;
                        case "WalkLineActivity":
                            intent = new Intent(MarkerEditActivity.this, WalkLineActivity.class);
                            break;
                        case "MarkerDetailActivity":
                            intent = new Intent(MarkerEditActivity.this, MarkerDetailActivity.class);
                            break;
                    }
                    intent.putExtra("MarkerBean", (Serializable)markerBean);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.tv_add_audio:
                PermissionUtils.permission(
                        PermissionConstants.MICROPHONE
                ).callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        isAudio = true;
                        Intent intent = new Intent(MarkerEditActivity.this, AudioRecordActivity.class);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        ToastUtils.showToast("请开启麦克风权限");
                        PermissionUtils.launchAppDetailsSettings();
                    }
                }).request();
                break;
        }
    }

    @Override
    public void onItemClick(String item, int position) {
        if (position == 0){
            PermissionUtils.permission(
                    PermissionConstants.CAMERA
            ).callback(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(@NonNull List<String> granted) {
                    isAudio = false;
                    new XPopup.Builder(MarkerEditActivity.this)
                        .asBottomList("", new String[]{"拍摄(图片或视频)", "从相册选择", "取消"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    if (position == 0){ //拍摄
                                        Intent intent1 = new Intent(MarkerEditActivity.this, TakePhotoActivity.class);
                                        intent1.putExtra("class", "MarkerEditActivity");
                                        startActivity(intent1);
                                    } else if (position == 1){ //从相册选择
                                        // https://github.com/lovetuzitong/MultiImageSelector
//                                        Intent intent = new Intent(MarkerEditActivity.this, MultiImageSelectorActivity.class);// whether show camera
//                                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);// max select image amount
//                                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
//                                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);// default select images (support array list)
//                                        intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
//                                        startActivityForResult(intent, TAKE_PICTURE);
                                        ImageSelector.builder()
                                            .useCamera(false) // 设置是否使用拍照
                                            .setSingle(false)  //设置是否单选
                                            .setMaxSelectCount(9) // 图片的最大选择数量，小于等于0时，不限数量。
                                            .setSelected(new ArrayList<String>()) // 把已选的图片传入默认选中。
                                            .canPreview(true) //是否可以预览图片，默认为true
                                            .start(MarkerEditActivity.this, TAKE_PICTURE); // 打开相册
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
        } else {
            String src = photos.get(position);
            if (src.substring(src.length() - 3).equals("mp4")){ //点击视频，预览
                isVideo = true;
                videoPath = src;
                playPopupView = new XPopup.Builder(MarkerEditActivity.this)
                        .asCustom(new PlayPopup(MarkerEditActivity.this))
                        .show();
            } else {
                isVideo = false;
                picturePath = src;
                binding.ivPicture.setVisibility(View.VISIBLE);
                new XPopup.Builder(MarkerEditActivity.this)
                        .asImageViewer(binding.ivPicture, picturePath, new ImageLoader())
                        .isShowSaveButton(false)
                        .show();
            }
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
                adapter.setDataList(photos);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * marker拖拽监听
     */
    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //更新微调后的经纬度
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        latLng = marker.getPosition().latitude + "," + marker.getPosition().longitude;
        //根据经纬度进行逆地理编码
        toGeocodeSearch(marker.getPosition().latitude, marker.getPosition().longitude);
    }

    /**
     * 视频、图片预览弹框
     */
    private class PlayPopup extends CenterPopupView {

        public PlayPopup(@NonNull Context context) {
            super(context);
        }

        @Override
        protected int getImplLayoutId() {
            return R.layout.popup_video_preview;
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            VideoView videoView = findViewById(R.id.vv_video);
            ImageView imageView = findViewById(R.id.iv_picture);
            if (isVideo){
                imageView.setVisibility(GONE);
                videoView.setVisibility(VISIBLE);
                //播放视频
                videoView.setVideoPath(videoPath);
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);
                    }
                });
                videoView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPopupView.dismiss();
                        videoView.pause();
                    }
                });
            } else {
                imageView.setVisibility(VISIBLE);
                videoView.setVisibility(GONE);
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPopupView.dismiss();
                    }
                });
            }
        }
        protected void onShow() {
            super.onShow();
        }
    }

    private class DeletePopup extends CenterPopupView {

        public DeletePopup(@NonNull Context context) {
            super(context);
        }

        @Override
        protected int getImplLayoutId() {
            return R.layout.popup_delete_ensure;
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            TextView tvMsg = findViewById(R.id.tv_msg);
            tvMsg.setText("确定删除语音信息吗？");
            Button btnCancel = findViewById(R.id.btn_cancel);
            Button btnEnsure = findViewById(R.id.btn_ensure);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePopupView.dismiss();
                }
            });
            btnEnsure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //更新界面显示
                    audios.remove(deleteVoicePosition);
                    audioRecordAdapter.setDataList(audios);
                    audioRecordAdapter.notifyDataSetChanged();
                    //更新数据库
                    List newVoices = new ArrayList(voices);
                    newVoices.remove(deleteVoicePosition);
                    List newVoiceTimes = new ArrayList(voiceTimes);
                    newVoiceTimes.remove(deleteVoicePosition);
                    List<MarkerBean> markerBeans = queryBuilder.list();
                    if (markerBeans != null && markerBeans.size() > 0){
                        MarkerBean markerBean = markerBeans.get(0);
                        markerBean.setVoices(newVoices);
                        markerBean.setVoiceTime(newVoiceTimes);
                        daoSession.update(markerBean);
                    }
                    deletePopupView.dismiss();
                }
            });
        }
        protected void onShow() {
            super.onShow();
        }
    }

    /****************************** 生命周期函数 *****************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        MediaManager.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        MediaManager.resume();
        if (isAudio){
            initMarkerData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
        mMapView.onPause();
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
