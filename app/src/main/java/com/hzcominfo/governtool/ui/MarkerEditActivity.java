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
    private List<String> voices; //????????????????????????????????????
    private List<String> voiceTimes; //??????????????????????????????????????????
    private double latitude,longitude; //????????????????????????
    private boolean isAudio; //????????????????????????onResume?????????????????????
    private String toClass; //??????activity??????????????????activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_marker_edit);
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        //????????????????????????
        mMapView = binding.map;
        mMapView.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.setOnMarkerDragListener(this);// ??????marker????????????????????????
        uiSettings(); // ??????????????????

        binding.ibBack.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.tvAddAudio.setOnClickListener(this);

        //????????????
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

        //????????????
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
        initMap(); //?????????????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMarkerData(); //????????? Marker??????
            }
        }).start();
    }

    /**
     * ?????????????????????
     */
    private void initMap(){
        //??????????????????????????????
        toClass = getIntent().getStringExtra("class");
        latitude = getIntent().getDoubleExtra("latitude", 30.210515);
        longitude = getIntent().getDoubleExtra("longitude", 120.22263);
        latLng = latitude + "," + longitude;
        //?????? Marker
        addMarker(new LatLng(latitude, longitude));
        //??????????????????????????????
        CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude, longitude), 17, 0, 30);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.moveCamera(cameraUpdate);

        //????????????????????????????????????
        toGeocodeSearch(latitude, longitude);
    }

    /**
     * ????????????????????????????????????
     */
    private void toGeocodeSearch(double latitude, double longitude){
        geocoderSearch = new GeocodeSearch(MarkerEditActivity.this);
        geocoderSearch.setOnGeocodeSearchListener(MarkerEditActivity.this);
        LatLonPoint latlng = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(latlng, 200, GeocodeSearch.AMAP);// ???????????????????????????Latlng????????????????????????????????????????????????????????????????????????????????????GPS???????????????
        geocoderSearch.getFromLocationAsyn(query);// ?????????????????????????????????
    }

    /**
     * ????????? Marker??????
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
                listIndex = strings.size(); //????????????????????????????????????????????????????????????
                adapter.setDataList(photos);
                adapter.notifyDataSetChanged();
            }
            if (null != markerBean.getVoices() && markerBean.getVoices().size()>0){
                voices = Arrays.asList(markerBean.getVoices()).get(0);
                voiceTimes = Arrays.asList(markerBean.getVoiceTime()).get(0);
                audios.clear(); //???????????????????????????????????????
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
        // ??????????????????????????????????????????
        String picturePath = intent.getStringExtra("picturePath");
        type = intent.getIntExtra("type", -1);
        if (StringUtils.isNotEmpty(picturePath)){
            if (type == 1){ //??????
                photos.add(listIndex, picturePath);
                listIndex ++;
                adapter.setDataList(photos);
                adapter.notifyDataSetChanged();
            } else if (type == 2) { //??????
                photos.add(listIndex, picturePath);
                listIndex ++;
                adapter.setDataList(photos);
                adapter.notifyDataSetChanged();
            }
        }
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
     * ?????????????????????
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
                        ToastUtils.showToast("??????????????????????????????????????????");
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
     * ????????????????????????
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * ?????? Marker
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

                    ToastUtils.showCenterToast("????????????");
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
                        ToastUtils.showToast("????????????????????????");
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
                        .asBottomList("", new String[]{"??????(???????????????)", "???????????????", "??????"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    if (position == 0){ //??????
                                        Intent intent1 = new Intent(MarkerEditActivity.this, TakePhotoActivity.class);
                                        intent1.putExtra("class", "MarkerEditActivity");
                                        startActivity(intent1);
                                    } else if (position == 1){ //???????????????
                                        // https://github.com/lovetuzitong/MultiImageSelector
//                                        Intent intent = new Intent(MarkerEditActivity.this, MultiImageSelectorActivity.class);// whether show camera
//                                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);// max select image amount
//                                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
//                                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);// default select images (support array list)
//                                        intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
//                                        startActivityForResult(intent, TAKE_PICTURE);
                                        ImageSelector.builder()
                                            .useCamera(false) // ????????????????????????
                                            .setSingle(false)  //??????????????????
                                            .setMaxSelectCount(9) // ??????????????????????????????????????????0?????????????????????
                                            .setSelected(new ArrayList<String>()) // ???????????????????????????????????????
                                            .canPreview(true) //????????????????????????????????????true
                                            .start(MarkerEditActivity.this, TAKE_PICTURE); // ????????????
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
        } else {
            String src = photos.get(position);
            if (src.substring(src.length() - 3).equals("mp4")){ //?????????????????????
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
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK && data != null) { //??????????????????
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
     * marker????????????
     */
    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //???????????????????????????
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        latLng = marker.getPosition().latitude + "," + marker.getPosition().longitude;
        //????????????????????????????????????
        toGeocodeSearch(marker.getPosition().latitude, marker.getPosition().longitude);
    }

    /**
     * ???????????????????????????
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
                //????????????
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
            tvMsg.setText("??????????????????????????????");
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
                    //??????????????????
                    audios.remove(deleteVoicePosition);
                    audioRecordAdapter.setDataList(audios);
                    audioRecordAdapter.notifyDataSetChanged();
                    //???????????????
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

    /****************************** ?????????????????? *****************************/
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
