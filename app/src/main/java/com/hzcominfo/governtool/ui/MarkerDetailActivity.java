package com.hzcominfo.governtool.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
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
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.hzcominfo.governtool.MyApplication;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.adapter.AudioRecordAdapter;
import com.hzcominfo.governtool.adapter.BaseAdapter;
import com.hzcominfo.governtool.adapter.DetailPhotoAdapter;
import com.hzcominfo.governtool.bean.AudioBean;
import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.custom.ImageLoader;
import com.hzcominfo.governtool.databinding.ActivityMarkerDetailBinding;
import com.hzcominfo.governtool.db.DaoSession;
import com.hzcominfo.governtool.db.MarkerBeanDao;
import com.hzcominfo.governtool.utils.StringUtils;
import com.hzcominfo.governtool.utils.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarkerDetailActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener, View.OnClickListener, BaseAdapter.OnItemClickListener<String> {

    private ActivityMarkerDetailBinding binding;
    private MapView mMapView = null;
    private AMap aMap;
    private UiSettings mUiSettings;
    private GeocodeSearch geocoderSearch;
    private DaoSession daoSession;
    private MarkerBean markerBean;
    private RecyclerView rvPhoto;
    private DetailPhotoAdapter adapter;
    private String addressName;
    private ArrayList<String> photos;
    private double latitude,longitude;
    private AudioRecordAdapter audioRecordAdapter;
    private RecyclerView rvAudio;
    private ArrayList<AudioBean> audios;
    private QueryBuilder<MarkerBean> queryBuilder;
    private List<String> voices; //????????????????????????????????????
    private List<String> voiceTimes; //??????????????????????????????????????????
    private String picturePath;
    private String videoPath;
    private BasePopupView playPopupView;
    private boolean isVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_marker_detail);
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        //????????????????????????
        mMapView = binding.map;
        mMapView.onCreate(savedInstanceState);
        //????????????Activity?????????????????????
        markerBean = (MarkerBean) getIntent().getSerializableExtra("MarkerBean");
        String[] split = markerBean.getLatLngs().split(",");
        latitude = Double.parseDouble(split[0]);
        longitude = Double.parseDouble(split[1]);

        init();
    }

    private void init(){
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        uiSettings(); // ??????????????????
        binding.ibBack.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);

        //????????????
        photos = new ArrayList<>();
        adapter = new DetailPhotoAdapter();
        adapter.setContext(this);
        rvPhoto = binding.rvPhoto;
        rvPhoto.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true));
        rvPhoto.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        //????????????
        audios = new ArrayList<>();
        audioRecordAdapter = new AudioRecordAdapter();
        audioRecordAdapter.setContext(this, false);
        rvAudio = binding.rvAudio;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.canScrollVertically();
        rvAudio.setLayoutManager(layoutManager);
        rvAudio.setAdapter(audioRecordAdapter);

        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMap();
                initAudio();
            }
        }).start();
    }

    /**
     * ?????????????????????
     */
    public void initMap(){
        //?????????????????????marker
        addMarker(new LatLng(latitude, longitude));
        //??????????????????????????????
        CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude, longitude), 18, 0, 30);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.moveCamera(cameraUpdate);
        //????????????????????????????????????
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        LatLonPoint latlng = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(latlng, 200, GeocodeSearch.AMAP);// ???????????????????????????Latlng????????????????????????????????????????????????????????????????????????????????????GPS???????????????
        geocoderSearch.getFromLocationAsyn(query);// ?????????????????????????????????
        //????????????
        if (StringUtils.isNotEmpty(markerBean.getDescribe())){
            binding.tvDescribe.setText(markerBean.getDescribe());
        } else {
            binding.tvDescribe.setText("????????????");
        }
        //??????????????????
        List<List<String>> lists = Arrays.asList(markerBean.getPhotos());
        List<String> strings = lists.get(0);
        if (null != strings && strings.size() > 1){
            binding.rvPhoto.setVisibility(View.VISIBLE);
            photos.clear(); //??????????????????????????????????????????
            for (int i = 1; i < strings.size(); i++) { //????????????????????????????????????1??????
                photos.add(i-1, strings.get(i));
            }
            adapter.setDataList(photos);
            adapter.notifyDataSetChanged();
        } else {
            binding.rvPhoto.setVisibility(View.GONE);
        }
    }

    /**
     * ?????????????????????
     */
    private void initAudio() {
        String latLng = latitude + "," + longitude;
        queryBuilder = daoSession.queryBuilder(MarkerBean.class).where(MarkerBeanDao.Properties.LatLngs.eq(latLng));
        List<MarkerBean> markerBeans = queryBuilder.list();
        if (markerBeans != null && markerBeans.size() > 0) {
            MarkerBean markerBean = markerBeans.get(0);
            if (null != markerBean.getVoices() && markerBean.getVoices().size() > 0) {
                voices = Arrays.asList(markerBean.getVoices()).get(0);
                voiceTimes = Arrays.asList(markerBean.getVoiceTime()).get(0);
                audios.clear(); //??????????????????????????????????????????
                for (int i = 0; i < voices.size(); i++) {
                    if (StringUtils.isEmpty(voices.get(i)) || StringUtils.isEmpty(voiceTimes.get(i))) return;
                    AudioBean audioBean = new AudioBean();
                    audioBean.setFilePath(voices.get(i));
                    audioBean.setTime(Integer.parseInt(voiceTimes.get(i)));
                    audios.add(audioBean);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        audioRecordAdapter.setDataList(audios);
                        audioRecordAdapter.notifyDataSetChanged();
                        binding.tvAudio.setVisibility(View.VISIBLE);
                    }
                });
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
     * ?????? Marker
     */
    private void addMarker(LatLng currentLatLng) {
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_position))
                .position(currentLatLng)
                .draggable(false);
        aMap.addMarker(markerOption);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back:
                markerBean.setIsNew(false);
                daoSession.update(markerBean);
                Intent intent = new Intent(MarkerDetailActivity.this, WalkInfoActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
                break;
            case R.id.btn_edit:
                markerBean.setIsNew(false);
                daoSession.update(markerBean);
                Intent intent1 = new Intent(MarkerDetailActivity.this, MarkerEditActivity.class);
                intent1.putExtra("latitude", latitude);
                intent1.putExtra("longitude", longitude);
                intent1.putExtra("class", "MarkerDetailActivity");
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.fake_anim);
                break;
        }
    }

    @Override
    public void onItemClick(String item, int position) {
        String src = photos.get(position);
        if (src.substring(src.length() - 3).equals("mp4")){ //?????????????????????
            isVideo = true;
            videoPath = src;
            playPopupView = new XPopup.Builder(MarkerDetailActivity.this)
                    .asCustom(new PlayPopup(MarkerDetailActivity.this))
                    .show();
        } else {
            isVideo = false;
            picturePath = src;
            binding.ivPicture.setVisibility(View.VISIBLE);
            new XPopup.Builder(MarkerDetailActivity.this)
                    .asImageViewer(binding.ivPicture, picturePath, new ImageLoader())
                    .isShowSaveButton(false)
                    .show();
        }
    }

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
                videoView.setOnClickListener(new View.OnClickListener() {
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
                imageView.setOnClickListener(new View.OnClickListener() {
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
//        initData();
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
            markerBean.setIsNew(false);
            daoSession.update(markerBean);
           finish();
        }
        return false;
    }
}
